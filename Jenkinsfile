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

        stage('Build images') {
            steps {
                environment {
                    script {
                        EUREKA_VERSION = getVersion("eureka_server")
                    }
                }
                script {
                    sh "echo ${env.EUREKA_VERSION}"
                    docker.build("eureka_server:${env.EUREKA_VERSION}", "./eureka_server")
                }
            }
        }
    }


}

String getVersion(String directory) {
    return sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./' + directory + '/build/info.txt', returnStdout: true)
}