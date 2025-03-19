package org.mql.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration commune pour tous les modules de l'application.
 * <p>
 * Cette classe charge les fichiers de propriétés communs et configure les beans partagés.
 * Elle doit être importée dans la configuration principale de chaque module exécutable.
 * </p>
 */
@Configuration
@PropertySource("classpath:application-common.properties")
public class CommonConfig {

    /**
     * Configure le résolveur de propriétés pour les fichiers de configuration.
     *
     * @return Le configurateur de propriétés
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}