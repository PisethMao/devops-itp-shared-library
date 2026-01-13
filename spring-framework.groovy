@Library('my-shared-library@master') _

pipeline {
    agent any
    
    environment {
        IMAGE_NAME = "pisethmao/jenkins-springboot-sonarqube-pipeline:${env.BUILD_NUMBER}"
        CONTAINER_NAME = "springboot-container-${env.BUILD_NUMBER}"
    }

    stages {
        stage('Clone Code') {
            steps {
                git 'https://github.com/PisethMao/sample-springboot-fileupload-gradle.git'
            }
        }

        stage('Include Dockerfile') {
            steps {
                script {
                    def dockerfileContent = libraryResource 'spring_framework/Dockerfile'
                    writeFile file: 'Dockerfile', text: dockerfileContent
                }
            }
        }

        stage('Build & Push') {
            steps {
                retry(3) {
                    sh "docker build -t ${IMAGE_NAME} ."
                }
                script { pushDockerToDH(IMAGE_NAME) }
            }
        }

        stage('Deploy & Health Check') {
            steps {
                script {
                    sh "docker rm -f ${CONTAINER_NAME} || true"
                    sh "docker run -d -p 8091:8080 --name ${CONTAINER_NAME} ${IMAGE_NAME}"
                    echo "Waiting for Spring Boot to be healthy..."
                    timeout(time: 2, unit: 'MINUTES') {
                        waitUntil {
                            def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:8091 || true", returnStdout: true).trim()
                            return (response != "000")
                        }
                    }
                    
                    sh "docker logs ${CONTAINER_NAME}"
                }
            }
        }
    }

    post {
        success {
            script {
                sendTelegramMessage("Success! ✅ App running at http://localhost:8091")
            }
        }
        failure {
            script {
                sh "docker logs ${CONTAINER_NAME} | tail -n 20 || true"
                sendTelegramMessage("Deployment Failed! ❌ Check Jenkins Build #${env.BUILD_NUMBER}")
            }
        }
        always {
            cleanWs()
        }
    }
}