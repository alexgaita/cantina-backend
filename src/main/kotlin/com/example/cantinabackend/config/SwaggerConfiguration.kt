package com.example.cantinabackend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This class is responsible for creating a global custom component
 * in the Swagger UI Tools to pass the Bearer token to all requests.
 */
@Configuration
class SwaggerConfiguration {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val components = Components()
            .addSecuritySchemes(
                "bearer-key",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            )
        return OpenAPI().components(components)
    }
}