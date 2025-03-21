package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un rendez-vous entre un patient et un médecin.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {

    /**
     * Identifiant unique du rendez-vous, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date et heure du rendez-vous.
     */
    @NotNull(message = "La date du rendez-vous est obligatoire")
    @FutureOrPresent(message = "La date du rendez-vous doit être dans le présent ou le futur")
    private LocalDateTime dateHeure;

    /**
     * Patient concerné par le rendez-vous.
     */
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    /**
     * Médecin qui reçoit le patient.
     */
    @ManyToOne
    @JoinColumn(name = "medecin_id")
    @NotNull(message = "Le médecin est obligatoire")
    private Medecin medecin;

    /**
     * Motif du rendez-vous.
     */
    private String motif;

    /**
     * Durée prévue du rendez-vous en minutes.
     */
    private Integer duree = 30;

    /**
     * Statut du rendez-vous (PLANIFIE, CONFIRME, ANNULE, TERMINE).
     */
    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut = StatutRendezVous.PLANIFIE;

    /**
     * Notes supplémentaires concernant le rendez-vous.
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Énumération des statuts possibles pour un rendez-vous.
     */
    public enum StatutRendezVous {
        PLANIFIE,
        CONFIRME,
        ANNULE,
        TERMINE
    }
}