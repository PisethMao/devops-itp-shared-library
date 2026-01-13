@Library('my-shared-library@master') _

pipeline {
    agent any

    environment {
        DOCKERFILE_PATH = 'resources/nextjs/Dockerfile'  // Path to Dockerfile in shared library
    }

    stages {
        stage('Clone Next.js Code') {
            steps {
                git 'https://github.com/PisethMao/Launchly'
            }
        }

        stage('Check Code Quality in SonarQube') {
            environment {
                scannerHome = tool 'sonarqube-scanner'
            }

            steps {
                script {
                    def projectKey = 'launchly'
                    def projectName = 'Launchly'
                    def projectVersion = '1.0.0'
                    checkCodeQualitySonarqube("${projectKey}", "${projectName}", "${projectVersion}")
                }
            }
        }

        stage('Wait for Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Debug Workspace') {
            steps {
                sh 'ls -R'
            }
        }

        stage('Load Dockerfile from Shared Library') {
            steps {
                writeFile file: 'Dockerfile', text: libraryResource('resources/nextjs/Dockerfile')
            }
        }

        stage('Build') {
            steps {
                sh '''
                    docker build -f Dockerfile -t pisethmao/jenkins-nextjs-sonarqube-pipeline:$BUILD_NUMBER .
                '''
            }
        }

        stage('Push') {
            steps {
                script {
                    def imageName="pisethmao/jenkins-nextjs-sonarqube-pipeline:${BUILD_NUMBER}" 
                    pushDockerToDH(imageName)
                }
            }
        }
    }

    post {
        success {
            script {
                def successMessage = """
                    Deployment is Success!!! ✅
                    Access Service: https://sonarqube.piseth.dev/dashboard?id=reactjs-template-product
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
                script {
                    sendTelegramMessage("${errorMessage}")
                }
            }
        }

        always {
            echo "This function do about clean work space!!!"
            echo "Clearing the workplace of ${env.JOB_NAME}"
            cleanWs()
        }
    }
}