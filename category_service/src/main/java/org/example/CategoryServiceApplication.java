package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories
@EnableDiscoveryClient(autoRegister = false)
@SpringBootApplication
public class CategoryServiceApplication {

    public static void main(String[] args)  {
        SpringApplication.run(CategoryServiceApplication.class, args);


    }
}