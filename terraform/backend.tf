/*
EC2
 */
resource "aws_instance" "web" {
  ami = var.migration_ami_id
  instance_type = var.ec2_instance_type
  subnet_id = aws_subnet.public_subnet[0].id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name
  key_name = var.ec2_key_name

  tags = { Name = "${var.project_name}-server" }
}

resource "aws_eip" "web_eip" {
  instance = aws_instance.web.id
  domain = "vpc"

  tags = { Name = "${var.project_name}-web-eip" }
}

/*
ALB
 */
resource "aws_lb" "main" {
  name = "${var.project_name}-alb"
  internal = false
  load_balancer_type = "application"
  security_groups = [aws_security_group.alb_sg.id]
  subnets = aws_subnet.public_subnet[*].id

  tags = { Name = "${var.project_name}-alb" }
}

resource "aws_lb_target_group" "web" {
  name     = "${var.project_name}-tg"
  port     = var.ec2_port
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path = var.ec2_health_check
  }
}

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.main.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = var.acm_certificate_arn_seoul

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.web.arn
  }
}

//80포트로 오면 443으로 리다이렉트
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port = "80"
  protocol = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port = "443"
      protocol = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_target_group_attachment" "web" {
  target_group_arn = aws_lb_target_group.web.arn
  target_id        = aws_instance.web.id
  port = var.ec2_port
}

/*
DB
 */
resource "aws_db_subnet_group" "default" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = aws_subnet.private_subnet[*].id
  tags = { Name = "My DB Subnet Group" }
}

resource "aws_db_instance" "default" {
  identifier        = "${var.project_name}-db"
  snapshot_identifier = var.db_snapshot_id != "" ? var.db_snapshot_id : null
  //스냅샷 복원시 아래 설정은 무시
  engine               = "mysql"
  engine_version       = var.db_mysql_engine_version
  instance_class       = var.db_instance_class
  username             = var.db_username
  password             = var.db_password
  allocated_storage    = var.db_mysql_allocated_storage

  db_subnet_group_name = aws_db_subnet_group.default.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  multi_az            = false # 비용 절감
  publicly_accessible = false
  skip_final_snapshot = true
}

/*
elasticache
 */
resource "aws_elasticache_subnet_group" "redis" {
  name       = "${var.project_name}-redis-subnet-group"
  subnet_ids = aws_subnet.private_subnet[*].id
}

resource "aws_elasticache_replication_group" "redis" {
  replication_group_id = "${var.project_name}-redis"
  description          = "My Redis Cluster"
  node_type            = "cache.t3.micro"
  num_cache_clusters   = 1 # 노드 1개 (비용 절감)
  port                 = 6379
  subnet_group_name    = aws_elasticache_subnet_group.redis.name
  security_group_ids   = [aws_security_group.redis_sg.id]
  engine         = "redis"
  engine_version = "7.0"
  snapshot_retention_limit = 0
}
