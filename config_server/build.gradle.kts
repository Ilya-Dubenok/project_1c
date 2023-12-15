import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.sonarqube") version "4.4.1.3373"
}

group = "org.example"
version = "0.1"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

sonar {
    properties {
        property("sonar.projectName", "project_1c")
        property("sonar.projectKey", "org:example:config_server")
    }
}

extra["springCloudVersion"] = "2022.0.4"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<BootJar>(){
    archiveFileName.set("config_server.jar")
}

tasks.named("build") {
    doLast {
        file("./build/info.txt").writeText("build_version=$version")
    }
}