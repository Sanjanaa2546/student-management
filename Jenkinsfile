pipeline {
    agent any
    environment {
        IMAGE_NAME = "student-management"
        CONTAINER_NAME = "student-management-container"
        APP_PORT = "8080"
        DOCKER = "C:\\Users\\chsan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"
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
                bat '"%DOCKER%" build -t %IMAGE_NAME%:%BUILD_NUMBER% .'
                bat '"%DOCKER%" tag %IMAGE_NAME%:%BUILD_NUMBER% %IMAGE_NAME%:latest'
            }
        }
        stage('Deploy Container') {
            steps {
                bat '"%DOCKER%" rm -f %CONTAINER_NAME% || exit 0'
                bat '"%DOCKER%" run -d --name %CONTAINER_NAME% -p %APP_PORT%:8080 %IMAGE_NAME%:latest'
            }
        }
    }
    post {
        success { echo 'Pipeline completed successfully.' }
        failure { echo 'Pipeline failed.' }
    }
}