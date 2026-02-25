# ========================================
# Lambda 입력 큐 (Server → SQS → Lambda)
# ========================================
resource "aws_sqs_queue" "image_input_dlq" {
  name                      = "image-processing-input-dlq"
  delay_seconds             = 0
  max_message_size          = 262144 # 256KB
  message_retention_seconds = 1209600 # 14일
  receive_wait_time_seconds = 20

  sqs_managed_sse_enabled = true

  tags = { Name = "${var.project_name}-image-input-dlq" }
}

resource "aws_sqs_queue" "image_input_queue" {
  name                       = "image-processing-input-queue"
  delay_seconds              = 0
  max_message_size           = 262144 # 256KB
  message_retention_seconds  = 1209600 # 14일
  receive_wait_time_seconds  = 20
  visibility_timeout_seconds = 330 # Lambda timeout (300s) + 30s 여유

  sqs_managed_sse_enabled = true

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.image_input_dlq.arn
    maxReceiveCount     = 3 # 3회 실패 시 DLQ로 이동
  })

  tags = { Name = "${var.project_name}-image-input-queue" }
}

# ========================================
# Lambda 출력 큐 (Lambda → SQS → Server)
# ========================================
resource "aws_sqs_queue" "image_dlq" {
  name                      = "image-processing-complete-dlq"
  delay_seconds             = 0
  max_message_size          = 262144 # 256KB
  message_retention_seconds = 1209600 # 14일
  receive_wait_time_seconds = 20

  sqs_managed_sse_enabled = true

  tags = { Name = "${var.project_name}-image-complete-dlq" }
}

resource "aws_sqs_queue" "image_queue" {
  name                      = "image-processing-complete-queue"
  delay_seconds             = 0
  max_message_size = 262144   # 256KB
  message_retention_seconds = 1209600  # 14일
  receive_wait_time_seconds = 20
  visibility_timeout_seconds = 30 # 서버 처리 시간 짧음 (DB 업데이트만)

  sqs_managed_sse_enabled = true

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.image_dlq.arn
    maxReceiveCount     = 5 # 일시적 실패 대응 (네트워크, DB)
  })

  tags = { Name = "${var.project_name}-image-complete-queue" }
}
