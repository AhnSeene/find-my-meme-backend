resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr_block
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = { Name = "${var.project_name}-vpc" }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = { Name = "${var.project_name}-igw" }
}

resource "aws_subnet" "public_subnet" {
  count = length(var.vpc_public_subnets_cidr)
  vpc_id = aws_vpc.main.id
  cidr_block = var.vpc_public_subnets_cidr[count.index]
  availability_zone = var.vpc_azs[count.index]
  map_public_ip_on_launch = true

  tags = { Name = "${var.project_name}-public-${count.index + 1}" }
}

resource "aws_subnet" "private_subnet" {
  count = length(var.vpc_private_subnets_cidr)
  vpc_id = aws_vpc.main.id
  cidr_block = var.vpc_private_subnets_cidr[count.index]
  availability_zone = var.vpc_azs[count.index]

  tags = { Name = "${var.project_name}-private-${count.index + 1}" }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = { Name = "${var.project_name}-public-rt" }
}

resource "aws_route_table_association" "public" {
  count = length(var.vpc_public_subnets_cidr)
  subnet_id = aws_subnet.public_subnet[count.index].id
  route_table_id = aws_route_table.public.id
}

# S3 Gateway Endpoint (무료, VPC 내부 경로로 S3 접근)
resource "aws_vpc_endpoint" "s3" {
  vpc_id       = aws_vpc.main.id
  service_name = "com.amazonaws.${var.aws_seoul_region}.s3"

  route_table_ids = [aws_route_table.public.id]

  tags = { Name = "${var.project_name}-s3-endpoint" }
}