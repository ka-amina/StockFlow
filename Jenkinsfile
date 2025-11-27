pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9'
        jdk 'JDK 17'
    }
    
    environment {
        SONAR_HOST_URL = 'https://sonarcloud.io'
        SONAR_ORGANIZATION = 'ka-amina'
        SONAR_PROJECT_KEY = 'ka-amina_StockFlow'
        DOCKER_IMAGE = 'stockflow'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out source code...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Building application...'
                sh './mvnw clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh './mvnw test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/target/surefire-reports/*.xml'
                    
                    // Publish JaCoCo coverage report
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/dto/**,**/mapper/**,**/model/**,**/enums/**,**/config/**,**/exception/**,**/DemoApplication.class',
                        minimumInstructionCoverage: '80',
                        minimumBranchCoverage: '75',
                        minimumLineCoverage: '80',
                        maximumInstructionCoverage: '100',
                        maximumBranchCoverage: '100',
                        maximumLineCoverage: '100'
                    )
                }
            }
        }
        
        stage('Code Coverage Report') {
            steps {
                echo '📊 Generating detailed code coverage report...'
                sh './mvnw jacoco:report'
                
                // Publish HTML coverage report
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report',
                    reportTitles: 'Code Coverage'
                ])
                
                // Archive coverage data
                archiveArtifacts artifacts: '**/target/site/jacoco/**', allowEmptyArchive: true
            }
        }
        
        stage('SonarCloud Analysis') {
            steps {
                echo '🔍 Running SonarCloud analysis...'
                withSonarQubeEnv('SonarCloud') {
                    sh '''
                        ./mvnw clean verify sonar:sonar \
                        -Dsonar.organization=ka-amina \
                        -Dsonar.projectKey=ka-amina_StockFlow \
                        -Dsonar.host.url=https://sonarcloud.io
                    '''
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                echo '🚦 Checking Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Packaging application...'
                sh './mvnw package -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        
        stage('Build Docker Image') {
            when {
                branch 'main'
            }
            steps {
                echo '🐳 Building Docker image...'
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }
        
        stage('Push Docker Image') {
            when {
                branch 'main'
            }
            steps {
                echo '📤 Pushing Docker image...'
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            // Add notification here (Slack, Email, etc.)
        }
        failure {
            echo '❌ Pipeline failed!'
            // Add notification here (Slack, Email, etc.)
        }
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
    }
}
