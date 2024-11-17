package com.sprk.service.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("UserService API Documentation")
                                .description("This microservice handles and manages user based actions like registration of the user and login using JWT tokens for secure access.")
                                .version("0.0.1")
                ).servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:9991")
                                        .description("Local")
                        )
                );
    }

}
