def call(String imageName) {
    withCredentials([usernamePassword(credentialsId: 'DOCKERHUB', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        // some block
        sh """
            set -e
            echo "\$PASSWORD" | docker login -u "\$USERNAME" --password-stdin
            docker push ${imageName}
        """
    }
}