pipeline {
    agent any
    tools {
        gradle 'default'
    }
    stages {
        stage('test') {
            steps {
                sh 'gradle clean :category_service:test'
            }
        }
    }
}