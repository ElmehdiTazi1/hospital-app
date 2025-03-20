package org.mql.hospital.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Objet de transfert de données pour les patients.
 * Utilisé pour les communications entre les couches service et présentation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 4, max = 20, message = "Le nom doit contenir entre 4 et 20 caractères")
    private String nom;

    private Date dateNaissance;

    private boolean malade;

    @Min(value = 100, message = "Le score doit être d'au moins 100")
    private int score;

    // Champs calculés
    private int age;
    private boolean highRisk;
}