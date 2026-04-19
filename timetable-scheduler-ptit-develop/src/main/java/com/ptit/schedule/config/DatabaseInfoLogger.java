package com.ptit.schedule.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseInfoLogger implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String serverPort;

    @Value("${api.fe.url}")
    private String frontendUrl;

    @Value("${api.fe.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void run(String... args) throws Exception {
        log.info("================== APPLICATION STARTUP INFO ==================");
        log.info("Application Name: {}", appName);
        log.info("Server Port: {}", serverPort);
        log.info("Database URL: {}", databaseUrl);
        log.info("Database Username: {}", databaseUsername);
        log.info("Frontend URL: {}", frontendUrl);
        log.info("CORS Allowed Origins: {}", allowedOrigins);
        log.info("Swagger UI: http://localhost:{}/swagger-ui.html", serverPort);
        log.info("API Documentation: http://localhost:{}/v3/api-docs", serverPort);
        log.info("==========================================================");
    }
}