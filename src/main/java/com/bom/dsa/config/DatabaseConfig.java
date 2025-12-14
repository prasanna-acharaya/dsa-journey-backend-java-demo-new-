package com.bom.dsa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.bom.dsa.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // Database configuration is handled via application.yml
    // This class enables JPA repositories and auditing
}
