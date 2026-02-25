#!/bin/bash
echo "Initializing LocalStack..."

awslocal s3 mb s3://find-my-meme

echo "S3 bucket 'find-my-meme' created."

awslocal iam create-role \
  --role-name lambda-role \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'
echo "IAM role 'lambda-role' created."

# Mock Lambda 함수 생성을 위한 더미 코드 준비
mkdir -p /tmp/lambda-dummy
echo 'def handler(event, context): return {"statusCode": 200, "body": "Mock Lambda executed!"}' > /tmp/lambda-dummy/index.py
zip -j /tmp/lambda-dummy/dummy-handler.zip /tmp/lambda-dummy/index.py

# Mock Lambda 함수 생성
awslocal lambda create-function \
  --function-name local-image-resizer \
  --region ap-northeast-2 \
  --runtime python3.9 \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --handler index.handler \
  --zip-file fileb:///tmp/lambda-dummy/dummy-handler.zip

echo "Mock Lambda function 'local-image-resizer' created."

# SQS 입력 큐 생성 (Server → SQS → Lambda)
awslocal sqs create-queue --queue-name local-image-input-queue-dlq
echo "SQS DLQ 'local-image-input-queue-dlq' created."

awslocal sqs create-queue \
  --queue-name local-image-input-queue \
  --attributes '{
    "VisibilityTimeout": "330",
    "RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:ap-northeast-2:000000000000:local-image-input-queue-dlq\",\"maxReceiveCount\":\"3\"}"
  }'
echo "SQS queue 'local-image-input-queue' created."

# SQS 완료 큐 생성 (Lambda → SQS → Server)
awslocal sqs create-queue --queue-name local-image-complete-queue-dlq
echo "SQS DLQ 'local-image-complete-queue-dlq' created."

awslocal sqs create-queue \
  --queue-name local-image-complete-queue \
  --attributes '{
    "VisibilityTimeout": "30",
    "RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:ap-northeast-2:000000000000:local-image-complete-queue-dlq\",\"maxReceiveCount\":\"5\"}"
  }'
echo "SQS queue 'local-image-complete-queue' created."

# Lambda → SQS 이벤트 소스 매핑
awslocal lambda create-event-source-mapping \
  --function-name local-image-resizer \
  --event-source-arn arn:aws:sqs:ap-northeast-2:000000000000:local-image-input-queue \
  --batch-size 1 \
  --enabled
echo "Lambda event source mapping created (SQS → Lambda)."