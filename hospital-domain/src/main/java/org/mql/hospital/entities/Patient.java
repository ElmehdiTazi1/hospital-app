package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Entité représentant un patient dans le système de gestion hospitalière.
 * Cette classe est mappée à la table "patient" dans la base de données.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    /**
     * Identifiant unique du patient, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du patient. Ne peut pas être vide et doit avoir entre 4 et 20 caractères.
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 4, max = 20, message = "Le nom doit contenir entre 4 et 20 caractères")
    private String nom;

    /**
     * Date de naissance du patient.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    /**
     * Indique si le patient est actuellement malade.
     */
    private boolean malade;

    /**
     * Score médical du patient. Doit être au minimum de 100.
     */
    @Min(value = 100, message = "Le score doit être d'au moins 100")
    private int score;

    /**
     * Calcule l'âge du patient basé sur sa date de naissance.
     *
     * @return L'âge du patient en années
     */
    @Transient // Cette propriété n'est pas persistée en base de données
    public int getAge() {
        if (dateNaissance == null) {
            return 0;
        }
        Date now = new Date();
        long diffInMillis = now.getTime() - dateNaissance.getTime();
        return (int) (diffInMillis / (1000L * 60 * 60 * 24 * 365));
    }

    /**
     * Vérifie si le patient est considéré comme à risque élevé.
     * Un patient est à risque élevé s'il est malade et que son score est inférieur à 120.
     *
     * @return true si le patient est à risque élevé, false sinon
     */
    @Transient
    public boolean isHighRisk() {
        return malade && score < 120;
    }
}
