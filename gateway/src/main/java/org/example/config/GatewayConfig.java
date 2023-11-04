package org.example.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class GatewayConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeBuilder) {

        Function<GatewayFilterSpec, UriSpec> replaceSegmentAndAddHeaderFunction =
                f -> f.rewritePath("/api/v1/(?<segment>.*)", "/${segment}")
                        .addRequestHeader("gateway", "true");

        return routeBuilder.routes()
                .route("category_service_route", r -> r.path("/api/v1/category/**")
                        .filters(replaceSegmentAndAddHeaderFunction)
                        .uri("lb://category-service")
                )
                .build();
    }

}
