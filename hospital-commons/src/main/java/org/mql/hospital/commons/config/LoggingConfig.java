package org.mql.hospital.commons.config;

import org.springframework.context.annotation.Configuration;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration de logging partagée entre tous les modules.
 */
@Configuration
public class LoggingConfig {

    // Cette classe peut être étendue avec des méthodes utilitaires
    // pour configurer programmatiquement le logging

    /**
     * Obtient un logger configuré pour une classe donnée.
     *
     * @param clazz La classe pour laquelle obtenir un logger
     * @return Un logger SLF4J
     */
    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}