package com.example.cantinabackend.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
class WebConfiguration(
    private val environment: Environment,
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        if (environment.activeProfiles.contains("local")) {
            registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600)
        }
    }

}