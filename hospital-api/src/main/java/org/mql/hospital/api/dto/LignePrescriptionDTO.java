package org.mql.hospital.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les lignes de prescription.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'une ligne de prescription (médicament prescrit)")
public class LignePrescriptionDTO {

    @Schema(description = "Identifiant unique de la ligne de prescription", example = "1")
    private Long id;

    @Schema(description = "Identifiant de la prescription à laquelle cette ligne appartient", example = "1")
    private Long prescriptionId;

    @Schema(description = "Identifiant du médicament prescrit", example = "1")
    private Long medicamentId;

    @Schema(description = "Nom du médicament prescrit", example = "Doliprane")
    private String medicamentNom;

    @Schema(description = "Dosage du médicament prescrit", example = "500mg")
    private String medicamentDosage;

    @NotNull(message = "La posologie est obligatoire")
    @Schema(description = "Posologie (ex: 1 comprimé 3 fois par jour)", example = "1 comprimé toutes les 6 heures")
    private String posologie;

    @Min(value = 1, message = "La durée du traitement doit être au moins 1 jour")
    @Schema(description = "Durée du traitement en jours", example = "7")
    private Integer dureeTraitement;

    @Schema(description = "Instructions spécifiques pour ce médicament", example = "À prendre pendant les repas")
    private String instructions;

    @Schema(description = "Moment de prise du médicament (AVANT_REPAS, PENDANT_REPAS, APRES_REPAS, INDIFFERENT)", example = "APRES_REPAS")
    private String momentPrise;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Schema(description = "Quantité totale de médicament à délivrer", example = "20")
    private Integer quantite;

    @Schema(description = "Indique si une substitution par un générique est autorisée", example = "true")
    private boolean substitutionAutorisee = true;
}