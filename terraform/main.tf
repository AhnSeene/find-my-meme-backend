terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
  required_version = ">= 1.2"

  backend "s3" {
    bucket  = "findmymeme-terraform-state"
    key     = "terraform.tfstate"
    region  = "ap-northeast-2"
    profile = "new"
  }
}

provider "aws" {
  region  = var.aws_seoul_region
  profile = "new"
}

provider "aws" {
  alias  = "virginia"
  region = var.aws_virginia_region
  profile = "new"
}

# Terraform State 저장용 S3 버킷
resource "aws_s3_bucket" "terraform_state" {
  bucket = "${var.project_name}-terraform-state"

  lifecycle {
    prevent_destroy = true
  }

  tags = { Name = "${var.project_name}-terraform-state" }
}

resource "aws_s3_bucket_versioning" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_public_access_block" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
