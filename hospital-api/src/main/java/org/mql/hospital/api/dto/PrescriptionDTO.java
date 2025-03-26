package org.mql.hospital.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les prescriptions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'une prescription médicale dans le système")
public class PrescriptionDTO {

    @Schema(description = "Identifiant unique de la prescription", example = "1")
    private Long id;

    @NotNull(message = "La date de prescription est obligatoire")
    @Schema(description = "Date à laquelle la prescription a été établie", example = "2025-03-15")
    private Date datePrescription;

    @Schema(description = "Identifiant du patient concerné", example = "1")
    private Long patientId;

    @Schema(description = "Nom du patient", example = "Jean Dupont")
    private String patientNom;

    @Schema(description = "Identifiant du médecin prescripteur", example = "1")
    private Long medecinId;

    @Schema(description = "Nom complet du médecin", example = "Dr. Marie Martin")
    private String medecinNom;

    @Schema(description = "Liste des médicaments prescrits")
    private List<LignePrescriptionDTO> lignePrescriptions;

    @Schema(description = "Durée de validité de la prescription en jours", example = "30")
    private Integer dureeValidite = 30;

    @Schema(description = "Statut de la prescription (ACTIVE, TERMINEE, ANNULEE)", example = "ACTIVE")
    private String statut;

    @Schema(description = "Observations complémentaires du médecin")
    private String observations;

    @Schema(description = "Indique si la prescription est encore valide", example = "true")
    private boolean valide;
}