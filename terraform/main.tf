terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
  required_version = ">= 1.2"
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
