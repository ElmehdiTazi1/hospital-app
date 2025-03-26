package org.mql.hospital.api.mapper;

import org.modelmapper.ModelMapper;
import org.mql.hospital.api.dto.MedicamentDTO;
import org.mql.hospital.api.dto.PatientDTO;
import org.mql.hospital.api.dto.MedecinDTO;
import org.mql.hospital.api.dto.DepartementDTO;
import org.mql.hospital.api.dto.RendezVousDTO;
import org.mql.hospital.api.dto.PrescriptionDTO;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Departement;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.entities.Prescription;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * Service pour la conversion entre les entités et les DTOs en utilisant ModelMapper.
 */
@Service
@RequiredArgsConstructor
public class EntityMapperService {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité en DTO.
     *
     * @param entity L'entité à convertir
     * @param dtoClass La classe du DTO cible
     * @return Le DTO converti
     */
    public <T, D> D toDto(T entity, Class<D> dtoClass) {
        if (entity == null) return null;
        return modelMapper.map(entity, dtoClass);
    }

    /**
     * Convertit un DTO en entité.
     *
     * @param dto Le DTO à convertir
     * @param entityClass La classe de l'entité cible
     * @return L'entité convertie
     */
    public <T, D> T toEntity(D dto, Class<T> entityClass) {
        if (dto == null) return null;
        return modelMapper.map(dto, entityClass);
    }

    /**
     * Convertit une liste d'entités en liste de DTOs.
     *
     * @param entities La liste d'entités à convertir
     * @param dtoClass La classe du DTO cible
     * @return Une liste de DTOs
     */
    public <T, D> List<D> toDtoList(List<T> entities, Class<D> dtoClass) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(entity -> toDto(entity, dtoClass))
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de DTOs en liste d'entités.
     *
     * @param dtos La liste de DTOs à convertir
     * @param entityClass La classe de l'entité cible
     * @return Une liste d'entités
     */
    public <T, D> List<T> toEntityList(List<D> dtos, Class<T> entityClass) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(dto -> toEntity(dto, entityClass))
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     *
     * @param dto Le DTO contenant les données à mettre à jour
     * @param entity L'entité à mettre à jour
     */
    public <T, D> void updateEntityFromDto(D dto, T entity) {
        if (dto == null || entity == null) return;
        modelMapper.map(dto, entity);
    }

    // Méthodes spécifiques pour les entités courantes

    // Patient
    public PatientDTO toPatientDto(Patient patient) {
        PatientDTO dto = toDto(patient, PatientDTO.class);
        if (patient != null) {
            dto.setAge(patient.getAge());
            dto.setHighRisk(patient.isHighRisk());
        }
        return dto;
    }

    public List<PatientDTO> toPatientDtoList(List<Patient> patients) {
        return patients.stream()
                .map(this::toPatientDto)
                .collect(Collectors.toList());
    }

    // Medicament
    public MedicamentDTO toMedicamentDto(Medicament medicament) {
        MedicamentDTO dto = toDto(medicament, MedicamentDTO.class);
        if (medicament != null) {
            dto.setEnAlerte(medicament.isStockAlert());
            dto.setExpire(medicament.isExpired());
        }
        return dto;
    }

    public List<MedicamentDTO> toMedicamentDtoList(List<Medicament> medicaments) {
        return medicaments.stream()
                .map(this::toMedicamentDto)
                .collect(Collectors.toList());
    }

    // Medecin
    public MedecinDTO toMedecinDto(Medecin medecin) {
        MedecinDTO dto = toDto(medecin, MedecinDTO.class);
        if (medecin != null && medecin.getDepartement() != null) {
            dto.setDepartementId(medecin.getDepartement().getId());
            dto.setDepartementNom(medecin.getDepartement().getNom());
        }
        return dto;
    }

    public List<MedecinDTO> toMedecinDtoList(List<Medecin> medecins) {
        return medecins.stream()
                .map(this::toMedecinDto)
                .collect(Collectors.toList());
    }

    // Departement
    public DepartementDTO toDepartementDto(Departement departement) {
        DepartementDTO dto = toDto(departement, DepartementDTO.class);
        if (departement != null && departement.getChefDepartement() != null) {
            dto.setChefDepartementId(departement.getChefDepartement().getId());
            dto.setChefDepartementNom(departement.getChefDepartement().getNom() + " " +
                    departement.getChefDepartement().getPrenom());
        }
        return dto;
    }

    public List<DepartementDTO> toDepartementDtoList(List<Departement> departements) {
        return departements.stream()
                .map(this::toDepartementDto)
                .collect(Collectors.toList());
    }
}