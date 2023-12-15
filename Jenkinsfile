pipeline {
    agent any
    tools {
        gradle 'default'
    }
    stages {
        stage('Clean up gradle') {
            steps {
                sh 'gradle clean'
            }
        }
        stage('Compile') {
            steps {
                sh 'gradle compileTestJava'
            }
        }
        stage('Testing') {
            steps {
                sh 'gradle test'
                sh 'gradle jacocoTestReport'
                junit '**/test-results/test/*.xml'
            }
        }
        stage('Submit for Sonar verification'){
            steps {
                withSonarQubeEnv("sonarqube1") {
                    sh 'gradle sonar -D sonar.gradle.skipCompile=true'
                }
            }
        }
        stage('Quality Gate from Sonar'){
            steps {
                timeout(time: 5, unit: 'MINUTES'){
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Build jars') {
            steps {
                sh 'gradle build'
            }
        }
        stage('Build and push images') {
            steps {
                script {
                    setAllVersions()
                    docker.withRegistry('', 'docker_cred') {
                        categoryServiceImage = docker.build("dubenokilya/category_service:${env.CATEGORY_SERVICE_VERSION}", "./category_service").push()
                        configServerImage = docker.build("dubenokilya/config_server:${env.CONFIG_SERVER_VERSION}", "./config_server").push()
                        eurekaServerImage = docker.build("dubenokilya/eureka_server:${env.EUREKA_VERSION}", "./eureka_server").push()
                        gatewayImage = docker.build("dubenokilya/gateway:${env.GATEWAY_VERSION}", "./gateway").push()
                        productServiceImage = docker.build("dubenokilya/product_service:${env.PRODUCT_SERVICE_VERSION}", "./product_service").push()
                        reportServiceImage = docker.build("dubenokilya/report_service:${env.REPORT_SERVICE_VERSION}", "./report_service").push()
                    }
                }
            }
        }
        stage('Clean up space') {
            steps {
                script {
                    try {
                        sh 'docker rmi $(docker image ls | grep -P \'dubenokilya/\' | awk \'{ print $3 }\')'
                        sh 'docker rmi $(docker images -f dangling=true -q)'
                    } catch (Exception ignored) {
                    }
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
    return sh(script: 'grep -oP \'build_version=\\K[^ ]+\' ./' + directory + '/build/info.txt', returnStdout: true).trim()
}