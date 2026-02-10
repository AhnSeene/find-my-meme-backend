
output "service_url" {
  description = "웹 서비스 접속 주소 (ALB)"
  value       = "https://${aws_lb.main.dns_name}"
}

output "s3_frontend_bucket_name" {
  description = "Github Actions에서 S3 Sync 할 버킷 이름"
  value       = aws_s3_bucket.frontend_bucket.id
}

output "cloudfront_distribution_id" {
  description = "Github Actions에서 캐시 무효화(Invalidation) 할 배포 ID"
  value       = aws_cloudfront_distribution.frontend_cdn.id
}

output "ec2_public_ip" {
  description = "Github Actions에서 SSH 접속할 서버 IP"
  value       = aws_instance.web.public_ip
}

output "ec2_ssh_command" {
  description = "SSH 접속 명령어 미리보기"
  value       = "ssh -i ${var.ec2_key_name}.pem ec2-user@${aws_instance.web.public_ip}"
}

output "rds_endpoint" {
  description = "RDS Host"
  value       = aws_db_instance.default.address
}

output "redis_endpoint" {
  description = "Redis Host 주소"
  value       = aws_elasticache_replication_group.redis.primary_endpoint_address
}

output "sqs_image_queue_url" {
  description = "이미지 처리 완료 SQS URL (IMAGE_COMPLETION_QUEUE)"
  value       = aws_sqs_queue.image_queue.url
}

output "s3_image_bucket_name" {
  description = "이미지 S3 버킷명 (AWS_BUCKET)"
  value       = aws_s3_bucket.image_bucket.id
}