# =========================================================================
# 0. 공통 데이터 소스 (AWS 관리형 정책 조회)
# =========================================================================
data "aws_cloudfront_cache_policy" "caching_optimized" {
  name = "Managed-CachingOptimized"
}

data "aws_cloudfront_origin_request_policy" "all_viewer_except_host_header" {
  name = "Managed-AllViewerExceptHostHeader" # CORS 헤더 전달용
}

# =========================================================================
# 1. 이미지 (Assets) 관련 리소스
# =========================================================================

# 1-1. S3 버킷
resource "aws_s3_bucket" "image_bucket" {
  bucket = var.s3_image_bucket
}

# 1-2. 퍼블릭 액세스 차단 (OAC 사용 시 필수)
resource "aws_s3_bucket_public_access_block" "image_block" {
  bucket = aws_s3_bucket.image_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# 1-3. CORS 설정
resource "aws_s3_bucket_cors_configuration" "image_cors" {
  bucket = aws_s3_bucket.image_bucket.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["PUT", "GET"]
    allowed_origins = [
      "http://localhost:3000",
      "https://${var.domain_name}",
      "https://www.${var.domain_name}"
    ]
    expose_headers  = ["ETag"]
  }
}

# 1-4. 수명주기 규칙
resource "aws_s3_bucket_lifecycle_configuration" "image_lifecycle" {
  bucket = aws_s3_bucket.image_bucket.id

  rule {
    id     = "temp-dir-life-cycle"
    status = "Enabled"
    filter {
      prefix = "temps/"
    }
    expiration {
      days = 1
    }
  }
}

# 1-5. CloudFront OAC (보안 설정)
resource "aws_cloudfront_origin_access_control" "image_oac" {
  name                              = "image-oac-policy"
  description                       = "S3 Image Security Policy"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# 1-6. CloudFront 배포
resource "aws_cloudfront_distribution" "image_cdn" {
  aliases = ["cdn.${var.domain_name}"] # images -> cdn 으로 변경됨 확인
  enabled = true

  # 원본: S3 버킷 도메인 사용
  origin {
    domain_name              = aws_s3_bucket.image_bucket.bucket_regional_domain_name
    origin_id                = "S3-Image-Origin"
    origin_access_control_id = aws_cloudfront_origin_access_control.image_oac.id
  }

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-Image-Origin"

    cache_policy_id          = data.aws_cloudfront_cache_policy.caching_optimized.id
    origin_request_policy_id = data.aws_cloudfront_origin_request_policy.all_viewer_except_host_header.id

    viewer_protocol_policy = "redirect-to-https"
  }

  viewer_certificate {
    acm_certificate_arn      = var.acm_certificate_arn_us
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2021"
  }

  restrictions {
    geo_restriction { restriction_type = "none" }
  }

  tags = { Name = "${var.project_name}-image-cdn" }
}

# 1-7. S3 버킷 정책 (CloudFront 생성 후 자동 연결)
resource "aws_s3_bucket_policy" "image_policy" {
  bucket = aws_s3_bucket.image_bucket.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipal",
        Effect    = "Allow",
        Principal = { Service = "cloudfront.amazonaws.com" },
        Action    = "s3:GetObject",
        Resource  = "${aws_s3_bucket.image_bucket.arn}/*",
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.image_cdn.arn
          }
        }
      }
    ]
  })
}


# =========================================================================
# 2. 프론트엔드 (Frontend) 관련 리소스
# =========================================================================

# 2-1. S3 버킷
resource "aws_s3_bucket" "frontend_bucket" {
  bucket = var.s3_frontend_bucket
}

