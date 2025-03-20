package org.mql.hospital.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration de la couche de persistance.
 */
@Configuration
@EntityScan("org.mql.hospital.entities")
@EnableJpaRepositories("org.mql.hospital.repository")
@EnableTransactionManagement
public class PersistenceConfig {
    // Configuration spécifique à JPA si nécessaire
}