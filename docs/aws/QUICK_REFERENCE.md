# Quick Reference Card - AWS Setup

## 📋 Checklist

### Manual Setup (30-45 min)
- [ ] **Step 1:** Create Target Group (5 min)
- [ ] **Step 2:** Create ALB (10 min)  
- [ ] **Step 3:** Secure EC2 (5 min)
- [ ] **Step 4:** Create S3 Bucket (3 min)
- [ ] **Step 5:** Create CloudFront (10 min)
- [ ] **Step 6:** Verify Monitoring (2 min)

### Terraform Setup (10-15 min)
- [ ] Open AWS CloudShell
- [ ] Upload `main.tf` and `terraform.tfvars.example`
- [ ] Get VPC and Subnet IDs
- [ ] Edit `terraform.tfvars`
- [ ] Run `terraform init && terraform apply`
- [ ] Copy outputs and test

## 🔑 Key Information

**Your Instance:**
- Name: `upi-backend-app`
- ID: `i-0c7e570df8180f3e5`
- Region: `ap-south-2` (Hyderabad)
- Port: `8080`

**Resources to Create:**
1. Target Group: `upi-backend-tg`
2. ALB: `upi-backend-alb`
3. Security Group: `alb-sg`
4. S3 Bucket: `upi-backend-cloudfront-logs-[account-id]`
5. CloudFront Distribution

## 🧪 Testing Commands

```bash
# Test ALB
curl http://[ALB-DNS-NAME]/actuator/health

# Test CloudFront
curl https://[CLOUDFRONT-DOMAIN]/actuator/health

# Check logs (after 24 hours)
aws s3 ls s3://upi-backend-cloudfront-logs-[account-id]/cloudfront/
```

## 📊 Where to Find Metrics

**ALB Metrics:**
EC2 Console → Load Balancers → Select ALB → Monitoring tab

**CloudFront Metrics:**
CloudFront Console → Distributions → Select Distribution → Monitoring tab

**CloudFront Logs:**
S3 Console → Your logs bucket → cloudfront/ folder

## 🚨 Common Issues

| Issue | Solution |
|-------|----------|
| Unhealthy targets | Check EC2 security group allows ALB on port 8080 |
| 502/503 errors | Wait 10 min after CloudFront creation |
| No logs | Normal - logs appear after 24 hours |
| Terraform subnet error | Use 2 subnets in different AZs |

## 💰 Estimated Cost

~$20-25/month for low-medium traffic
