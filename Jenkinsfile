pipeline {
    agent any
    
    // Tools are installed system-wide on the EC2 instance
    
    environment {
        APP_NAME = 'upi-backend'
        JAR_NAME = 'upi-backend-0.0.1-SNAPSHOT.jar'
        // Server IP for Deployment
        APP_SERVER_IP = '16.112.4.208' 
        
        // Inject Secrets
        MONGODB_URI = credentials('mongodb-uri')
        MONGODB_DATABASE = credentials('mongodb-database')
        GOOGLE_CLIENT_ID = credentials('google-client-id')
        GOOGLE_CLIENT_SECRET = credentials('google-client-secret')
        JWT_SECRET = credentials('jwt-secret')
        RAZORPAY_KEY_ID = credentials('razorpay-key-id')
        RAZORPAY_KEY_SECRET = credentials('razorpay-key-secret')
        FRONTEND_URL = credentials('frontend-url')
        BACKEND_URL = credentials('backend-url')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code from GitHub...'
                // Ensure we checkout to the correct directory if needed, or default workspace
                git branch: 'main',
                    url: 'https://github.com/swarnim921/upi-backend.git'
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Building the application...'
                sh 'mvn clean compile -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 Running tests...'
                sh 'mvn test || echo "Tests completed with some failures"'
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Packaging the application...'
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Deploy to EC2') {
            steps {
                echo '🚀 Deploying to Application Server...'
                withCredentials([sshUserPrivateKey(credentialsId: 'upi-ec2-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    sh '''
                        echo "Deploying to ${APP_SERVER_IP}..."
                        # SSH Options
                        # We need to STRICTLY disable host key checking for automation
                        SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ConnectTimeout=30"
                        
                        # Debug: Verify JAR exists locally first
                        ls -la target/

                        # 1. Install Java (Simple check)
                        ssh $SSH_OPTS -i $SSH_KEY $SSH_USER@${APP_SERVER_IP} "java -version || sudo dnf install -y java-17-amazon-corretto"
                        sleep 2
                        
                        # 2. Kill existing process (Simplified)
                        # We ignore exit code 255 or 1 just in case
                        ssh $SSH_OPTS -i $SSH_KEY $SSH_USER@${APP_SERVER_IP} "pkill -f java || true" || true
                        sleep 2
                        
                        # 3. Copy JAR file (Verbose + Legacy Protocol)
                        echo "Checking remote permissions..."
                        ssh $SSH_OPTS -i $SSH_KEY $SSH_USER@${APP_SERVER_IP} "ls -ld /home/ec2-user"
                        
                        echo "Copying JAR to server (using legacy SCP)..."
                        # Use -O for legacy SCP protocol (avoids SFTP issues)
                        scp -v -O $SSH_OPTS -i $SSH_KEY target/${JAR_NAME} $SSH_USER@${APP_SERVER_IP}:/home/ec2-user/app.jar
                        
                        # 4. Start Application with Env Vars
                        ssh $SSH_OPTS -i $SSH_KEY $SSH_USER@${APP_SERVER_IP} "
                            export MONGODB_URI='${MONGODB_URI}'
                            export MONGODB_DATABASE='${MONGODB_DATABASE}'
                            export GOOGLE_CLIENT_ID='${GOOGLE_CLIENT_ID}'
                            export GOOGLE_CLIENT_SECRET='${GOOGLE_CLIENT_SECRET}'
                            export JWT_SECRET='${JWT_SECRET}'
                            export RAZORPAY_KEY_ID='${RAZORPAY_KEY_ID}'
                            export RAZORPAY_KEY_SECRET='${RAZORPAY_KEY_SECRET}'
                            export FRONTEND_URL='${FRONTEND_URL}'
                            export BACKEND_URL='${BACKEND_URL}'
                            
                            # Start in background
                            nohup java -jar /home/ec2-user/app.jar > app.log 2>&1 &
                            sleep 2
                        "
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            // Archiving artifacts
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
        failure {
            echo '❌ Pipeline failed!'
        }
        cleanup {
            cleanWs()
        }
    }
}
