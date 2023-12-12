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
//                sh 'gradle :gateway:test'
//                sh 'gradle :config_server:test'
//                sh 'gradle :category_service:test'
//                sh 'gradle :product_service:test'
//                sh 'gradle :report_service:test'
//                junit '**/test-results/test/*.xml'
            }
        }
        stage('Build jars') {
            steps {
                sh 'gradle :eureka_server:build'
//                sh 'gradle build'
                println getVersion("eureka_server")
                sh 'echo ${EUREKA_VERSION}'
            }
        }
        stage('Build images') {
            steps {
                script {
                    docker.build("eureka_server:${EUREKA_VERSION}", "./eureka_server")
                }
            }
        }
    }


}

String getVersion(String build) {
    env.EUREKA_VERSION=sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./eureka_server/build/info.txt', returnStdout: true)
    return sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./'+build+'/build/info.txt', returnStdout: true)
}