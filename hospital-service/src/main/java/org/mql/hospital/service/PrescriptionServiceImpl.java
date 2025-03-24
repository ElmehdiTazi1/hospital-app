package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.LignePrescription;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.Prescription;
import org.mql.hospital.repository.LignePrescriptionRepository;
import org.mql.hospital.repository.MedecinRepository;
import org.mql.hospital.repository.PatientRepository;
import org.mql.hospital.repository.PrescriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des prescriptions.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final LignePrescriptionRepository lignePrescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Override
    // Dans PrescriptionServiceImpl
    public List<Prescription> getAllPrescriptions() {
        log.info("Récupération de toutes les prescriptions");
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        log.info("Nombre de prescriptions trouvées : {}", prescriptions.size());
        return prescriptions;
    }

    @Override
    public Optional<Prescription> getPrescriptionById(Long id) {
        log.info("Récupération de la prescription avec l'ID: {}", id);
        return prescriptionRepository.findById(id);
    }

    @Override
    public Prescription savePrescription(Prescription prescription) {
        // Validation
        validatePrescription(prescription);

        if (prescription.getId() == null) {
            log.info("Création d'une nouvelle prescription pour le patient ID: {} par le médecin ID: {}",
                    prescription.getPatient().getId(), prescription.getMedecin().getId());
        } else {
            log.info("Mise à jour de la prescription avec l'ID: {}", prescription.getId());
        }

        return prescriptionRepository.save(prescription);
    }

    @Override
    public void deletePrescription(Long id) {
        log.info("Suppression de la prescription avec l'ID: {}", id);
        prescriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findByPatient(Long patientId) {
        log.info("Recherche des prescriptions pour le patient ID: {}", patientId);
        return prescriptionRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findByMedecin(Long medecinId) {
        log.info("Recherche des prescriptions pour le médecin ID: {}", medecinId);
        return prescriptionRepository.findByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findActivePrescriptionsByPatient(Long patientId) {
        log.info("Recherche des prescriptions actives pour le patient ID: {}", patientId);
        return prescriptionRepository.findByPatientIdAndStatut(patientId, Prescription.StatutPrescription.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> findByPeriode(Date debut, Date fin) {
        log.info("Recherche des prescriptions entre {} et {}", debut, fin);
        return prescriptionRepository.findByDatePrescriptionBetween(debut, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Prescription> findByPatientAndPeriode(Long patientId, Date debut, Date fin, Pageable pageable) {
        log.info("Recherche des prescriptions pour le patient ID: {} entre {} et {}", patientId, debut, fin);
        return prescriptionRepository.findByPatientIdAndDatePrescriptionBetween(patientId, debut, fin, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Prescription> searchPrescriptions(
            Long patientId,
            Long medecinId,
            Prescription.StatutPrescription statut,
            Date dateDebut,
            Date dateFin,
            Pageable pageable) {
        log.info("Recherche avancée de prescriptions");
        return prescriptionRepository.recherchePrescriptions(patientId, medecinId, statut, dateDebut, dateFin, pageable);
    }

    @Override
    public Prescription addLignePrescription(Long prescriptionId, LignePrescription lignePrescription) {
        log.info("Ajout d'une ligne de prescription à la prescription ID: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription non trouvée avec l'ID: " + prescriptionId));

        // Vérification que la prescription est active
        if (prescription.getStatut() != Prescription.StatutPrescription.ACTIVE) {
            throw new IllegalStateException("Impossible d'ajouter une ligne à une prescription non active");
        }

        lignePrescription.setPrescription(prescription);
        lignePrescriptionRepository.save(lignePrescription);

        // Mise à jour de la collection des lignes de prescription
        if (prescription.getLignePrescriptions() == null) {
            prescription.setLignePrescriptions(new HashSet<>());
        }
        prescription.getLignePrescriptions().add(lignePrescription);

        return prescriptionRepository.save(prescription);
    }

    @Override
    public void deleteLignePrescription(Long lignePrescriptionId) {
        log.info("Suppression de la ligne de prescription avec l'ID: {}", lignePrescriptionId);

        LignePrescription lignePrescription = lignePrescriptionRepository.findById(lignePrescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de prescription non trouvée avec l'ID: " + lignePrescriptionId));

        // Vérification que la prescription est active
        if (lignePrescription.getPrescription().getStatut() != Prescription.StatutPrescription.ACTIVE) {
            throw new IllegalStateException("Impossible de supprimer une ligne d'une prescription non active");
        }

        lignePrescriptionRepository.deleteById(lignePrescriptionId);
    }

    @Override
    public Prescription updatePrescriptionStatus(Long id, Prescription.StatutPrescription statut) {
        log.info("Mise à jour du statut de la prescription ID: {} vers {}", id, statut);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription non trouvée avec l'ID: " + id));

        prescription.setStatut(statut);
        return prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countPrescriptionsByMedecin(Date dateDebut, Date dateFin) {
        log.info("Comptage des prescriptions par médecin entre {} et {}", dateDebut, dateFin);

        List<Object[]> results = prescriptionRepository.countPrescriptionsByMedecin(dateDebut, dateFin);

        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            Long medecinId = (Long) result[0];
            String medecinNom = (String) result[1] + " " + (String) result[2];
            Long count = (Long) result[3];
            countMap.put(medecinNom, count);
        }

        return countMap;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPrescriptionValide(Long id) {
        log.info("Vérification de la validité de la prescription ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription non trouvée avec l'ID: " + id));

        return prescription.isValide();
    }

    /**
     * Valide les données d'une prescription avant de la sauvegarder.
     *
     * @param prescription La prescription à valider
     */
    private void validatePrescription(Prescription prescription) {
        // Vérifier que le patient existe
        Patient patient = patientRepository.findById(prescription.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé avec l'ID: " + prescription.getPatient().getId()));
        prescription.setPatient(patient);

        // Vérifier que le médecin existe
        Medecin medecin = medecinRepository.findById(prescription.getMedecin().getId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + prescription.getMedecin().getId()));
        prescription.setMedecin(medecin);

        // Vérifier que la date de prescription n'est pas dans le futur
        if (prescription.getDatePrescription().after(new Date())) {
            throw new IllegalArgumentException("La date de prescription ne peut pas être dans le futur");
        }

        // Vérifier que la durée de validité est positive
        if (prescription.getDureeValidite() <= 0) {
            throw new IllegalArgumentException("La durée de validité doit être positive");
        }
    }
}