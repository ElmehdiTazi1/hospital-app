package org.mql.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.mql.hospital.commons.config.LoggingConfig;

/**
 * Configuration commune pour l'application.
 * Cette classe peut être importée par d'autres modules.
 */
@Configuration
@Import(LoggingConfig.class)
public class CommonConfig {

    /**
     * Configuration commune à tous les modules.
     * À compléter selon les besoins de l'application.
     */
    @Bean
    public String applicationName() {
        return "Hospital Management System";
    }
}