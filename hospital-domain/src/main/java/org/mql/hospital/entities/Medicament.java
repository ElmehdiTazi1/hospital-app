package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entité représentant un médicament dans la pharmacie de l'hôpital.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament {

    /**
     * Identifiant unique du médicament, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom commercial du médicament.
     */
    @NotBlank(message = "Le nom du médicament est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du médicament doit contenir entre 2 et 100 caractères")
    private String nom;

    /**
     * Nom de la molécule active (DCI - Dénomination Commune Internationale).
     */
    @NotBlank(message = "La DCI est obligatoire")
    private String dci;

    /**
     * Laboratoire pharmaceutique fabriquant le médicament.
     */
    private String laboratoire;

    /**
     * Dosage du médicament (ex: 500mg, 1g, etc.).
     */
    private String dosage;

    /**
     * Forme galénique du médicament (comprimé, sirop, injectable, etc.).
     */
    private String forme;

    /**
     * Date d'expiration du lot actuel.
     */
    @Temporal(TemporalType.DATE)
    private Date dateExpiration;

    /**
     * Quantité disponible en stock.
     */
    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer quantiteStock;

    /**
     * Seuil minimal de stock à partir duquel déclencher un réapprovisionnement.
     */
    @Min(value = 1, message = "Le seuil d'alerte doit être au moins 1")
    private Integer seuilAlerte;

    /**
     * Prix unitaire du médicament.
     */
    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private BigDecimal prix;

    /**
     * Indique si le médicament est disponible à la prescription.
     */
    private boolean disponible = true;

    /**
     * Contre-indications et précautions d'emploi.
     */
    @Column(length = 1000)
    private String contreIndications;

    /**
     * Vérifie si le stock du médicament est inférieur au seuil d'alerte.
     *
     * @return true si le stock est sous le seuil d'alerte, false sinon
     */
    @Transient
    public boolean isStockAlert() {
        return quantiteStock <= seuilAlerte;
    }

    /**
     * Vérifie si le médicament est périmé.
     *
     * @return true si la date d'expiration est dépassée, false sinon
     */
    @Transient
    public boolean isExpired() {
        if (dateExpiration == null) {
            return false;
        }
        return new Date().after(dateExpiration);
    }
}