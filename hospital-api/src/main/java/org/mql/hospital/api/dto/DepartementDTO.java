package org.mql.hospital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les départements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'un département dans le système")
public class DepartementDTO {

    @Schema(description = "Identifiant unique du département", example = "1")
    private Long id;

    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du département doit contenir entre 2 et 100 caractères")
    @Schema(description = "Nom du département", example = "Cardiologie")
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Schema(description = "Description du département")
    private String description;

    @Schema(description = "Localisation du département dans l'hôpital", example = "Bâtiment A, 3ème étage")
    private String localisation;

    @Schema(description = "Nombre de lits disponibles dans le département", example = "30")
    private Integer capaciteLits;

    @Schema(description = "Indique si le département est actuellement actif", example = "true")
    private boolean actif;

    // Informations sur le chef de département (pour l'affichage)
    @Schema(description = "Identifiant du chef de département", example = "1")
    private Long chefDepartementId;

    @Schema(description = "Nom complet du chef de département", example = "Dr. Jean Dupont")
    private String chefDepartementNom;

    @Schema(description = "Nombre de médecins rattachés au département", example = "5")
    private Integer nombreMedecins;
}