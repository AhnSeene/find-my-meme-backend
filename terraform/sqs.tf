resource "aws_sqs_queue" "image_dlq" {
  name                      = "image-processing-complete-dlq"
  delay_seconds             = 0
  max_message_size          = 262144 # 256KB
  message_retention_seconds = 1209600 # 14일
  receive_wait_time_seconds = 20

  sqs_managed_sse_enabled = true

  tags = { Name = "${var.project_name}-image-dlq" }
}

resource "aws_sqs_queue" "image_queue" {
  name                      = "image-processing-complete-queue"
  delay_seconds             = 0
  max_message_size = 262144   # 256KB
  message_retention_seconds = 1209600  # 14일
  receive_wait_time_seconds = 20
  visibility_timeout_seconds = 30

  sqs_managed_sse_enabled = true

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.image_dlq.arn
    maxReceiveCount     = 5
  })

  tags = { Name = "${var.project_name}-image-sqs" }
}
