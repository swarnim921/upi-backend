# Infrastructure as Code (Terraform)

This directory contains Terraform configuration for AWS infrastructure.

## What Gets Created

- **Application Load Balancer (ALB)** - Distributes traffic to EC2 instances
- **Target Group** - Routes traffic to your backend app on port 8080
- **Security Groups** - Controls network access
- **CloudFront Distribution** - CDN with HTTPS and caching
- **S3 Bucket** - Stores CloudFront access logs for monitoring

## Prerequisites

- AWS CLI configured with credentials
- Terraform installed (or use AWS CloudShell)
- VPC with at least 2 subnets in different AZs

## Usage

### Option 1: AWS CloudShell (Recommended)

1. Open AWS CloudShell in the console
2. Upload files: `main.tf` and `terraform.tfvars.example`
3. Rename: `mv terraform.tfvars.example terraform.tfvars`
4. Edit `terraform.tfvars` with your VPC and subnet IDs
5. Run:
   ```bash
   terraform init
   terraform plan
   terraform apply
   ```

### Option 2: Local Machine

1. Install Terraform: https://www.terraform.io/downloads
2. Configure AWS CLI: `aws configure`
3. Copy `terraform.tfvars.example` to `terraform.tfvars`
4. Update values in `terraform.tfvars`
5. Run:
   ```bash
   terraform init
   terraform plan
   terraform apply
   ```

## Getting VPC and Subnet IDs

Run in AWS CloudShell or local terminal:

```bash
# Get VPC ID
aws ec2 describe-vpcs --region ap-south-2

# Get Subnet IDs (select 2 from different AZs)
aws ec2 describe-subnets --region ap-south-2 --filters "Name=vpc-id,Values=<YOUR_VPC_ID>"
```

## Outputs

After successful apply, you'll get:

- **alb_dns_name** - Test your app: `http://<alb-dns>/actuator/health`
- **cloudfront_domain_name** - Production URL: `https://<domain>/actuator/health`
- **logs_bucket_name** - S3 bucket with CloudFront logs

## Cleanup

To destroy all resources:

```bash
terraform destroy
```

## Monitoring

- **CloudWatch Metrics**: Automatically enabled for ALB and CloudFront
- **CloudFront Logs**: Check S3 bucket (logs appear within 24 hours)
- **ALB Logs**: Can be enabled separately if needed
