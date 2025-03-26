package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.LignePrescription;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.entities.Prescription;
import org.mql.hospital.repository.LignePrescriptionRepository;
import org.mql.hospital.repository.MedicamentRepository;
import org.mql.hospital.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des lignes de prescription.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class LignePrescriptionServiceImpl implements LignePrescriptionService {

    private final LignePrescriptionRepository lignePrescriptionRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicamentRepository medicamentRepository;

    @Override
    public List<LignePrescription> getAllLignePrescriptions() {
        log.info("Récupération de toutes les lignes de prescription");
        return lignePrescriptionRepository.findAll();
    }

    @Override
    public Optional<LignePrescription> getLignePrescriptionById(Long id) {
        log.info("Récupération de la ligne de prescription avec l'ID: {}", id);
        return lignePrescriptionRepository.findById(id);
    }

    @Override
    public LignePrescription saveLignePrescription(LignePrescription lignePrescription) {
        // Validation
        validateLignePrescription(lignePrescription);

        if (lignePrescription.getId() == null) {
            log.info("Création d'une nouvelle ligne de prescription pour le médicament ID: {}",
                    lignePrescription.getMedicament().getId());
        } else {
            log.info("Mise à jour de la ligne de prescription avec l'ID: {}", lignePrescription.getId());
        }

        return lignePrescriptionRepository.save(lignePrescription);
    }

    @Override
    public void deleteLignePrescription(Long id) {
        log.info("Suppression de la ligne de prescription avec l'ID: {}", id);

        // Récupération de la ligne avant suppression pour mettre à jour la relation bidirectionnelle
        LignePrescription lignePrescription = lignePrescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de prescription non trouvée avec l'ID: " + id));

        // Vérification que la prescription est active avant suppression
        if (lignePrescription.getPrescription().getStatut() != Prescription.StatutPrescription.ACTIVE) {
            throw new IllegalStateException("Impossible de supprimer une ligne d'une prescription non active");
        }

        // Récupérer la prescription associée
        Prescription prescription = lignePrescription.getPrescription();

        // Supprimer la ligne de la collection dans la prescription
        if (prescription.getLignePrescriptions() != null) {
            prescription.getLignePrescriptions().remove(lignePrescription);
            prescriptionRepository.save(prescription); // Sauvegarder la prescription mise à jour
        }

        // Supprimer la ligne
        lignePrescriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LignePrescription> findByPrescription(Long prescriptionId) {
        log.info("Recherche des lignes de prescription pour la prescription ID: {}", prescriptionId);
        return lignePrescriptionRepository.findByPrescriptionId(prescriptionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LignePrescription> findByMedicament(Long medicamentId) {
        log.info("Recherche des lignes de prescription pour le médicament ID: {}", medicamentId);
        return lignePrescriptionRepository.findByMedicamentId(medicamentId);
    }

    @Override
    public LignePrescription addToPrescription(Long prescriptionId, LignePrescription lignePrescription) {
        log.info("Ajout d'une ligne de prescription à la prescription ID: {}", prescriptionId);

        // Récupérer la prescription
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription non trouvée avec l'ID: " + prescriptionId));

        // Vérifier que la prescription est active
        if (prescription.getStatut() != Prescription.StatutPrescription.ACTIVE) {
            throw new IllegalStateException("Impossible d'ajouter une ligne à une prescription non active");
        }

        // Vérifier que le médicament n'est pas déjà présent dans la prescription
        if (lignePrescription.getMedicament() != null &&
                prescription.getLignePrescriptions() != null &&
                prescription.getLignePrescriptions().stream()
                        .anyMatch(lp -> lp.getMedicament().getId().equals(lignePrescription.getMedicament().getId()))) {
            throw new IllegalStateException("Ce médicament est déjà présent dans la prescription");
        }

        // Associer la prescription à la ligne
        lignePrescription.setPrescription(prescription);

        // Sauvegarder la ligne
        LignePrescription savedLigne = lignePrescriptionRepository.save(lignePrescription);

        // Mettre à jour la collection des lignes de prescription
        if (prescription.getLignePrescriptions() == null) {
            prescription.setLignePrescriptions(new java.util.HashSet<>());
        }
        prescription.getLignePrescriptions().add(savedLigne);

        // Sauvegarder la prescription mise à jour
        prescriptionRepository.save(prescription);

        return savedLigne;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMedicamentInPrescription(Long prescriptionId, Long medicamentId) {
        log.info("Vérification si le médicament ID: {} est présent dans la prescription ID: {}", medicamentId, prescriptionId);

        List<LignePrescription> lignes = lignePrescriptionRepository.findByPrescriptionId(prescriptionId);
        return lignes.stream().anyMatch(ligne -> ligne.getMedicament().getId().equals(medicamentId));
    }

    /**
     * Valide les données d'une ligne de prescription avant de la sauvegarder.
     *
     * @param lignePrescription La ligne de prescription à valider
     */
    private void validateLignePrescription(LignePrescription lignePrescription) {
        // Vérifier que la prescription existe
        if (lignePrescription.getPrescription() == null || lignePrescription.getPrescription().getId() == null) {
            throw new IllegalArgumentException("La prescription est obligatoire");
        }

        // Vérifier que le médicament existe
        if (lignePrescription.getMedicament() == null || lignePrescription.getMedicament().getId() == null) {
            throw new IllegalArgumentException("Le médicament est obligatoire");
        }

        // Vérifier que la posologie est renseignée
        if (lignePrescription.getPosologie() == null || lignePrescription.getPosologie().trim().isEmpty()) {
            throw new IllegalArgumentException("La posologie est obligatoire");
        }

        // Vérifier que la durée du traitement est positive
        if (lignePrescription.getDureeTraitement() == null || lignePrescription.getDureeTraitement() <= 0) {
            throw new IllegalArgumentException("La durée du traitement doit être positive");
        }

        // Vérifier que la quantité est positive
        if (lignePrescription.getQuantite() == null || lignePrescription.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        // Vérifier que le médicament est disponible
        Medicament medicament = medicamentRepository.findById(lignePrescription.getMedicament().getId())
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé avec l'ID: " + lignePrescription.getMedicament().getId()));

        if (!medicament.isDisponible()) {
            throw new IllegalStateException("Le médicament " + medicament.getNom() + " n'est pas disponible");
        }

        // Vérifier que le stock est suffisant
        if (medicament.getQuantiteStock() < lignePrescription.getQuantite()) {
            throw new IllegalStateException("Stock insuffisant pour le médicament " + medicament.getNom() +
                    ". Stock disponible: " + medicament.getQuantiteStock() +
                    ", Quantité demandée: " + lignePrescription.getQuantite());
        }
    }
}