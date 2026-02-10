# AWS 계정 마이그레이션 가이드

## 개요
구계정(A)에서 신계정(B)으로 AWS 인프라 마이그레이션 절차

---

## 1. 사전 준비

### 1.0 구계정 CloudFront CNAME 삭제 (필수!)

> **중요:** CloudFront의 대체 도메인(CNAME)은 전 세계적으로 고유해야 함.
> 구계정에서 삭제하지 않으면 신계정에서 같은 도메인으로 CloudFront 생성 불가

**삭제할 CloudFront 배포:**
- 프론트엔드 CDN: `findmymeme.online`, `*.findmymeme.online`
- 이미지 CDN: `cdn.findmymeme.online`

**방법: 배포 전체 삭제**
1. CloudFront → 배포 선택 → 비활성화
2. 상태가 "Deployed"로 변경되면 → 삭제

---

### 1.1 IAM 사용자 생성 (신계정)
- AWS 콘솔 → IAM → 사용자 → 사용자 생성
- 권한: `AdministratorAccess` (또는 필요한 권한만)
- Access Key 생성 → 저장

### 1.2 AWS CLI 프로필 설정
```bash
# ~/.aws/credentials
[new]
aws_access_key_id = AKIA...
aws_secret_access_key = ...

[old]
aws_access_key_id = AKIA...
aws_secret_access_key = ...
```

### 1.3 EC2 키페어 생성
- AWS 콘솔 → EC2 → 키 페어 → 생성
- `.pem` 파일 안전하게 저장

### 1.4 ACM 인증서 발급 (Certificate Manager)
- **서울 리전 (ap-northeast-2)**: ALB용
  - 도메인: `findmymeme.online`, `*.findmymeme.online`
- **버지니아 리전 (us-east-1)**: CloudFront용
  - 도메인: `findmymeme.online`, `*.findmymeme.online`
- DNS 검증 완료 확인 (Route53에서 레코드 자동 생성)

### 1.5 Route53 호스팅 영역 생성
- 신계정에 호스팅 영역 생성
- NS 레코드를 도메인 등록기관(Namecheap 등)에 업데이트
- **주의:** NS 레코드 복사 시 마지막 `.` 제외하고 붙여넣기

---

## 2. Terraform 설정

### 2.1 State 파일 초기화 (중요!)
구계정 리소스 참조하는 state 삭제:
```powershell
cd terraform
Remove-Item -Recurse -Force .terraform
Remove-Item -Force terraform.tfstate -ErrorAction SilentlyContinue
Remove-Item -Force terraform.tfstate.backup -ErrorAction SilentlyContinue
terraform init
```

### 2.2 terraform.tfvars 업데이트
```hcl
ec2_key_name = "신계정-키페어-이름"

# S3 버킷명 (글로벌 유니크해야 함 - 구계정과 다른 이름 필요)
s3_image_bucket    = "my-app-image-v2"
s3_frontend_bucket = "my-app-frontend-v2"

# 신계정 ACM 인증서 ARN
acm_certificate_arn_seoul = "arn:aws:acm:ap-northeast-2:신계정ID:certificate/..."
acm_certificate_arn_us    = "arn:aws:acm:us-east-1:신계정ID:certificate/..."

```

### 2.3 Terraform 적용
```bash
terraform plan
terraform apply
```

---

## 3. S3 데이터 마이그레이션

### 로컬 경유 방식 (크로스 계정 권한 문제 회피)
```bash
# 구계정 → 로컬
aws s3 sync s3://구계정-이미지버킷 ./images --profile old
aws s3 sync s3://구계정-프론트버킷 ./front --profile old

# 로컬 → 신계정
aws s3 sync ./images s3://신계정-이미지버킷 --profile new
aws s3 sync ./front s3://신계정-프론트버킷 --profile new

# 임시 폴더 삭제
Remove-Item -Recurse -Force ./images, ./front
```

---

## 4. RDS 데이터 마이그레이션

### 4.1 구계정 RDS 접속 준비 (스냅샷 복원 시)

퍼블릭 접근 가능하게 복원:
1. RDS → 스냅샷 → 스냅샷에서 복원
2. **VPC**: Default VPC 선택 (커스텀 VPC는 private 서브넷이라 외부 접속 불가)
3. **퍼블릭 액세스**: 예
4. 복원 후 **보안 그룹 인바운드**: `3306 | 0.0.0.0/0` 추가

### 4.2 MySQL Workbench로 Export
1. 구계정 RDS 접속
2. **Server → Data Export**
3. findmymeme_db schema만 선택 (sys, mysql 등 제외)
4. **Export to Self-Contained File** 선택
5. **Advanced Options**:
   - `set-gtid-purged` → **OFF**
   - Dump Triggers/Routines/Events → 체크 해제
6. Start Export

### 4.3 신계정 RDS 접속 (SSH 터널링)
MySQL Workbench 새 연결:
- **Connection Method**: `Standard TCP/IP over SSH`
- **SSH Hostname**: 신계정-EC2-IP:22
- **SSH Username**: ubuntu (또는 ec2-user)
- **SSH Key File**: 신계정-키페어.pem
- **MySQL Hostname**: 신계정-RDS-엔드포인트
- **MySQL Port**: 3306
- **Username/Password**: DB 자격증명

