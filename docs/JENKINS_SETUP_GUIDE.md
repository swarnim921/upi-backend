# Jenkins CI/CD Setup Guide for UPI Backend

## Prerequisites
- AWS Account (Free Tier)
- GitHub repository with code pushed
- EC2 Key Pair (.pem file)

---

## Step 1: Launch EC2 Instance for Jenkins

### 1.1 Create EC2 Instance
1. Go to **AWS Console** → **EC2** → **Launch Instance**
2. Configure:
   - **Name**: `jenkins-server`
   - **AMI**: Amazon Linux 2023 or Ubuntu 22.04
   - **Instance Type**: `t3.small` (Required for Jenkins stability, t3.micro is insufficient)
   - **Key Pair**: Select or create new (.pem file)
   - **Security Group**: Create new with rules below

### 1.2 Security Group Rules
| Type | Port | Source | Purpose |
|------|------|--------|---------|
| SSH | 22 | My IP | SSH access |
| Custom TCP | 8080 | Anywhere | Jenkins UI |
| HTTP | 80 | Anywhere | Web traffic |
| HTTPS | 443 | Anywhere | Secure traffic |

### 1.3 Connect to EC2
```bash
chmod 400 your-key.pem
ssh -i your-key.pem ec2-user@<EC2-PUBLIC-IP>
```

---

## Step 2: Install Java & Jenkins

### For Amazon Linux 2023:
```bash
# Update system
sudo yum update -y

# Install Java 17
sudo yum install java-17-amazon-corretto -y

# Verify Java
java -version

# Add Jenkins repo
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key

# Install Jenkins
sudo yum install jenkins -y

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### For Ubuntu:
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Add Jenkins repo
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install jenkins -y

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

---

## Step 3: Configure Jenkins

### 3.1 Access Jenkins UI
1. Open browser: `http://<EC2-PUBLIC-IP>:8080`
2. Enter the initial admin password
3. Click **Install suggested plugins**
4. Create admin user

### 3.2 Install Additional Plugins
Go to: **Manage Jenkins** → **Plugins** → **Available plugins**

Install:
- ✅ Git Plugin
- ✅ Maven Integration Plugin
- ✅ Pipeline
- ✅ SSH Agent Plugin
- ✅ Credentials Binding Plugin

### 3.3 Configure Tools
Go to: **Manage Jenkins** → **Tools**

**JDK Installation:**
- Name: `JDK17`
- Install automatically: ✅
- Version: `jdk-17`

**Maven Installation:**
- Name: `Maven3`
- Install automatically: ✅
- Version: `3.9.6`

---

## Step 4: Add Credentials

Go to: **Manage Jenkins** → **Credentials** → **System** → **Global credentials** → **Add Credentials**

### 4.1 EC2 SSH Key
| Field | Value |
|-------|-------|
| Kind | SSH Username with private key |
| ID | `ec2-ssh-key` |
| Username | `ec2-user` |
| Private Key | Enter directly → Paste .pem content |

### 4.2 EC2 Host
| Field | Value |
|-------|-------|
| Kind | Secret text |
| ID | `ec2-host` |
| Secret | `<Your EC2 Public IP>` |

### 4.3 Application Secrets
Add these as **Secret text**:

| ID | Secret Value |
|----|--------------|
| `mongodb-uri` | `YOUR_MONGODB_URI` |
| `mongodb-database` | `upi_dashboard` |
| `google-client-id` | `YOUR_GOOGLE_CLIENT_ID` |
| `google-client-secret` | `YOUR_GOOGLE_CLIENT_SECRET` |
| `jwt-secret` | `your-production-secret-here` |
| `razorpay-key-id` | `YOUR_RAZORPAY_KEY_ID` |
| `razorpay-key-secret` | `YOUR_RAZORPAY_KEY_SECRET` |

---

## Step 5: Create Pipeline Job

1. **Dashboard** → **New Item**
2. Enter name: `upi-backend-pipeline`
3. Select: **Pipeline**
4. Click **OK**

### Configure Pipeline:
**General:**
- ✅ GitHub project
- Project url: `https://github.com/YOUR_USERNAME/upi-backend`

**Build Triggers:**
- ✅ GitHub hook trigger for GITScm polling

**Pipeline:**
- Definition: `Pipeline script from SCM`
- SCM: `Git`
- Repository URL: `https://github.com/YOUR_USERNAME/upi-backend.git`
- Branch: `*/main`
- Script Path: `Jenkinsfile`

Click **Save**

---

## Step 6: Setup GitHub Webhook

1. Go to your GitHub repo → **Settings** → **Webhooks**
2. Click **Add webhook**
3. Configure:
   - Payload URL: `http://<JENKINS-EC2-IP>:8080/github-webhook/`
   - Content type: `application/json`
   - Events: `Just the push event`
4. Click **Add webhook**

---

## Step 7: Setup Application EC2

Create another EC2 instance for running your app:

### 7.1 Launch App Server EC2
- **Name**: `upi-backend-app`
- **AMI**: Amazon Linux 2023
- **Instance Type**: `t2.micro`
- **Key Pair**: Same as Jenkins
- **Security Group**: Allow ports 22, 8080, 80, 443

### 7.2 Install Java on App Server
```bash
ssh -i your-key.pem ec2-user@<APP-EC2-IP>
sudo yum install java-17-amazon-corretto -y
```

### 7.3 Create Systemd Service
```bash
sudo nano /etc/systemd/system/upi-backend.service
```

Paste:
```ini
[Unit]
Description=UPI Backend Spring Boot Application
After=network.target

[Service]
User=ec2-user
Type=simple
WorkingDirectory=/opt/app

# Environment Variables
Environment=MONGODB_URI=YOUR_MONGODB_URI
Environment=MONGODB_DATABASE=upi_dashboard
Environment=GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
Environment=GOOGLE_CLIENT_SECRET=YOUR_GOOGLE_CLIENT_SECRET
Environment=JWT_SECRET=your-production-secret-here
Environment=RAZORPAY_KEY_ID=YOUR_RAZORPAY_KEY_ID
Environment=RAZORPAY_KEY_SECRET=YOUR_RAZORPAY_KEY_SECRET

ExecStart=/usr/bin/java -jar /opt/app/upi-backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable upi-backend
```

---

## Step 8: Test the Pipeline

1. Go to Jenkins → `upi-backend-pipeline`
2. Click **Build Now**
3. Watch the build progress
4. Check **Console Output** for details

---

## Complete Flow Diagram

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   GitHub    │─────▶│   Jenkins   │─────▶│   AWS EC2   │
│  (Push)     │      │   (Build)   │      │   (Deploy)  │
└─────────────┘      └─────────────┘      └─────────────┘
      │                    │                     │
      │              ┌─────┴─────┐              │
      │              │           │              │
      ▼              ▼           ▼              ▼
   Webhook      Checkout     Package        Run App
   Trigger      + Build      + Test        on EC2
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Jenkins can't connect to EC2 | Check security group, verify SSH key |
| Build fails | Check Maven/Java configuration |
| App won't start | Check systemd logs: `sudo journalctl -u upi-backend` |
| Webhook not triggering | Verify GitHub webhook URL and Jenkins URL |
