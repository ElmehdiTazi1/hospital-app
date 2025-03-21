package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

/**
 * Entité représentant une prescription médicale.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    /**
     * Identifiant unique de la prescription, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date à laquelle la prescription a été établie.
     */
    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date de prescription est obligatoire")
    private Date datePrescription;

    /**
     * Patient concerné par la prescription.
     */
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    /**
     * Médecin qui a établi la prescription.
     */
    @ManyToOne
    @JoinColumn(name = "medecin_id")
    @NotNull(message = "Le médecin est obligatoire")
    private Medecin medecin;

    /**
     * Liste des lignes de prescription (médicaments prescrits).
     */
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LignePrescription> lignePrescriptions;

    /**
     * Durée de validité de la prescription en jours.
     */
    private Integer dureeValidite = 30;

    /**
     * Statut de la prescription (ACTIVE, TERMINEE, ANNULEE).
     */
    @Enumerated(EnumType.STRING)
    private StatutPrescription statut = StatutPrescription.ACTIVE;

    /**
     * Observations complémentaires du médecin.
     */
    @Column(length = 1000)
    private String observations;

    /**
     * Énumération des statuts possibles pour une prescription.
     */
    public enum StatutPrescription {
        ACTIVE,
        TERMINEE,
        ANNULEE
    }

    /**
     * Vérifie si la prescription est encore valide.
     *
     * @return true si la prescription est encore valide, false sinon
     */
    @Transient
    public boolean isValide() {
        if (statut != StatutPrescription.ACTIVE) {
            return false;
        }

        Date today = new Date();
        Date dateExpiration = new Date(datePrescription.getTime() + (long)dureeValidite * 24 * 60 * 60 * 1000);

        return !today.after(dateExpiration);
    }
}