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
//                println getVersion("eureka_server")
            }
        }

        stage('setup env') {
            steps {
                script {
                    getVersion()
                }
            }
        }

        stage('Build images') {
            steps {
                script {
//                    env.EUREKA_VERSION = 1
//                    environment {
//                        getVersion()
//                    }
                    sh 'echo ${env.EUREKA_VERSION}'
//                    getVersion("eureka_server")
                    docker.build("eureka_server:${env.EUREKA_VERSION}", "./eureka_server")
                }
            }
        }
    }


}

def getVersion() {
        env.EUREKA_VERSION=sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./eureka_server/build/info.txt', returnStdout: true)
}