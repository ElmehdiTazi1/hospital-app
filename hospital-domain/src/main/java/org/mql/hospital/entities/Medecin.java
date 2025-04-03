package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Entité représentant un médecin dans le système de gestion hospitalière.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medecin {

    /**
     * Identifiant unique du médecin, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de famille du médecin.
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    /**
     * Prénom du médecin.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    /**
     * Spécialité médicale du médecin.
     */
    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;

    /**
     * Numéro de téléphone du médecin.
     */
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String telephone;

    /**
     * Adresse email du médecin.
     */
    @Email(message = "Email invalide")
    private String email;

    /**
     * Matricule unique du médecin dans l'hôpital.
     */
    @NotBlank(message = "Le matricule est obligatoire")
    @Column(unique = true)
    private String matricule;

    /**
     * Département auquel est rattaché le médecin.
     */
    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    /**
     * Liste des rendez-vous du médecin.
     */
    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
    private Set<RendezVous> rendezVous;

    /**
     * Indique si le médecin est disponible (non suspendu, non en congé, etc.).
     */
    private boolean disponible = true;

    /**
     * Relation avec l'utilisateur correspondant (pour l'authentification)
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}