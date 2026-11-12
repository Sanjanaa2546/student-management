
pipeline {
    agent any

    environment {
        IMAGE_NAME = "student-management"
        ACR_SERVER = "sanjuuacr2026.azurecr.io"
        FULL_IMAGE = "${ACR_SERVER}/${IMAGE_NAME}:${BUILD_NUMBER}"
        DOCKER     = "C:\\Users\\chsan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"
    }

    triggers {
        // Checks GitHub every minute for new commits.
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Sanjanaa2546/student-management.git'
            }
        }

        stage('Secret Scan (Gitleaks)') {
            steps {
                bat '"%DOCKER%" run --rm -v "%WORKSPACE%:/repo" zricethezav/gitleaks:latest detect --source=/repo -v'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn -B clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn -B test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '"%DOCKER%" build -t %FULL_IMAGE% -t %ACR_SERVER%/%IMAGE_NAME%:latest .'
            }
        }

        stage('Push to ACR') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'acr-creds', usernameVariable: 'ACR_USER', passwordVariable: 'ACR_PASS')]) {
                    bat 'echo %ACR_PASS%| "%DOCKER%" login %ACR_SERVER% -u %ACR_USER% --password-stdin'
                }

                bat '"%DOCKER%" push %FULL_IMAGE%'
                bat '"%DOCKER%" push %ACR_SERVER%/%IMAGE_NAME%:latest'
            }
        }

        stage('Deploy to AKS') {
            steps {
                withCredentials([file(credentialsId: 'aks-kubeconfig', variable: 'KUBECONFIG')]) {
                    bat 'kubectl apply -f k8s/'
                    bat 'kubectl set image deployment/student-management student-management=%FULL_IMAGE%'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                withCredentials([file(credentialsId: 'aks-kubeconfig', variable: 'KUBECONFIG')]) {

                    bat 'kubectl rollout status deployment/student-management --timeout=180s'
                    bat 'kubectl get pods -l app=student-management'
                    bat 'kubectl get service student-management-service'

                    powershell '''
                        $ip = ""

                        for ($i = 0; $i -lt 30 -and -not $ip; $i++) {
                            $ip = kubectl get service student-management-service -o jsonpath="{.status.loadBalancer.ingress[0].ip}"

                            if (-not $ip) {
                                Start-Sleep -Seconds 10
                            }
                        }

                        if (-not $ip) {
                            throw "No external IP assigned"
                        }

                        Write-Host "Service IP: $ip"

                        try {
                             "$url = "http://$ip:8081/students/health"

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

                        } catch {
                            throw "Application health check FAILED: $_"
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
