variable "aws_seoul_region" {
  description = "AWS seoul region"
  type = string
  default = "ap-northeast-2"
}

variable "aws_virginia_region" {
  description = "AWS virginia region"
  type = string
  default = "us-east-1"
}

variable "project_name" {
  description = "Project name Prefix"
  type = string
  default = "findmymeme"
}

variable "domain_name" {
  description = "Project domain name"
  type = string
  default = "findmymeme.online"
}

/*
vpc 설정
 */
variable "vpc_cidr_block" {
  description = "CIDR block for VPC"
  type = string
  default = "10.0.0.0/16"
}

variable "vpc_azs" {
  description = "Availability zones for VPC"
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2c"]
}

variable "vpc_public_subnets_cidr" {
  description = "Public Subnet CIDR 목록"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "vpc_private_subnets_cidr" {
  description = "Private Subnet CIDR 목록"
  type        = list(string)
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
}

/*
리소스 설정
 */
variable "ec2_instance_type" {
  description = "ec2 instance's type"
  type = string
  default = "t3.micro"
}

variable "my_home_ip" {
  description = "my ip for ssh access"
  type = string
}

variable "ec2_key_name" {
  description = "AWS 콘솔에서 미리 만들어둔 EC2 키 페어 이름"
  type        = string
}

variable "db_username" {
  description = "Database administrator username"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Database administrator password"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "DB instance type"
  type = string
  default = "db.t3.micro"
}

variable "db_mysql_engine_version" {
  description = "DB mysql version"
  type = string
  default = "8.0.40"
}

variable "db_mysql_allocated_storage" {
  description = "DB allocated storage"
  type = number
  default = 20
}

variable "ec2_health_check" {
  description = "ec2 health_check for ALB target group"
  type = string
  default = "/"
}

variable "ec2_port" {
  description = "ec2 port"
  type = number
  default = 8080
}

variable "s3_image_bucket" {
  description = "s3 image bucket"
  type = string
}

variable "s3_frontend_bucket" {
  description = "s3 frontend bucket"
  type = string
}

variable "acm_certificate_arn_seoul" {
  description = "acm certificate arn"
  type = string
}


variable "acm_certificate_arn_us" {
  description = "acm certificate arn"
  type = string
}

/*
마이그레션용 변수
 */
variable "migration_ami_id" {
  description = "AMI ID from old account"
  type = string
}

variable "db_snapshot_id" {
  description = "dp snapshot arn from old account"
  type = string
}
