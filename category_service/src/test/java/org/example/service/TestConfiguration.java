package org.example.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class TestConfiguration {

    @Container
    static GenericContainer<?> eureka = new GenericContainer<>("project_1c-eureka-server")
            .withExposedPorts(8761);

    @Container
    static GenericContainer<?> postgres = new GenericContainer<>("project_1c-postgres_db")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_USER", "root")
            .withEnv("POSTGRES_PASSWORD", "root");

    @Test
    public void test() {
    }

    @DynamicPropertySource
    static void eurekaProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.service-url.defaultZone", () -> "http://%s:%d/eureka"
                .formatted(eureka.getHost(), eureka.getFirstMappedPort()));
        registry.add("eureka.client.instance.preferIpAddress", () -> "true");
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://%s:%d/category_service?ApplicationName=Category_Service"
                .formatted(postgres.getHost(), postgres.getFirstMappedPort()));
    }

}