### 4.4 Import
1. 신계정 RDS 접속
2. 스키마 생성:
   ```sql
   CREATE DATABASE findmymeme_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. **Server → Data Import**
4. **Import from Self-Contained File** 선택
5. **Default Target Schema**: 생성한 스키마 선택
6. Start Import

### 4.5 RDS 접속 에러 대응

**"Access denied; SUPER privilege" 에러:**
- Export 시 `set-gtid-purged=OFF` 옵션 확인
- 또는 dump 파일에서 해당 라인 삭제

**"Lost connection" 에러:**
- EC2 → RDS 보안 그룹 확인 (3306 포트 허용)
- RDS 상태가 "사용 가능"인지 확인

---

## 5. 애플리케이션 설정 업데이트

### 5.1 변경해야 할 엔드포인트
백엔드 환경변수
- AWS_ACCESS_KEY
- AWS_BUCKET
- AWS_SECRET_KEY
- RDS 엔드포인트
- Redis (ElastiCache) 엔드포인트
- S3 버킷명
- IMAGE_COMPLETION_QUEUE (SQS URL)

프론트엔드 환경변수 
- CloudFront 도메인 (CDN URL)

### 5.2 엔드포인트 확인
```bash
terraform output
```

---

## 6. GitHub Actions 업데이트

### 6.1 변경할 Secrets
| Secret | 설명 |
|--------|------|
| EC2_HOST | 신계정 EC2 퍼블릭 IP |
| SSH_PRIVATE_KEY | 신계정 키페어 private key |
| DB_HOST | 신계정 RDS 엔드포인트 |
| REDIS_HOST | 신계정 Redis 엔드포인트 |
| AWS_ACCESS_KEY | 신계정 IAM Access Key |
| AWS_SECRET_KEY | 신계정 IAM Secret Key |
| AWS_BUCKET | 신계정 S3 버킷명 |
| IMAGE_COMPLETION_QUEUE | 신계정 SQS URL |

### 6.2 (선택) .env 통합 관리
모든 환경변수를 하나의 Secret으로:
```yaml
# deploy.yml
script: |
  echo "${{ secrets.ENV_FILE }}" > .env
  docker-compose up -d
```

---

## 7. 배포 및 확인

```bash
# GitHub Actions 트리거 또는 수동 배포 후
# Health Check
curl https://api.yourdomain.com/health

# ALB Target Group 상태 확인
AWS 콘솔 → EC2 → Target Groups → Health Status
```

---

## 8. 정리 작업

### 8.1 구계정 리소스 삭제 (비용 절감)

**삭제 순서 (의존성 고려):**

1. **EC2 관련**
   - [ ] EC2 인스턴스 종료
   - [ ] EIP (탄력적 IP) 릴리스
   - [ ] 대상 그룹 삭제
   - [ ] ALB 삭제

2. **데이터베이스**
   - [ ] 임시 RDS 인스턴스 삭제 (스냅샷 복원한 것)
   - [ ] 원본 RDS 인스턴스 삭제 (최종 스냅샷 생성 권장)
   - [ ] ElastiCache (Redis) 삭제

3. **스토리지 & CDN**
   - [ ] CloudFront 배포 비활성화 → 삭제
   - [ ] S3 버킷 비우기 → 삭제

4. **네트워크**
   - [ ] NAT Gateway 삭제 (비용 높음!)
   - [ ] VPC 삭제 (서브넷, 보안그룹 자동 삭제)

5. **기타**
   - [ ] Lambda 함수 삭제
   - [ ] SQS 큐 삭제
   - [ ] CloudWatch 로그 그룹 삭제
   - [ ] IAM 사용자/역할 정리

**비용 확인:**
```
AWS 콘솔 → Billing → Bills
```

---

### 8.2 신계정 보안 정리

- [ ] RDS 보안 그룹에서 `0.0.0.0/0` 제거 (임시로 열었다면)
- [ ] 불필요한 퍼블릭 액세스 차단
- [ ] IAM 사용자 최소 권한 원칙 적용

> **참고:** EC2 SSH(22) 포트는 GitHub Actions 배포를 위해 `0.0.0.0/0` 유지 필요

---

### 8.3 신계정에서 AMI생성
혹시 모를 대비를 위해 AMI를 신계정에서 생성하고 terraform.tfvars 아래 수정. 구계정에 공유한 ami 제거하기 위해서 
migration_ami_id = "ami-..."

## 체크리스트

### 사전 준비
- [ ] 구계정 CloudFront CNAME 삭제 (도메인 충돌 방지)
- [ ] 신계정 IAM 사용자 생성 및 CLI 설정
- [ ] 신계정 EC2 키페어 생성
- [ ] 신계정 ACM 인증서 발급 (서울 + 버지니아)
- [ ] 신계정 Route53 호스팅 영역 생성 및 NS 레코드 업데이트

### 인프라 생성
- [ ] Terraform state 초기화
- [ ] terraform.tfvars 업데이트
- [ ] terraform apply

### 데이터 이전
- [ ] S3 데이터 이전
- [ ] RDS 데이터 이전

### 애플리케이션 배포
- [ ] application.yml / 환경변수 업데이트
- [ ] GitHub Actions Secrets 업데이트
- [ ] 배포 및 테스트

### 정리
- [ ] 구계정 리소스 삭제 (비용 절감)

---

*작성일: 2026-02-10*
