package org.mql.hospital.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mql.hospital.api.dto.PatientDTO;
import org.mql.hospital.api.mapper.EntityMapperService;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patient", description = "API pour gérer les patients")
@RequiredArgsConstructor
public class PatientRestController {

    private final PatientService patientService;
    private final EntityMapperService mapper;
    @GetMapping
    @Operation(summary = "Lister tous les patients", description = "Récupère la liste de tous les patients avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des patients récupérée avec succès")
    })
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patientDTOs = mapper.toPatientDtoList(patientService.getAllPatients());
        return ResponseEntity.ok(patientDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un patient par ID", description = "Récupère les détails d'un patient spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé", content = @Content)
    })
    public ResponseEntity<PatientDTO> getPatientById(
            @Parameter(description = "ID du patient à récupérer") @PathVariable Long id) {

        Optional<Patient> patient = patientService.getPatientById(id);

        return patient.map(value -> ResponseEntity.ok(mapper.toPatientDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau patient", description = "Ajoute un nouveau patient dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<PatientDTO> createPatient(
            @Parameter(description = "Données du patient à créer") @Valid @RequestBody PatientDTO patientDTO) {

        Patient patient = mapper.toEntity(patientDTO,Patient.class);
        patient.setId(null); // Assurer que c'est bien une création

        Patient savedPatient = patientService.savePatient(patient);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPatientDto(savedPatient));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un patient", description = "Met à jour les données d'un patient existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<PatientDTO> updatePatient(
            @Parameter(description = "ID du patient à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du patient") @Valid @RequestBody PatientDTO patientDTO) {

        if (!patientService.patientExists(id)) {
            return ResponseEntity.notFound().build();
        }

        Patient patient = mapper.toEntity(patientDTO,Patient.class);
        patient.setId(id);

        Patient updatedPatient = patientService.savePatient(patient);

        return ResponseEntity.ok(mapper.toPatientDto(updatedPatient));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient", description = "Supprime un patient du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "ID du patient à supprimer") @PathVariable Long id) {

        if (!patientService.patientExists(id)) {
            return ResponseEntity.notFound().build();
        }

        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }


}