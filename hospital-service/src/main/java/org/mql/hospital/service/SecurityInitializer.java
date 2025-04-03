package org.mql.hospital.service;

import lombok.RequiredArgsConstructor;
import org.mql.hospital.entities.Role;
import org.mql.hospital.repository.RoleRepository;
import org.mql.hospital.service.UserRegistrationDto;
import org.mql.hospital.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityInitializer {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeSecurity() {
        return args -> {
            // Création des rôles de base s'ils n'existent pas
            createRoleIfNotFound("ROLE_ADMIN", "Administrateur du système");
            createRoleIfNotFound("ROLE_MEDECIN", "Médecin de l'hôpital");
            createRoleIfNotFound("ROLE_PATIENT", "Patient de l'hôpital");

            // Création d'un compte administrateur par défaut si aucun admin n'existe
            if (!userService.adminExists()) {
                UserRegistrationDto adminDto = new UserRegistrationDto();
                adminDto.setUsername("admin");
                adminDto.setPassword("admin123"); // À changer en production
                adminDto.setPasswordConfirm("admin123");
                adminDto.setEmail("admin@hospital.org");
                adminDto.setNom("Administrateur");
                userService.registerNewAdminUser(adminDto);
            }
        };
    }

    private void createRoleIfNotFound(String name, String description) {
        if (!roleRepository.findByName(name).isPresent()) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            roleRepository.save(role);
        }
    }
}