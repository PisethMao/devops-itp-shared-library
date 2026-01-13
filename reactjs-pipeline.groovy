@Library('my-shared-library@master') _

pipeline {
    agent any
    
    environment {
        IMAGE_NAME = "pisethmao/jenkins-launchly-sonarqube-pipeline:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Clone Launchly Code') {
            steps {
                git 'https://github.com/PisethMao/reactjs-template-product.git'
            }
        }

        stage('Include Dockerfile from Shared Library') {
            steps {
                script {
                    def dockerfileContent = libraryResource 'nextjs/Dockerfile'
                    writeFile file: 'Dockerfile', text: dockerfileContent
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def imageName = "pisethmao/jenkins-launchly-sonarqube-pipeline:${BUILD_NUMBER}"
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def imageName = "pisethmao/jenkins-launchly-sonarqube-pipeline:${BUILD_NUMBER}"
                    pushDockerToDH(imageName)
                }
            }
        }

        stage('Deploy Docker Container') {
            steps {
                script {
                    def containerName = "launchly-container-${env.BUILD_NUMBER}"
                    sh "docker rm -f ${containerName} || true"
                    sh """
                        docker run -d -p 8090:3000 --name ${containerName} ${IMAGE_NAME}
                    """
                    sleep(time: 15)
                    sh 'docker ps'
                    sh 'curl http://localhost:8090'
                }
            }
        }
    }

    post {
        success {
            script {
                def successMessage = """
                    Deployment is Success!!! ✅
                    Access Service: http://localhost:8090
                    Job Name: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                """
                sendTelegramMessage(successMessage)
            }
        }

        failure {
            script {
                def errorMessage = """
                    Deployment is Failed!!! ❌
                    Job Name: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                """
                sendTelegramMessage("${errorMessage}")
            }
        }

        always {
            echo "This function does the clean work space!!!"
            cleanWs()
        }
    }
}