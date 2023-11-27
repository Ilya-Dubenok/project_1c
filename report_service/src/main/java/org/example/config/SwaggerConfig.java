package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Value("${paths.jwt.token-url}")
    private String tokenUrl;

    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
    }

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${openapi.service.title}") String serviceTitle,
            @Value("${openapi.service.version}") String serviceVersion,
            @Value("${openapi.service.url}") String url) {
        final String securitySchemeName = "oauthScheme";
        return new OpenAPI()
                .servers(List.of(new Server().url(url)))
                .info(new Info().title(serviceTitle).version(serviceVersion))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createPasswordOauthFlow();
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createPasswordOauthFlow() {
        OAuthFlow flow = createTokenUrlOAuthFlow();
        return new OAuthFlows().password(flow);
    }

    private OAuthFlow createTokenUrlOAuthFlow() {
        return new OAuthFlow()
                .tokenUrl(tokenUrl)
                .scopes(new Scopes().addString("perform_all_operations", "full_access_scope"));
    }
}
