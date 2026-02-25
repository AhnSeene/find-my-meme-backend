# 계층 1: MySQL Layer
resource "aws_lambda_layer_version" "mysql_layer" {
  filename   = "${path.module}/zip/mysql-layer.zip"
  layer_name = "mysql-layer"

  compatible_runtimes = ["nodejs18.x", "nodejs22.x"]

  source_code_hash = filebase64sha256("${path.module}/zip/mysql-layer.zip")
}

# 계층 2: FFmpeg WebP Layer
resource "aws_lambda_layer_version" "ffmpeg_webp_layer" {
  filename   = "${path.module}/zip/ffmpeg-webp.zip"
  layer_name = "ffmpeg-webp-layer"

  compatible_runtimes = ["nodejs18.x", "nodejs22.x"]

  source_code_hash = filebase64sha256("${path.module}/zip/ffmpeg-webp.zip")
}

# 계층 3: FFmpeg 2 Layer
resource "aws_lambda_layer_version" "ffmpeg_2_layer" {
  filename   = "${path.module}/zip/ffmpeg-2.zip"
  layer_name = "ffmpeg-2-layer"

  compatible_runtimes = ["nodejs18.x", "nodejs22.x"]

  source_code_hash = filebase64sha256("${path.module}/zip/ffmpeg-2.zip")
}

resource "aws_lambda_function" "resize_function" {
  filename      = "${path.module}/zip/ResizeImage.zip"
  function_name = "ResizeImage"

  role          = aws_iam_role.lambda_resize_role.arn

  handler       = "index.handler"
  runtime       = "nodejs22.x"

  layers = [
    aws_lambda_layer_version.mysql_layer.arn,
    aws_lambda_layer_version.ffmpeg_webp_layer.arn,
    aws_lambda_layer_version.ffmpeg_2_layer.arn
  ]

  environment {
    variables = {
      SQS_QUEUE_URL  = aws_sqs_queue.image_queue.url
      IMAGE_BUCKET   = aws_s3_bucket.image_bucket.id
    }
  }
  timeout     = 300 # 5분 
  memory_size = 3008

  source_code_hash = filebase64sha256("${path.module}/zip/ResizeImage.zip")

  tags = { Name = "${var.project_name}-lambda" }
}

# SQS → Lambda 이벤트 소스 매핑
resource "aws_lambda_event_source_mapping" "sqs_to_lambda" {
  event_source_arn = aws_sqs_queue.image_input_queue.arn
  function_name    = aws_lambda_function.resize_function.arn
  batch_size       = 1 # 한 번에 1개 메시지 처리
  enabled          = true

  # 실패 시 설정
  function_response_types = ["ReportBatchItemFailures"] # 부분 실패 지원
}

/*
테스트 이벤트
{
  "memePostId": 124,
  "userId":10,
  "s3ObjectKey": "images/memes/2025/07/26e7d36e-8e1d-4aef-9c80-b11d89909536.gif"
}
 */