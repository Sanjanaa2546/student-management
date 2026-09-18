pipeline {
    agent any
    environment {
        IMAGE_NAME = "student-management"
        CONTAINER_NAME = "student-management-container"
        APP_PORT = "8080"
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Sanjanaa2546/student-management.git'
            }
        }
        stage('Build & Test with Maven') {
            steps {
                bat 'mvn clean package'
            }
        }
        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%BUILD_NUMBER% .'
                bat 'docker tag %IMAGE_NAME%:%BUILD_NUMBER% %IMAGE_NAME%:latest'
            }
        }
        stage('Deploy Container') {
            steps {
                bat 'docker rm -f %CONTAINER_NAME% || exit 0'
                bat 'docker run -d --name %CONTAINER_NAME% -p %APP_PORT%:8080 %IMAGE_NAME%:latest'
            }
        }
    }
    post {
        success { echo 'Pipeline completed successfully.' }
        failure { echo 'Pipeline failed.' }
    }
}