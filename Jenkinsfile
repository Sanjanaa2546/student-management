
pipeline {
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    options {
        skipDefaultCheckout(true)
    }

    environment {
        IMAGE_NAME = 'student-management'
        ACR_SERVER = 'sanjuuacr2026.azurecr.io'
        FULL_IMAGE = "${ACR_SERVER}/${IMAGE_NAME}:${BUILD_NUMBER}"
        LATEST_IMAGE = "${ACR_SERVER}/${IMAGE_NAME}:latest"

        DOCKER_EXE = 'C:/Users/chsan/AppData/Local/Programs/DockerDesktop/resources/bin/docker.exe'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Source code checked out successfully.'
            }
        }

        stage('Secret Scan') {
            steps {
                bat '"%DOCKER_EXE%" run --rm -v "%WORKSPACE%:/repo" zricethezav/gitleaks:latest detect --source=/repo -v'
    }
}

        stage('Build with Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '''
                    "%DOCKER_EXE%" build -t "%FULL_IMAGE%" -t "%LATEST_IMAGE%" .
                '''
            }
        }

        stage('Push to ACR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'acr-creds',
                        usernameVariable: 'ACR_USERNAME',
                        passwordVariable: 'ACR_PASSWORD'
                    )
                ]) {
                    bat '''
                        echo %ACR_PASSWORD% | "%DOCKER_EXE%" login "%ACR_SERVER%" --username "%ACR_USERNAME%" --password-stdin

                        "%DOCKER_EXE%" push "%FULL_IMAGE%"

                        "%DOCKER_EXE%" push "%LATEST_IMAGE%"
                    '''
                }
            }
        }

        stage('Deploy to AKS') {
            steps {
                withCredentials([
                    file(
                        credentialsId: 'aks-kubeconfig',
                        variable: 'KUBECONFIG'
                    )
                ]) {
                    bat '''
                        kubectl apply -f k8s/

                        kubectl set image deployment/student-management student-management=%FULL_IMAGE%
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                withCredentials([
                    file(
                        credentialsId: 'aks-kubeconfig',
                        variable: 'KUBECONFIG'
                    )
                ]) {
                    bat 'kubectl rollout status deployment/student-management --timeout=180s'

                    bat 'kubectl get pods -l app=student-management'

                    bat 'kubectl get service student-management-service'

                    powershell '''
                        $ErrorActionPreference = "Stop"

                        $ip = ""

                        for ($i = 0; $i -lt 30 -and -not $ip; $i++) {

                            $ip = kubectl get service student-management-service -o jsonpath="{.status.loadBalancer.ingress[0].ip}"

                            $ip = "$ip".Trim()

                            if (-not $ip) {
                                Start-Sleep -Seconds 10
                            }
                        }

                        if (-not $ip) {
                            throw "No external IP assigned"
                        }

                        Write-Host "Service IP: $ip"

                        try {
                            $url = "http://${ip}:8081/students/health"

                            $r = Invoke-WebRequest `
                                -UseBasicParsing `
                                -Uri $url `
                                -TimeoutSec 30

                            if ($r.StatusCode -ne 200) {
                                throw "Unexpected HTTP status: $($r.StatusCode)"
                            }

                            Write-Host "HTTP status: $($r.StatusCode)"
                            Write-Host "Health response: $($r.Content)"
                            Write-Host "Application health check PASSED"

                        }
                        catch {
                            throw "Application health check FAILED: $($_.Exception.Message)"
                        }
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Deployed ${FULL_IMAGE} to AKS successfully."
        }

        failure {
            echo 'Pipeline failed - check the stage logs.'
        }
    }
}
