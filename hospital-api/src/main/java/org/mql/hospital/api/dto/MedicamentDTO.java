package org.mql.hospital.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les médicaments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'un médicament dans le système")
public class MedicamentDTO {

    @Schema(description = "Identifiant unique du médicament", example = "1")
    private Long id;

    @NotBlank(message = "Le nom du médicament est obligatoire")
    @Schema(description = "Nom commercial du médicament", example = "Doliprane")
    private String nom;

    @NotBlank(message = "La DCI est obligatoire")
    @Schema(description = "Dénomination Commune Internationale (molécule active)", example = "Paracétamol")
    private String dci;

    @Schema(description = "Laboratoire pharmaceutique fabricant", example = "Sanofi")
    private String laboratoire;

    @Schema(description = "Dosage du médicament", example = "500mg")
    private String dosage;

    @Schema(description = "Forme galénique du médicament", example = "Comprimé")
    private String forme;

    @Schema(description = "Date d'expiration du lot actuel", example = "2025-12-31")
    private Date dateExpiration;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    @Schema(description = "Quantité disponible en stock", example = "100")
    private Integer quantiteStock;

    @Min(value = 1, message = "Le seuil d'alerte doit être au moins 1")
    @Schema(description = "Seuil minimal de stock pour déclenchement d'alerte", example = "20")
    private Integer seuilAlerte;

    @NotNull(message = "Le prix est obligatoire")
    @Schema(description = "Prix unitaire du médicament", example = "5.99")
    private BigDecimal prix;

    @Schema(description = "Indique si le médicament est disponible à la prescription", example = "true")
    private boolean disponible;

    @Schema(description = "Contre-indications et précautions d'emploi")
    private String contreIndications;

    // Champs calculés
    @Schema(description = "Indique si le stock est sous le seuil d'alerte", example = "false")
    private boolean enAlerte;

    @Schema(description = "Indique si le médicament est périmé", example = "false")
    private boolean expire;
}