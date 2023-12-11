pipeline {
    agent any
    tools {
        gradle 'default'
    }
    stages {
        stage('Cleanup') {
            steps {
                sh 'gradle clean'
            }
        }
        stage('Testing') {
            steps {
                sh 'gradle :eureka_server:test'
                sh 'gradle :gateway:test'
                sh 'gradle :config_server:test'
                sh 'gradle :category_service:test'
                sh 'gradle :product_service:test'
                sh 'gradle :report_service:test'
                junit '**/test-results/test/*.xml'
            }
        }
        stage('Build jars') {
            steps {
                sh 'gradle build'
            }
        }
    }
}