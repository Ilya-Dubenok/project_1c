package org.example.base;

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
public abstract class BaseRepositoryContainerTest {


    @Container
    static GenericContainer<?> postgres = new GenericContainer<>("postgres:15.3-alpine3.18")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_USER", "root")
            .withEnv("POSTGRES_PASSWORD", "root")
            .withEnv("POSTGRES_DB", "category_service");


    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://%s:%d/category_service?ApplicationName=Category_Service"
                .formatted(postgres.getHost(), postgres.getFirstMappedPort()));
    }

}
