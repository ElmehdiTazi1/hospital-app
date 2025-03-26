package org.mql.hospital.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mql.hospital.api.dto.DepartementDTO;
import org.mql.hospital.api.mapper.EntityMapperService;
import org.mql.hospital.entities.Departement;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.service.DepartementService;
import org.mql.hospital.service.MedecinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departements")
@Tag(name = "Departement", description = "API pour gérer les départements hospitaliers")
@RequiredArgsConstructor
public class DepartementRestController {

    private final DepartementService departementService;
    private final MedecinService medecinService;
    private final EntityMapperService mapper;

    @GetMapping
    @Operation(summary = "Lister tous les départements", description = "Récupère la liste de tous les départements avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des départements récupérée avec succès")
    })
    public ResponseEntity<List<DepartementDTO>> getAllDepartements() {
        // Récupérer tous les départements
        List<Departement> departements = departementService.getAllDepartements();
        // Récupérer le décompte des médecins par département
        Map<String, Long> medecinsByDept = departementService.countMedecinsByDepartement();
        // Convertir et enrichir les DTOs
        List<DepartementDTO> departementDTOs = departements.stream()
                .map(dept -> {
                    // Convertir en DTO
                    DepartementDTO dto = mapper.toDepartementDto(dept);
                    // Enrichir avec le nombre de médecins
                    dto.setNombreMedecins(medecinsByDept.getOrDefault(dept.getNom(), 0L).intValue());

                    // Ajouter les informations du chef de département s'il existe
                    if (dept.getChefDepartement() != null) {
                        dto.setChefDepartementId(dept.getChefDepartement().getId());
                        dto.setChefDepartementNom(dept.getChefDepartement().getPrenom() + " " +
                                dept.getChefDepartement().getNom());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(departementDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un département par ID", description = "Récupère les détails d'un département spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Département trouvé"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content)
    })
    public ResponseEntity<DepartementDTO> getDepartementById(
            @Parameter(description = "ID du département à récupérer") @PathVariable Long id) {

        Optional<Departement> departement = departementService.getDepartementById(id);

        if (!departement.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        DepartementDTO dto = mapper.toDepartementDto(departement.get());
        List<Medecin> medecins = medecinService.findByDepartement(id);
        dto.setNombreMedecins(medecins.size());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/actifs")
    @Operation(summary = "Lister les départements actifs", description = "Récupère la liste de tous les départements actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des départements actifs récupérée avec succès")
    })
    public ResponseEntity<List<DepartementDTO>> getDepartementsActifs() {
        List<Departement> departements = departementService.findActiveDepartements();
        List<DepartementDTO> departementDTOs = mapper.toDtoList(departements, DepartementDTO.class);
        return ResponseEntity.ok(departementDTOs);
    }

    @GetMapping("/capacite/{capaciteMin}")
    @Operation(summary = "Lister les départements par capacité minimale",
            description = "Récupère la liste des départements ayant une capacité de lits supérieure ou égale à la valeur spécifiée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des départements récupérée avec succès")
    })
    public ResponseEntity<List<DepartementDTO>> getDepartementsByCapacite(
            @Parameter(description = "Capacité minimale de lits") @PathVariable Integer capaciteMin) {

        List<Departement> departements = departementService.findByCapaciteMinimale(capaciteMin);

        List<DepartementDTO> departementDTOs = mapper.toDtoList(departements, DepartementDTO.class);


        return ResponseEntity.ok(departementDTOs);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau département", description = "Ajoute un nouveau département dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Département créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<DepartementDTO> createDepartement(
            @Parameter(description = "Données du département à créer") @Valid @RequestBody DepartementDTO departementDTO) {

        Departement departement = mapper.toEntity(departementDTO, Departement.class);
        departement.setId(null); // Assurer que c'est bien une création
        Departement savedDepartement = departementService.saveDepartement(departement);
        // Si un chef de département est spécifié, l'assigner
        if (departementDTO.getChefDepartementId() != null) {
            try {
                departementService.assignChefDepartement(savedDepartement.getId(), departementDTO.getChefDepartementId());
                savedDepartement = departementService.getDepartementById(savedDepartement.getId()).get();
            } catch (IllegalArgumentException e) {
                // Ignorer si le médecin n'existe pas
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDepartementDto(savedDepartement));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un département", description = "Met à jour les données d'un département existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Département mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<DepartementDTO> updateDepartement(
            @Parameter(description = "ID du département à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du département") @Valid @RequestBody DepartementDTO departementDTO) {

        if (!departementService.getDepartementById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Departement departement = mapper.toEntity(departementDTO, Departement.class);
        departement.setId(id);
        Departement updatedDepartement = departementService.saveDepartement(departement);
        // Si un chef de département est spécifié, l'assigner
        if (departementDTO.getChefDepartementId() != null) {
            try {
                departementService.assignChefDepartement(id, departementDTO.getChefDepartementId());
                updatedDepartement = departementService.getDepartementById(id).get();
            } catch (IllegalArgumentException e) {
                // Ignorer si le médecin n'existe pas
            }
        }

        return ResponseEntity.ok(mapper.toDepartementDto(updatedDepartement));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un département", description = "Active ou désactive un département")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content)
    })
    public ResponseEntity<DepartementDTO> toggleStatut(
            @Parameter(description = "ID du département") @PathVariable Long id,
            @Parameter(description = "Nouveau statut (true pour actif, false pour inactif)") @RequestParam boolean actif) {

        try {
            Departement departement = departementService.toggleDepartementStatus(id, actif);
            return ResponseEntity.ok(mapper.toDepartementDto(departement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/chef")
    @Operation(summary = "Assigner un chef de département", description = "Désigne un médecin comme chef d'un département")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chef de département assigné avec succès"),
            @ApiResponse(responseCode = "404", description = "Département ou médecin non trouvé", content = @Content)
    })
    public ResponseEntity<DepartementDTO> assignerChefDepartement(
            @Parameter(description = "ID du département") @PathVariable Long id,
            @Parameter(description = "ID du médecin à désigner comme chef") @RequestParam Long medecinId) {

        try {
            Departement departement = departementService.assignChefDepartement(id, medecinId);
            return ResponseEntity.ok(mapper.toDepartementDto(departement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un département", description = "Supprime un département du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Département supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteDepartement(
            @Parameter(description = "ID du département à supprimer") @PathVariable Long id) {

        if (!departementService.getDepartementById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        departementService.deleteDepartement(id);

        return ResponseEntity.noContent().build();
    }


}