# 2-2. 퍼블릭 액세스 차단
resource "aws_s3_bucket_public_access_block" "frontend_block" {
  bucket = aws_s3_bucket.frontend_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# 2-3. CloudFront OAC (프론트엔드용 별도 생성)
resource "aws_cloudfront_origin_access_control" "frontend_oac" {
  name                              = "frontend-oac-policy"
  description                       = "S3 Frontend Security Policy"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# 2-4. CloudFront 배포 (Website Endpoint 대신 S3 Bucket 직접 참조)
resource "aws_cloudfront_distribution" "frontend_cdn" {
  aliases = [var.domain_name, "www.${var.domain_name}"]
  enabled = true
  default_root_object = "index.html"

  # [중요 변경] 원본을 S3 Website Endpoint가 아닌 '버킷 자체'로 변경
  origin {
    domain_name              = aws_s3_bucket.frontend_bucket.bucket_regional_domain_name
    origin_id                = "S3-Frontend-Origin"
    origin_access_control_id = aws_cloudfront_origin_access_control.frontend_oac.id
  }

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-Frontend-Origin"

    cache_policy_id = data.aws_cloudfront_cache_policy.caching_optimized.id

    viewer_protocol_policy = "redirect-to-https"
  }

  # SPA 라우팅 문제 해결 (403/404 -> index.html)
  custom_error_response {
    error_code            = 403
    response_code         = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 10
  }
  custom_error_response {
    error_code            = 404
    response_code         = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 10
  }

  viewer_certificate {
    acm_certificate_arn      = var.acm_certificate_arn_us
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2021"
  }

  restrictions {
    geo_restriction { restriction_type = "none" }
  }

  tags = { Name = "${var.project_name}-frontend-cdn" }
}

# 2-5. S3 버킷 정책 (CloudFront 생성 후 자동 연결)
resource "aws_s3_bucket_policy" "frontend_policy" {
  bucket = aws_s3_bucket.frontend_bucket.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipal",
        Effect    = "Allow",
        Principal = { Service = "cloudfront.amazonaws.com" },
        Action    = "s3:GetObject",
        Resource  = "${aws_s3_bucket.frontend_bucket.arn}/*",
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.frontend_cdn.arn
          }
        }
      }
    ]
  })
}

/*
구계정 정책

image는 아래의 버킷정책 + 모든 퍼블릭 엑세스 차단
{
    "Version": "2008-10-17",
    "Id": "PolicyForCloudFrontPrivateContent",
    "Statement": [
        {
            "Sid": "AllowCloudFrontServicePrincipal",
            "Effect": "Allow",
            "Principal": {
                "Service": "cloudfront.amazonaws.com"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::find-my-meme/*",
            "Condition": {
                "StringEquals": {
                    "AWS:SourceArn": "arn:aws:cloudfront::365074501938:distribution/EU260ILHM4VXZ"
                }
            }
        }
    ]
}\

cors
[
    {
        "AllowedHeaders": [
            "*"
        ],
        "AllowedMethods": [
            "PUT",
            "GET"
        ],
        "AllowedOrigins": [
            "http://localhost:3000",
            "https://findmymeme.online"
        ],
        "ExposeHeaders": [
            "ETag"
        ]
    }
]

수명주기도 하나 있음
temp-dir-life-cycle
접두사 temps/
규칙범위 선택
하나 이상의 필터를 사용하여 이 규칙의 범위 제한
수명 주기 규칙 작업: 객체의 현재 버전 만료
객체 생성 후 경과 일수 1


front는 아래의 버킷정책 + 모든 퍼블릭 엑세스 차단
{
    "Version": "2012-10-17",
    "Id": "Policy1740304477196",
    "Statement": [
        {
            "Sid": "Stmt1740304473679",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::find-my-meme-front/*"
        },
        {
            "Sid": "AllowCloudFrontServicePrincipal",
            "Effect": "Allow",
            "Principal": {
                "Service": "cloudfront.amazonaws.com"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::find-my-meme-front/*",
            "Condition": {
                "ArnLike": {
                    "AWS:SourceArn": "arn:aws:cloudfront::365074501938:distribution/E1MJPB5691YJ86"
                }
            }
        }
    ]
}
 */