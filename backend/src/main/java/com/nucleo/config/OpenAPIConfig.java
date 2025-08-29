package com.nucleo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI nucleoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nucleo API")
                        .description("API REST para o sistema de gestão financeira Nucleo")
                        .version("v1.0.0")
                );
    }
}