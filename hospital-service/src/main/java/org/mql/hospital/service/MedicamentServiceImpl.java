package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.repository.MedicamentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des médicaments.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository medicamentRepository;

    @Override
    public List<Medicament> getAllMedicaments() {
        log.info("Récupération de tous les médicaments");
        return medicamentRepository.findAll();
    }

    @Override
    public Optional<Medicament> getMedicamentById(Long id) {
        log.info("Récupération du médicament avec l'ID: {}", id);
        return medicamentRepository.findById(id);
    }

    @Override
    public Medicament saveMedicament(Medicament medicament) {
        if (medicament.getId() == null) {
            log.info("Création d'un nouveau médicament: {}", medicament.getNom());
        } else {
            log.info("Mise à jour du médicament avec l'ID: {}", medicament.getId());
        }
        return medicamentRepository.save(medicament);
    }

    @Override
    public void deleteMedicament(Long id) {
        log.info("Suppression du médicament avec l'ID: {}", id);
        medicamentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Medicament> findByNomContains(String nom, Pageable pageable) {
        log.info("Recherche de médicaments contenant '{}' dans leur nom", nom);
        return medicamentRepository.findByNomContainingIgnoreCase(nom, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicament> findByDciContains(String dci) {
        log.info("Recherche de médicaments contenant '{}' dans leur DCI", dci);
        return medicamentRepository.findByDciContainingIgnoreCase(dci);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicament> findByLaboratoire(String laboratoire) {
        log.info("Recherche de médicaments par laboratoire: {}", laboratoire);
        return medicamentRepository.findByLaboratoireIgnoreCase(laboratoire);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicament> findAvailableMedicaments() {
        log.info("Recherche des médicaments disponibles");
        return medicamentRepository.findByDisponibleTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicament> findMedicamentsEnAlerte() {
        log.info("Recherche des médicaments en alerte de stock");
        return medicamentRepository.findMedicamentsEnAlerte();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicament> findMedicamentsExpiringBefore(Date date) {
        log.info("Recherche des médicaments expirant avant: {}", date);
        return medicamentRepository.findByDateExpirationBeforeAndQuantiteStockGreaterThan(date, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Medicament> searchMedicaments(
            String nom,
            String dci,
            String laboratoire,
            Boolean disponible,
            Boolean enAlerte,
            Pageable pageable) {
        // Utilisation de la méthode simplifiée pour éviter les erreurs
        log.info("Recherche simple de médicaments par nom: {}", nom);
        return medicamentRepository.rechercheMedicamentsSimple(nom, pageable);

        // Version complète à implémenter ultérieurement si nécessaire
        // return medicamentRepository.rechercheMedicaments(nom, dci, laboratoire, disponible, enAlerte, pageable);
    }

    @Override
    public Medicament updateStock(Long id, Integer quantite) {
        log.info("Mise à jour du stock du médicament ID: {} avec quantité: {}", id, quantite);

        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé avec l'ID: " + id));

        // Vérification que le stock ne devient pas négatif
        int newStock = medicament.getQuantiteStock() + quantite;
        if (newStock < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas être négatif. Stock actuel: " +
                    medicament.getQuantiteStock() + ", Modification demandée: " + quantite);
        }

        medicament.setQuantiteStock(newStock);

        // Mise à jour automatique du statut de disponibilité si le stock tombe à zéro
        if (newStock == 0) {
            medicament.setDisponible(false);
            log.info("Le médicament ID: {} est maintenant indisponible (stock = 0)", id);
        } else if (!medicament.isDisponible() && newStock > 0) {
            medicament.setDisponible(true);
            log.info("Le médicament ID: {} est maintenant disponible (stock > 0)", id);
        }

        return medicamentRepository.save(medicament);
    }

    @Override
    public Medicament toggleAvailability(Long id, boolean disponible) {
        log.info("{} le médicament ID: {}", disponible ? "Activation de" : "Désactivation de", id);

        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé avec l'ID: " + id));

        // Si on essaie de rendre disponible un médicament sans stock, on lance une exception
        if (disponible && medicament.getQuantiteStock() <= 0) {
            throw new IllegalStateException("Impossible de rendre disponible un médicament sans stock");
        }

        medicament.setDisponible(disponible);
        return medicamentRepository.save(medicament);
    }
}