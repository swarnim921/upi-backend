# AWS Infrastructure Setup Guide

Complete guide for setting up Application Load Balancer (ALB) and CloudFront for the UPI Backend.

> **Region:** ap-south-2 (Hyderabad)  
> **Instance:** upi-backend-app (i-0c7e570df8180f3e5)

## Step 1: Create Target Group

1. Navigate to **EC2 Console** → **Load Balancing** → **Target Groups**
2. Click **Create target group**
3. Configure:
   - **Target type:** Instances
   - **Name:** `upi-backend-tg`
   - **Protocol:** HTTP
   - **Port:** 8080
   - **VPC:** Select your VPC
   - **Health check path:** `/actuator/health`
4. Click **Next**
5. Select instance `upi-backend-app` (i-0c7e570df8180f3e5)
6. Set port to **8080**
7. Click **Include as pending below**
8. Click **Create target group**

## Step 2: Create Application Load Balancer

1. Navigate to **EC2 Console** → **Load Balancers**
2. Click **Create load balancer** → **Application Load Balancer**
3. Configure:
   - **Name:** `upi-backend-alb`
   - **Scheme:** Internet-facing
   - **IP address type:** IPv4
4. **Network mapping:**
   - **VPC:** Same as your instance
   - **Availability Zones:** Select at least 2 (e.g., ap-south-2a, ap-south-2b)
5. **Security groups:**
   - Create new: `alb-sg`
   - Allow HTTP (80) from 0.0.0.0/0
6. **Listeners:**
   - Protocol: HTTP
   - Port: 80
   - Default action: Forward to `upi-backend-tg`
7. Click **Create load balancer**
8. **Copy the DNS name** once active (e.g., `upi-backend-alb-xxx.ap-south-2.elb.amazonaws.com`)

## Step 3: Update EC2 Security Group

1. Go to **EC2 Console** → **Instances**
2. Select `upi-backend-app`
3. Click **Security** tab → Click security group link
4. Edit **Inbound rules:**
   - **Type:** Custom TCP
   - **Port:** 8080
   - **Source:** Select the ALB security group (`alb-sg`)
5. Save rules

## Step 4: Create S3 Bucket for CloudFront Logs

1. Navigate to **S3 Console**
2. Click **Create bucket**
3. Configure:
   - **Name:** `upi-backend-cloudfront-logs-<your-account-id>`
   - **Region:** ap-south-2
   - Keep default settings
4. Click **Create bucket**

## Step 5: Create CloudFront Distribution

1. Navigate to **CloudFront Console**
2. Click **Create distribution**
3. **Origin settings:**
   - **Origin domain:** Paste ALB DNS name from Step 2
   - **Protocol:** HTTP only
   - **Name:** ALB-UPI-Backend
4. **Default cache behavior:**
   - **Viewer protocol policy:** Redirect HTTP to HTTPS
   - **Allowed HTTP methods:** GET, HEAD, OPTIONS, PUT, POST, PATCH, DELETE
   - **Cache policy:** CachingDisabled (for dynamic API)
   - **Origin request policy:** AllViewer
5. **Settings:**
   - **Price class:** Use all edge locations (or choose based on budget)
   - **Standard logging:** ON
   - **S3 bucket:** Select bucket from Step 4
   - **Log prefix:** `cloudfront/`
6. Click **Create distribution**
7. Wait 5-10 minutes for deployment
8. **Copy CloudFront domain name** (e.g., `d12345abcdef.cloudfront.net`)

## Step 6: Verify Setup

Test the endpoints:

```bash
# Test ALB directly
curl http://<alb-dns-name>/actuator/health

# Test via CloudFront
curl https://<cloudfront-domain>/actuator/health
```

Expected response: `{"status":"UP"}`

## Monitoring

### CloudWatch Metrics
- **ALB Metrics:** EC2 → Load Balancers → Monitoring tab
- **CloudFront Metrics:** CloudFront → Distributions → Monitoring tab

### CloudFront Logs
- Access logs in S3 bucket: `s3://upi-backend-cloudfront-logs-<account-id>/cloudfront/`
- Logs appear within 24 hours of requests

### Key Metrics to Monitor
- **ALB:** TargetResponseTime, HealthyHostCount, RequestCount
- **CloudFront:** Requests, BytesDownloaded, ErrorRate
