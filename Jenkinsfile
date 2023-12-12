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
        stage('Build images') {
            steps {
                script {
                    setAllVersions()
                    docker.build("eureka_server:${env.EUREKA_VERSION}", "./eureka_server")
                }
            }
        }
    }
}

def setAllVersions() {
    env.EUREKA_VERSION = getVersion("category_service")
    env.EUREKA_VERSION = getVersion("config_server")
    env.EUREKA_VERSION = getVersion("eureka_server")
    env.EUREKA_VERSION = getVersion("gateway")
    env.EUREKA_VERSION = getVersion("product_service")
    env.EUREKA_VERSION = getVersion("report_service")
}

String getVersion(String directory) {
    return sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./'+directory+'/build/info.txt', returnStdout: true).trim()
}