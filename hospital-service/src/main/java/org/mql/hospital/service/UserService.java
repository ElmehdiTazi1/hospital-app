package org.mql.hospital.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.Role;
import org.mql.hospital.entities.User;
import org.mql.hospital.repository.MedecinRepository;
import org.mql.hospital.repository.PatientRepository;
import org.mql.hospital.repository.RoleRepository;
import org.mql.hospital.repository.UserRepository;
import org.mql.hospital.service.UserRegistrationDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur avec le rôle patient.
     *
     * @param registrationDto Les données d'inscription
     * @return L'utilisateur créé
     */
    @Transactional
    public User registerNewPatientUser(org.mql.hospital.service.@Valid UserRegistrationDto registrationDto) {
        log.info("Enregistrement d'un nouveau patient: {}", registrationDto.getUsername());

        // Vérifier si le nom d'utilisateur ou l'email existe déjà
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Le nom d'utilisateur est déjà utilisé");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est déjà utilisée");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Ajouter le rôle PATIENT
        Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new RuntimeException("Rôle PATIENT non trouvé"));
        user.setRoles(Collections.singleton(patientRole));

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Créer et lier un nouveau patient
        Patient patient = new Patient();
        patient.setNom(registrationDto.getNom());
        patient.setUser(savedUser);
        patient.setScore(100); // Score par défaut
        patientRepository.save(patient);

        return savedUser;
    }

    /**
     * Crée un nouvel utilisateur avec le rôle médecin.
     * Cette méthode est généralement appelée par un administrateur.
     *
     * @param registrationDto Les données d'inscription
     * @param medecinId L'ID du médecin à associer
     * @return L'utilisateur créé
     */
    @Transactional
    public User registerNewMedecinUser(org.mql.hospital.service.@Valid UserRegistrationDto registrationDto, Long medecinId) {
        log.info("Enregistrement d'un nouveau compte médecin pour le médecin ID: {}", medecinId);

        // Vérifier si le médecin existe
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + medecinId));

        // Vérifier si le médecin a déjà un compte utilisateur
        if (medecin.getUser() != null) {
            throw new IllegalArgumentException("Ce médecin possède déjà un compte utilisateur");
        }

        // Vérifier si le nom d'utilisateur ou l'email existe déjà
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Le nom d'utilisateur est déjà utilisé");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est déjà utilisée");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Ajouter le rôle MEDECIN
        Role medecinRole = roleRepository.findByName("ROLE_MEDECIN")
                .orElseThrow(() -> new RuntimeException("Rôle MEDECIN non trouvé"));
        user.setRoles(Collections.singleton(medecinRole));

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Lier le médecin à l'utilisateur
        medecin.setUser(savedUser);
        medecinRepository.save(medecin);

        return savedUser;
    }

    /**
     * Crée un nouvel utilisateur administrateur.
     * Cette méthode est généralement utilisée pour l'initialisation du système.
     *
     * @param registrationDto Les données d'inscription
     * @return L'utilisateur créé
     */
    @Transactional
    public User registerNewAdminUser(UserRegistrationDto registrationDto) {
        log.info("Enregistrement d'un nouvel administrateur: {}", registrationDto.getUsername());

        // Vérifier si le nom d'utilisateur ou l'email existe déjà
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Le nom d'utilisateur est déjà utilisé");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est déjà utilisée");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Ajouter le rôle ADMIN
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
        user.setRoles(Collections.singleton(adminRole));

        // Sauvegarder l'utilisateur
        return userRepository.save(user);
    }

    /**
     * Récupère l'utilisateur actuellement connecté.
     *
     * @param username Le nom d'utilisateur
     * @return L'utilisateur s'il existe
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Modifie le mot de passe d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param newPassword Le nouveau mot de passe
     * @return L'utilisateur mis à jour
     */
    @Transactional
    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Désactive un compte utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @return L'utilisateur mis à jour
     */
    @Transactional
    public User disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));

        user.setActive(false);
        return userRepository.save(user);
    }

    /**
     * Active un compte utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @return L'utilisateur mis à jour
     */
    @Transactional
    public User enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));

        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * Ajoute un rôle à un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param roleName Le nom du rôle à ajouter
     * @return L'utilisateur mis à jour
     */
    @Transactional
    public User addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleName));

        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(role);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Retire un rôle à un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param roleName Le nom du rôle à retirer
     * @return L'utilisateur mis à jour
     */
    @Transactional
    public User removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleName));

        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.remove(role);

        // Vérifier qu'il reste au moins un rôle
        if (roles.isEmpty()) {
            throw new IllegalStateException("Impossible de retirer le dernier rôle de l'utilisateur");
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
}