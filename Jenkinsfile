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
                echo 'Checking out source code from Git...'
                git branch: 'main', url: 'https://github.com/Sanjanaa2546/student-management.git'
            }
        }

        stage('Build & Test with Maven') {
            steps {
                echo 'Running Maven build and unit tests...'
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .'
                sh 'docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest'
            }
        }

        stage('Deploy Container') {
            steps {
                echo 'Stopping any existing container and deploying the new one...'
                sh '''
                    docker rm -f ${CONTAINER_NAME} || true
                    docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:8080 ${IMAGE_NAME}:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully. Application deployed as a container.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs above.'
        }
    }
}
