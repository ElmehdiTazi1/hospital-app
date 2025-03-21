package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une ligne de prescription (un médicament prescrit).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LignePrescription {

    /**
     * Identifiant unique de la ligne de prescription, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Prescription à laquelle cette ligne appartient.
     */
    @ManyToOne
    @JoinColumn(name = "prescription_id")
    @NotNull(message = "La prescription est obligatoire")
    private Prescription prescription;

    /**
     * Médicament prescrit.
     */
    @ManyToOne
    @JoinColumn(name = "medicament_id")
    @NotNull(message = "Le médicament est obligatoire")
    private Medicament medicament;

    /**
     * Posologie (ex: "1 comprimé 3 fois par jour").
     */
    @NotNull(message = "La posologie est obligatoire")
    private String posologie;

    /**
     * Durée du traitement en jours.
     */
    @Min(value = 1, message = "La durée du traitement doit être au moins 1 jour")
    private Integer dureeTraitement;

    /**
     * Instructions spécifiques pour ce médicament.
     */
    private String instructions;

    /**
     * Indique si ce médicament doit être pris avant, pendant ou après les repas.
     */
    @Enumerated(EnumType.STRING)
    private MomentPrise momentPrise;

    /**
     * Quantité totale de médicament à délivrer.
     */
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    /**
     * Indique si une substitution par un générique est autorisée.
     */
    private boolean substitutionAutorisee = true;

    /**
     * Énumération des moments possibles pour la prise d'un médicament.
     */
    public enum MomentPrise {
        AVANT_REPAS,
        PENDANT_REPAS,
        APRES_REPAS,
        INDIFFERENT
    }
}