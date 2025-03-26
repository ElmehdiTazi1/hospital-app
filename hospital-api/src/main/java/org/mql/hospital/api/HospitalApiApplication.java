package org.mql.hospital.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.mql.hospital.config.CommonConfig;
import org.mql.hospital.config.PersistenceConfig;

/**
 * Point d'entrée pour l'application API REST du système de gestion hospitalière.
 */
@SpringBootApplication
@EntityScan("org.mql.hospital.entities")
@ComponentScan(basePackages = {
        "org.mql.hospital.api",
        "org.mql.hospital.service",
        "org.mql.hospital.config"
})
@Import({CommonConfig.class, PersistenceConfig.class})
public class HospitalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApiApplication.class, args);
    }
}