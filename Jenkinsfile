pipeline {
    agent any
    tools {
        gradle 'default'
    }
    stages {
        stage('Test echo message') {
            steps {
                sh 'echo "message from ${env.BRANCH_NAME} branch"'
            }
        }
    }
}
