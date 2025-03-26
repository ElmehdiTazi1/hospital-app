package org.mql.hospital.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les médecins.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'un médecin dans le système")
public class MedecinDTO {

    @Schema(description = "Identifiant unique du médecin", example = "1")
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Schema(description = "Nom de famille du médecin", example = "Dupont")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Schema(description = "Prénom du médecin", example = "Jean")
    private String prenom;

    @NotBlank(message = "La spécialité est obligatoire")
    @Schema(description = "Spécialité médicale", example = "Cardiologie")
    private String specialite;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    @Schema(description = "Numéro de téléphone", example = "+33612345678")
    private String telephone;

    @Email(message = "Email invalide")
    @Schema(description = "Adresse email", example = "jean.dupont@hospital.fr")
    private String email;

    @NotBlank(message = "Le matricule est obligatoire")
    @Schema(description = "Matricule unique du médecin dans l'hôpital", example = "MED001")
    private String matricule;

    @Schema(description = "Indique si le médecin est disponible", example = "true")
    private boolean disponible;

    // Informations sur le département (pour l'affichage)
    @Schema(description = "Identifiant du département auquel le médecin est rattaché", example = "1")
    private Long departementId;

    @Schema(description = "Nom du département auquel le médecin est rattaché", example = "Cardiologie")
    private String departementNom;
}