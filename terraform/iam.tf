/*
Lambda IAM
 */
resource "aws_iam_role" "lambda_resize_role" {
  name = "${var.project_name}-lambda-resize-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
    }]
  })
}

# 람다 정책 생성 (S3 접근 + SQS 메시지 전송 통합)
resource "aws_iam_policy" "lambda_custom_policy" {
  name = "${var.project_name}-lambda-policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = ["s3:PutObject", "s3:GetObject"]
        Resource = "${aws_s3_bucket.image_bucket.arn}/*"
      },
      {
        Effect = "Allow"
        Action = "sqs:SendMessage"
        Resource = aws_sqs_queue.image_queue.arn
      },
      {
        Effect = "Allow"
        Action = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:PutLogEvents"]
        Resource = "arn:aws:logs:*:*:*"
      }
    ]
  })
}


resource "aws_iam_role_policy_attachment" "lambda_custom_attach" {
  role       = aws_iam_role.lambda_resize_role.name
  policy_arn = aws_iam_policy.lambda_custom_policy.arn
}

# resource "aws_iam_role_policy_attachment" "lambda_vpc_attach" {
#   role       = aws_iam_role.lambda_resize_role.name
#   policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
# }

/*
EC2 IAM
 */
resource "aws_iam_role" "ec2_app_role" {
  name = "${var.project_name}-ec2-app-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

# EC2 정책 (SQS 소비 + Lambda 호출 + S3 접근)
resource "aws_iam_policy" "ec2_custom_policy" {
  name = "${var.project_name}-ec2-policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ],
        Resource = aws_sqs_queue.image_queue.arn
      },
      {
        Effect = "Allow"
        Action = "lambda:InvokeFunction",
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = ["s3:GetObject", "s3:ListBucket", "s3:PutObject"],
        Resource = [
          aws_s3_bucket.image_bucket.arn,
          "${aws_s3_bucket.image_bucket.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ec2_custom_attach" {
  role       = aws_iam_role.ec2_app_role.name
  policy_arn = aws_iam_policy.ec2_custom_policy.arn
}

# SSM 접속용 (SSH 키 없이 콘솔 접속 가능)
resource "aws_iam_role_policy_attachment" "ec2_ssm_attach" {
  role       = aws_iam_role.ec2_app_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "${var.project_name}-ec2-profile"
  role = aws_iam_role.ec2_app_role.name
}


/*

구계정 정책
LambdaS3Policy
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject"
            ],
            "Resource": "arn:aws:s3:::find-my-meme/*"
        }
    ]
}


InvokeLambda-ResizeImage
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": "lambda:InvokeFunction",
            "Resource": "arn:aws:lambda:ap-northeast-2:365074501938:function:ResizeImage"
        }
    ]
}


image-processing-complete-queue-sendMessage
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": "sqs:SendMessage",
            "Resource": "arn:aws:sqs:ap-northeast-2:365074501938:image-processing-complete-queue"
        }
    ]
}

image-complete-queue-consummer
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage",
                "sqs:GetQueueAttributes"
            ],
            "Resource": "arn:aws:sqs:ap-northeast-2:365074501938:image-processing-complete-queue"
        }
    ]
}
 */

/*
역할
LambdaS3Role
- LambdaS3Policy
- image-processing-complete-queue-sendMessage
- AWSLambdaVPCAccessExecutionRole


rds-monitoring-role
- AmazonRDSEnhancedMonitoringRole
 */


/*
사용자 acycoco
- InvokeLambda-ResizeImage
- image-complete-queue-consummer
- IAMUserChangePassword
- CloudFrontFullAccess
- AmazonS3FullAccess
- AdministratorAccess
 */

