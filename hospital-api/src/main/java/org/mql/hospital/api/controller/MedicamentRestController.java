package org.mql.hospital.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.mql.hospital.api.dto.MedicamentDTO;
import org.mql.hospital.api.mapper.EntityMapperService;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.service.MedicamentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicaments")
@Tag(name = "Medicament", description = "API pour gérer les médicaments")
@RequiredArgsConstructor
public class MedicamentRestController {

    private final MedicamentService medicamentService;
    private final EntityMapperService mapper;
    @GetMapping
    @Operation(summary = "Lister tous les médicaments", description = "Récupère la liste de tous les médicaments avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médicaments récupérée avec succès")
    })
    public ResponseEntity<List<MedicamentDTO>> getAllMedicaments() {
        return ResponseEntity.ok(mapper.toDtoList(medicamentService.getAllMedicaments(),MedicamentDTO.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un médicament par ID", description = "Récupère les détails d'un médicament spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médicament trouvé"),
            @ApiResponse(responseCode = "404", description = "Médicament non trouvé", content = @Content)
    })
    public ResponseEntity<MedicamentDTO> getMedicamentById(
            @Parameter(description = "ID du médicament à récupérer") @PathVariable Long id) {

        Optional<Medicament> medicament = medicamentService.getMedicamentById(id);

        return medicament.map(value -> ResponseEntity.ok(mapper.toMedicamentDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/alerte")
    @Operation(summary = "Lister les médicaments en alerte de stock",
            description = "Récupère la liste des médicaments dont le stock est inférieur ou égal au seuil d'alerte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médicaments en alerte récupérée avec succès")
    })
    public ResponseEntity<List<MedicamentDTO>> getMedicamentsEnAlerte() {
        List<Medicament> medicaments = medicamentService.findMedicamentsEnAlerte();
        return ResponseEntity.ok(mapper.toDtoList(medicaments,MedicamentDTO.class));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Lister les médicaments disponibles",
            description = "Récupère la liste des médicaments disponibles à la prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médicaments disponibles récupérée avec succès")
    })
    public ResponseEntity<List<MedicamentDTO>> getMedicamentsDisponibles() {
        List<Medicament> medicaments = medicamentService.findAvailableMedicaments();
        return ResponseEntity.ok(mapper.toDtoList(medicaments,MedicamentDTO.class));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau médicament", description = "Ajoute un nouveau médicament dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médicament créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<MedicamentDTO> createMedicament(
            @Parameter(description = "Données du médicament à créer") @Valid @RequestBody MedicamentDTO medicamentDTO) {

        Medicament medicament = mapper.toEntity(medicamentDTO,Medicament.class);
        medicament.setId(null); // Assurer que c'est bien une création
        Medicament savedMedicament = medicamentService.saveMedicament(medicament);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMedicamentDto(savedMedicament));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un médicament", description = "Met à jour les données d'un médicament existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médicament mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Médicament non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<MedicamentDTO> updateMedicament(
            @Parameter(description = "ID du médicament à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du médicament") @Valid @RequestBody MedicamentDTO medicamentDTO) {

        if (!medicamentService.getMedicamentById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Medicament medicament = mapper.toEntity(medicamentDTO,Medicament.class);
        medicament.setId(id);

        Medicament updatedMedicament = medicamentService.saveMedicament(medicament);

        return ResponseEntity.ok(mapper.toMedicamentDto(updatedMedicament));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Mettre à jour le stock d'un médicament",
            description = "Ajuste la quantité en stock d'un médicament (valeur positive pour ajouter, négative pour retirer)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Médicament non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Quantité invalide", content = @Content)
    })
    public ResponseEntity<MedicamentDTO> updateStock(
            @Parameter(description = "ID du médicament") @PathVariable Long id,
            @Parameter(description = "Quantité à ajouter/soustraire") @RequestParam Integer quantite) {

        try {
            Medicament medicament = medicamentService.updateStock(id, quantite);
            return ResponseEntity.ok(mapper.toMedicamentDto(medicament));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/disponibilite")
    @Operation(summary = "Modifier la disponibilité d'un médicament",
            description = "Active ou désactive la disponibilité d'un médicament pour la prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Médicament non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Opération invalide", content = @Content)
    })
    public ResponseEntity<MedicamentDTO> toggleDisponibilite(
            @Parameter(description = "ID du médicament") @PathVariable Long id,
            @Parameter(description = "Nouveau statut de disponibilité") @RequestParam boolean disponible) {

        try {
            Medicament medicament = medicamentService.toggleAvailability(id, disponible);
            return ResponseEntity.ok(mapper.toMedicamentDto(medicament));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un médicament", description = "Supprime un médicament du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Médicament supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Médicament non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteMedicament(
            @Parameter(description = "ID du médicament à supprimer") @PathVariable Long id) {

        if (!medicamentService.getMedicamentById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        medicamentService.deleteMedicament(id);

        return ResponseEntity.noContent().build();
    }



}