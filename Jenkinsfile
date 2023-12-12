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
                    docker.build("eureka_server:${env.CATEGORY_SERVICE_VERSION}", "./category_service")
                    docker.build("eureka_server:${env.CONFIG_SERVER_VERSION}", "./config_server")
                    docker.build("eureka_server:${env.EUREKA_VERSION}", "./eureka_server")
                    docker.build("eureka_server:${env.GATEWAY_VERSION}", "./gateway")
                    docker.build("eureka_server:${env.PRODUCT_SERVICE_VERSION}", "./product_service")
                    docker.build("eureka_server:${env.REPORT_SERVICE_VERSION}", "./report_service")
                }
            }
        }
    }
}

def setAllVersions() {
    env.CATEGORY_SERVICE_VERSION = getVersion("category_service")
    env.CONFIG_SERVER_VERSION = getVersion("config_server")
    env.EUREKA_VERSION = getVersion("eureka_server")
    env.GATEWAY_VERSION = getVersion("gateway")
    env.PRODUCT_SERVICE_VERSION = getVersion("product_service")
    env.REPORT_SERVICE_VERSION = getVersion("report_service")
}

String getVersion(String directory) {
    return sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./'+directory+'/build/info.txt', returnStdout: true).trim()
}