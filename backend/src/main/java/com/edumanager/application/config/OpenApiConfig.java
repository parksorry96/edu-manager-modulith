package com.edumanager.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EduManager API",
                description = "교육 관리 시스템 API - Spring Modulith 기반",
                version = "v1.0.0",
                contact = @Contact(
                        name = "EduManager Team",
                        email = "dev@edumanager.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "개발 서버"),
                @Server(url = "https://api.edumanager.com", description = "운영 서버")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-management")
                .displayName(" 사용자 관리")
                .pathsToMatch("/api/users/**", "/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("student-management")
                .displayName(" 학생 관리")
                .pathsToMatch("/api/students/**")
                .build();
    }

    @Bean
    public GroupedOpenApi devApi() {
        return GroupedOpenApi.builder()
                .group("development")
                .displayName(" 개발/테스트")
                .pathsToMatch("/api/dev/**")
                .build();
    }


}
