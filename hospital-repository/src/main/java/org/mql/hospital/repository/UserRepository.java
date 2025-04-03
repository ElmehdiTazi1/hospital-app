package org.mql.hospital.repository;

import org.mql.hospital.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     */
    Optional<User> findByUsername(String username);

    /**
     * Recherche un utilisateur par son adresse email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un nom d'utilisateur existe déjà.
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si une adresse email existe déjà.
     */
    boolean existsByEmail(String email);
}