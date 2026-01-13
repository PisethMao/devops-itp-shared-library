@Library('my-shared-library') _

pipeline {
    agent any

    environment {
        CHAT_ID = "1093358989"
        CHAT_TOKEN="8417360346:AAFWYp5O224wzHkAZxZ9J-Zm4bVbu-V7M2c"
    }

    stages {
        stage('Send Message To Telegram') {
            steps {
                script {
                    def message = """{
                        "chat_id": "${env.CHAT_ID}",
                        "text": "Greetings from Jenkins Pipeline!\nThis is a test message sent to Telegram using Jenkins Shared Library. ✅"
                    }"""
                    sendTelegramMessage(message)
                }
            }
        }
    }
}