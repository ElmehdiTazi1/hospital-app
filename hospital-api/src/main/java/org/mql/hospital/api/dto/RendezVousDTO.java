package org.mql.hospital.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les rendez-vous.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'un rendez-vous dans le système")
public class RendezVousDTO {

    @Schema(description = "Identifiant unique du rendez-vous", example = "1")
    private Long id;

    @NotNull(message = "La date du rendez-vous est obligatoire")
    @FutureOrPresent(message = "La date du rendez-vous doit être dans le présent ou le futur")
    @Schema(description = "Date et heure du rendez-vous", example = "2025-04-30T14:30:00")
    private LocalDateTime dateHeure;

    @Schema(description = "Identifiant du patient concerné", example = "1")
    private Long patientId;

    @Schema(description = "Nom du patient", example = "Jean Dupont")
    private String patientNom;

    @Schema(description = "Identifiant du médecin qui reçoit le patient", example = "1")
    private Long medecinId;

    @Schema(description = "Nom complet du médecin", example = "Dr. Marie Martin")
    private String medecinNom;

    @Schema(description = "Spécialité du médecin", example = "Cardiologie")
    private String medecinSpecialite;

    @Schema(description = "Motif du rendez-vous", example = "Consultation de suivi")
    private String motif;

    @Schema(description = "Durée prévue du rendez-vous en minutes", example = "30")
    private Integer duree = 30;

    @Schema(description = "Statut du rendez-vous (PLANIFIE, CONFIRME, ANNULE, TERMINE)", example = "PLANIFIE")
    private String statut;

    @Schema(description = "Notes supplémentaires concernant le rendez-vous")
    private String notes;
}