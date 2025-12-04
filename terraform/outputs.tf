# ==========================================
# 1. 접속 정보 (개발자가 브라우저로 확인)
# ==========================================

output "service_url" {
  description = "웹 서비스 접속 주소 (ALB)"
  value       = "https://${aws_lb.main.dns_name}"
}

# ==========================================
# 2. Github Actions용: 프론트엔드 배포 정보
# ==========================================

output "s3_frontend_bucket_name" {
  description = "Github Actions에서 S3 Sync 할 버킷 이름"
  value       = aws_s3_bucket.frontend_bucket.id
}

output "cloudfront_distribution_id" {
  description = "Github Actions에서 캐시 무효화(Invalidation) 할 배포 ID"
  # 주의: CloudFront를 수동으로 만들었다면 이 output은 비워두거나,
  # cdn.tf를 사용할 때만 주석을 푸세요.
  # value       = aws_cloudfront_distribution.frontend_cdn.id
  value = "수동 생성한 CloudFront ID를 Github Secrets에 직접 넣으세요"
}

# ==========================================
# 3. Github Actions용: 백엔드 배포 정보
# ==========================================

output "ec2_public_ip" {
  description = "Github Actions에서 SSH 접속할 서버 IP"
  value       = aws_instance.web.public_ip
}

output "ec2_ssh_command" {
  description = "SSH 접속 명령어 미리보기"
  value       = "ssh -i ${var.ec2_key_name}.pem ec2-user@${aws_instance.web.public_ip}"
}

# ==========================================
# 4. 애플리케이션 설정용 (DB 연결 정보)
# ==========================================

output "rds_endpoint" {
  description = "RDS Host"
  value       = aws_db_instance.default.address
}

output "redis_endpoint" {
  description = "Redis Host 주소"
  value       = aws_elasticache_replication_group.redis.primary_endpoint_address
}