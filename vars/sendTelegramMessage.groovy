def call(String message) { 
    withCredentials([usernamePassword(credentialsId: 'TELEGRAM_BOTS', passwordVariable: 'TOKEN', usernameVariable: 'CHAT_ID')]) {
        script {
            withEnv(["TG_MESSAGE=${message}"]) { 
                sh '''#!/bin/bash -e 
                    curl -s -X POST "https://api.telegram.org/bot$TOKEN/sendMessage" -d "chat_id=$CHAT_ID" --data-urlencode "text=$TG_MESSAGE" > /dev/null
                ''' 
            }
        }
    } 
}