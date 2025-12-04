# Terraform 배포 전 체크리스트

## 1. 파일 준비 확인
- [ ] `zip/ResizeImage.zip` 파일 존재 확인
- [ ] `zip/mysql-layer.zip` 파일 존재 확인
- [ ] `zip/ffmpeg-webp.zip` 파일 존재 확인
- [ ] `zip/ffmpeg-2.zip` 파일 존재 확인

## 2. 변수 파일 (secret.tfvars) 작성
- [ ] `my_home_ip` - 내 IP 주소 (형식: "X.X.X.X/32")
- [ ] `ec2_key_name` - EC2 키 페어 이름 (신 계정에서 미리 생성)
- [ ] `db_username` - RDS 관리자 계정
- [ ] `db_password` - RDS 비밀번호
- [ ] `s3_image_bucket` - 이미지 버킷 이름
- [ ] `s3_frontend_bucket` - 프론트엔드 버킷 이름
- [ ] `acm_certificate_arn_us` - us-east-1 ACM 인증서 ARN (CloudFront용)
- [ ] `acm_certificate_arn_seoul` - ap-northeast-2 ACM 인증서 ARN (ALB용)
- [ ] `migration_ami_id` - 구 계정 AMI ID (신 계정으로 공유 완료)
- [ ] `db_snapshot_id` - 구 계정 RDS 스냅샷 ID (신 계정으로 공유 완료)

## 3. AWS 사전 준비
- [ ] **ACM 인증서 생성**
  - us-east-1에 *.findmymeme.online 인증서
  - ap-northeast-2에 *.findmymeme.online 인증서
  - 둘 다 DNS 검증 완료
- [ ] **AMI 공유**
  - 구 계정에서 AMI 생성
  - 신 계정 계정번호로 공유
- [ ] **RDS 스냅샷 공유**
  - 구 계정에서 스냅샷 생성
  - 신 계정 계정번호로 공유
- [ ] **EC2 키 페어 생성**
  - 신 계정에서 키 페어 생성
  - .pem 파일 안전하게 보관

## 4. 코드 검증
```bash
# Terraform 초기화
terraform init

# 문법 검증
terraform validate

# 계획 확인 (실제로 무엇이 생성되는지 확인)
terraform plan -var-file="secret.tfvars"
```

## 5. 주요 설정 재확인
- [ ] **도메인 이름** - variables.tf에서 오타 없는지 확인
- [ ] **Lambda 파일명** - lambda.tf에서 filename과 source_code_hash 일치
- [ ] **CloudFront 인증서** - 반드시 us-east-1 리전 인증서 사용
- [ ] **보안 그룹 포트** - EC2 보안그룹 ingress 포트 범위 확인

## 6. 배포 실행
```bash
# 배포 (확인 후 yes 입력)
terraform apply -var-file="secret.tfvars"

# 출력 값 확인
terraform output
```

## 7. 배포 후 작업
- [ ] **Route53 DNS 설정**
  - cdn.findmymeme.online → CloudFront 도메인
  - findmymeme.online → CloudFront 도메인
  - www.findmymeme.online → CloudFront 도메인
  - api.findmymeme.online → ALB 도메인
- [ ] **S3 버킷에 데이터 업로드**
  - 구 계정에서 데이터 복사
- [ ] **Lambda 함수 테스트**
  - 콘솔에서 테스트 이벤트 실행
- [ ] **EC2 접속 테스트**
  - SSH 접속 확인
  - 애플리케이션 실행

## 8. 트러블슈팅 팁
- CloudFront 배포는 10-15분 소요 (기다려야 함)
- RDS는 스냅샷에서 복원하므로 10-20분 소요
- VPC 리소스가 먼저 생성되어야 다른 리소스 생성 가능

## 9. 비용 확인
배포 후 예상 비용:
- EC2 t3.micro: ~$8/월
- RDS db.t3.micro: ~$15/월
- ElastiCache t3.micro: ~$12/월
- CloudFront: 트래픽 따라 다름
- S3: 저장량 따라 다름
- **총 약 $35-50/월**
