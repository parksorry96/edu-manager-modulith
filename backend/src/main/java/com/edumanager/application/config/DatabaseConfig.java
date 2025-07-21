package com.edumanager.application.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.edumanager")
@EntityScan(basePackages = "com.edumanager")
@EnableJpaAuditing
public class DatabaseConfig {
}
