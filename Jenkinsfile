pipeline {
    agent any
    tools {
        gradle 'default'
    }
    stages {
        stage('build') {
            steps {
                sh 'gradle build --no-daemon'
            }
        }
    }
}