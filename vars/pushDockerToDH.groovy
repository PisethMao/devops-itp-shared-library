def call(String imageName) {
    def dockerfileContent = libraryResource('nextjs/Dockerfile')
    writeFile file: 'Dockerfile', text: dockerfileContent
    withCredentials([usernamePassword(credentialsId: 'DOCKERHUB', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        // some block
        sh """
            set -e
            docker build -t ${imageName} .
            echo "\$PASSWORD" | docker login -u "\$USERNAME" --password-stdin
            docker push ${imageName}
        """
    }
}