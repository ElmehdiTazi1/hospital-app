package org.mql.hospital.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mql.hospital.api.dto.MedecinDTO;
import org.mql.hospital.api.mapper.EntityMapperService;
import org.mql.hospital.entities.Departement;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.service.DepartementService;
import org.mql.hospital.service.MedecinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medecins")
@Tag(name = "Medecin", description = "API pour gérer les médecins")
@RequiredArgsConstructor
public class MedecinRestController {

    private final MedecinService medecinService;
    private final DepartementService departementService;
    public final EntityMapperService mapper;

    @GetMapping
    @Operation(summary = "Lister tous les médecins", description = "Récupère la liste de tous les médecins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médecins récupérée avec succès")
    })
    public ResponseEntity<List<MedecinDTO>> getAllMedecins() {
        List<Medecin> medecinsPage = medecinService.getAllMedecins();
        List<MedecinDTO> medecinDTOs = mapper.toMedecinDtoList(medecinsPage);
        return ResponseEntity.ok(medecinDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un médecin par ID", description = "Récupère les détails d'un médecin spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecin trouvé"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé", content = @Content)
    })
    public ResponseEntity<MedecinDTO> getMedecinById(
            @Parameter(description = "ID du médecin à récupérer") @PathVariable Long id) {

        Optional<Medecin> medecin = medecinService.getMedecinById(id);

        return medecin.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/matricule/{matricule}")
    @Operation(summary = "Récupérer un médecin par matricule", description = "Récupère les détails d'un médecin spécifique par son matricule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecin trouvé"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé", content = @Content)
    })
    public ResponseEntity<MedecinDTO> getMedecinByMatricule(
            @Parameter(description = "Matricule du médecin à récupérer") @PathVariable String matricule) {

        Optional<Medecin> medecin = medecinService.getMedecinByMatricule(matricule);

        return medecin.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/specialite/{specialite}")
    @Operation(summary = "Lister les médecins par spécialité",
            description = "Récupère la liste des médecins ayant une spécialité spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médecins récupérée avec succès")
    })
    public ResponseEntity<List<MedecinDTO>> getMedecinsBySpecialite(
            @Parameter(description = "Spécialité médicale") @PathVariable String specialite) {

        List<Medecin> medecins = medecinService.findBySpecialite(specialite);

        List<MedecinDTO> medecinDTOs = medecins.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medecinDTOs);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Lister les médecins disponibles",
            description = "Récupère la liste des médecins actuellement disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médecins disponibles récupérée avec succès")
    })
    public ResponseEntity<List<MedecinDTO>> getMedecinsDisponibles() {
        List<Medecin> medecins = medecinService.findAvailableMedecins();

        List<MedecinDTO> medecinDTOs = medecins.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medecinDTOs);
    }

    @GetMapping("/departement/{departementId}")
    @Operation(summary = "Lister les médecins par département",
            description = "Récupère la liste des médecins appartenant à un département spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médecins récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content)
    })
    public ResponseEntity<List<MedecinDTO>> getMedecinsByDepartement(
            @Parameter(description = "ID du département") @PathVariable Long departementId) {

        if (!departementService.getDepartementById(departementId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Medecin> medecins = medecinService.findByDepartement(departementId);

        List<MedecinDTO> medecinDTOs = medecins.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medecinDTOs);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau médecin", description = "Ajoute un nouveau médecin dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médecin créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<MedecinDTO> createMedecin(
            @Parameter(description = "Données du médecin à créer") @Valid @RequestBody MedecinDTO medecinDTO) {

        Medecin medecin = convertToEntity(medecinDTO);
        medecin.setId(null); // Assurer que c'est bien une création

        // Associer le département si spécifié
        if (medecinDTO.getDepartementId() != null) {
            Optional<Departement> departement = departementService.getDepartementById(medecinDTO.getDepartementId());
            departement.ifPresent(medecin::setDepartement);
        }

        try {
            Medecin savedMedecin = medecinService.saveMedecin(medecin);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedMedecin));
        } catch (Exception e) {
            // En cas d'erreur (matricule dupliqué, etc.)
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un médecin", description = "Met à jour les données d'un médecin existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecin mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<MedecinDTO> updateMedecin(
            @Parameter(description = "ID du médecin à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du médecin") @Valid @RequestBody MedecinDTO medecinDTO) {

        if (!medecinService.medecinExists(id)) {
            return ResponseEntity.notFound().build();
        }

        Medecin medecin = convertToEntity(medecinDTO);
        medecin.setId(id);

        // Associer le département si spécifié
        if (medecinDTO.getDepartementId() != null) {
            Optional<Departement> departement = departementService.getDepartementById(medecinDTO.getDepartementId());
            departement.ifPresent(medecin::setDepartement);
        } else {
            medecin.setDepartement(null);
        }

        try {
            Medecin updatedMedecin = medecinService.saveMedecin(medecin);
            return ResponseEntity.ok(convertToDTO(updatedMedecin));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/disponibilite")
    @Operation(summary = "Modifier la disponibilité d'un médecin",
            description = "Change le statut de disponibilité d'un médecin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé", content = @Content)
    })
    public ResponseEntity<MedecinDTO> updateDisponibilite(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Parameter(description = "Nouveau statut de disponibilité") @RequestParam boolean disponible) {

        Optional<Medecin> medecinOpt = medecinService.getMedecinById(id);

        if (!medecinOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Medecin medecin = medecinOpt.get();
        medecin.setDisponible(disponible);

        Medecin updatedMedecin = medecinService.saveMedecin(medecin);

        return ResponseEntity.ok(convertToDTO(updatedMedecin));
    }

    @PatchMapping("/{id}/chef-departement")
    @Operation(summary = "Assigner un médecin comme chef de département",
            description = "Désigne un médecin comme chef d'un département spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecin assigné comme chef de département avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin ou département non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Opération invalide", content = @Content)
    })
    public ResponseEntity<MedecinDTO> setChefDepartement(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Parameter(description = "ID du département") @RequestParam Long departementId) {

        try {
            Medecin medecin = medecinService.setChefDepartement(id, departementId);
            return ResponseEntity.ok(convertToDTO(medecin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un médecin", description = "Supprime un médecin du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Médecin supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteMedecin(
            @Parameter(description = "ID du médecin à supprimer") @PathVariable Long id) {

        if (!medecinService.medecinExists(id)) {
            return ResponseEntity.notFound().build();
        }

        medecinService.deleteMedecin(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Convertit une entité Medecin en MedecinDTO.
     */
    private MedecinDTO convertToDTO(Medecin medecin) {
        return mapper.toMedecinDto(medecin);
    }

    /**
     * Convertit un MedecinDTO en entité Medecin.
     */
    private Medecin convertToEntity(MedecinDTO medecinDTO) {
        return mapper.toEntity(medecinDTO, Medecin.class);
    }
}