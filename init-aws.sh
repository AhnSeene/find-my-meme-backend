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

awslocal sqs create-queue --queue-name local-image-complete-queue
echo "SQS queue 'local-image-complete-queue' created."