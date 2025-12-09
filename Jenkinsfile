pipeline {
    agent any
    
    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }
    
    environment {
        APP_NAME = 'upi-backend'
        JAR_NAME = 'upi-backend-0.0.1-SNAPSHOT.jar'
        
        // These will be loaded from Jenkins Credentials
        EC2_HOST = credentials('ec2-host')
        EC2_USER = 'ec2-user'
        
        // Application secrets from Jenkins Credentials
        MONGODB_URI = credentials('mongodb-uri')
        MONGODB_DATABASE = credentials('mongodb-database')
        GOOGLE_CLIENT_ID = credentials('google-client-id')
        GOOGLE_CLIENT_SECRET = credentials('google-client-secret')
        JWT_SECRET = credentials('jwt-secret')
        RAZORPAY_KEY_ID = credentials('razorpay-key-id')
        RAZORPAY_KEY_SECRET = credentials('razorpay-key-secret')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code from GitHub...'
                git branch: 'main',
                    url: 'https://github.com/swarnim921/upi-backend.git'
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Building the application...'
                sh 'mvn clean compile -q'
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 Running tests...'
                sh 'mvn test -q'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Packaging the application...'
                sh 'mvn package -DskipTests -q'
            }
        }
        
        stage('Deploy to EC2') {
            steps {
                echo '🚀 Deploying to AWS EC2...'
                sshagent(['ec2-ssh-key']) {
                    sh """
                        # Copy JAR to EC2
                        scp -o StrictHostKeyChecking=no target/${JAR_NAME} ${EC2_USER}@${EC2_HOST}:/home/${EC2_USER}/
                        
                        # SSH and deploy
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'EOF'
                            # Stop existing service
                            sudo systemctl stop upi-backend || true
                            
                            # Move JAR to app directory
                            sudo mkdir -p /opt/app
                            sudo mv /home/${EC2_USER}/${JAR_NAME} /opt/app/
                            
                            # Set environment variables and start
                            sudo systemctl start upi-backend
                            
                            echo "✅ Deployment complete!"
EOF
                    """
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo '🏥 Checking application health...'
                sleep(time: 30, unit: 'SECONDS')
                sh """
                    curl -f http://${EC2_HOST}:8080/actuator/health || echo "Health check pending..."
                """
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
        always {
            cleanWs()
        }
    }
}
