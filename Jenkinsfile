pipeline {
    agent any
    tools {
        gradle 'default'
    }
    stages {
        stage('Init stage') {
            steps {
                sh 'echo "message from master branch"'
            }
        }
    }
}
