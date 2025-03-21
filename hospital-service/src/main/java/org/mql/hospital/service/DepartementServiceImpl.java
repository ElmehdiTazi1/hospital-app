package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Departement;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.repository.DepartementRepository;
import org.mql.hospital.repository.MedecinRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation du service de gestion des départements.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class DepartementServiceImpl implements DepartementService {

    private final DepartementRepository departementRepository;
    private final MedecinRepository medecinRepository;

    @Override
    public List<Departement> getAllDepartements() {
        log.info("Récupération de tous les départements");
        return departementRepository.findAll();
    }

    @Override
    public Optional<Departement> getDepartementById(Long id) {
        log.info("Récupération du département avec l'ID: {}", id);
        return departementRepository.findById(id);
    }

    @Override
    public Departement saveDepartement(Departement departement) {
        if (departement.getId() == null) {
            log.info("Création d'un nouveau département: {}", departement.getNom());
        } else {
            log.info("Mise à jour du département avec l'ID: {}", departement.getId());
        }
        return departementRepository.save(departement);
    }

    @Override
    public void deleteDepartement(Long id) {
        log.info("Suppression du département avec l'ID: {}", id);
        departementRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Departement> findByNomContains(String nom, Pageable pageable) {
        log.info("Recherche de départements contenant '{}' dans leur nom", nom);
        return departementRepository.findByNomContainsIgnoreCase(nom, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Departement> findActiveDepartements() {
        log.info("Recherche des départements actifs");
        return departementRepository.findByActifTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Departement findByChefDepartement(Long medecinId) {
        log.info("Recherche du département dirigé par le médecin ID: {}", medecinId);
        return departementRepository.findByChefDepartementId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countMedecinsByDepartement() {
        log.info("Comptage du nombre de médecins par département");
        List<Object[]> results = departementRepository.countMedecinsByDepartement();
        Map<String, Long> countMap = new HashMap<>();

        for (Object[] result : results) {
            Long departementId = (Long) result[0];
            String departementNom = (String) result[1];
            Long count = (Long) result[2];
            countMap.put(departementNom, count);
        }

        return countMap;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Departement> findByCapaciteMinimale(Integer capaciteMinimale) {
        log.info("Recherche de départements avec une capacité de lits minimale de: {}", capaciteMinimale);
        return departementRepository.findByCapaciteLitsGreaterThanEqual(capaciteMinimale);
    }

    @Override
    public Departement toggleDepartementStatus(Long id, boolean actif) {
        log.info("{} le département avec l'ID: {}", actif ? "Activation de" : "Désactivation de", id);

        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Département non trouvé avec l'ID: " + id));

        departement.setActif(actif);
        return departementRepository.save(departement);
    }

    @Override
    public Departement assignChefDepartement(Long departementId, Long medecinId) {
        log.info("Assignation du médecin ID: {} comme chef du département ID: {}", medecinId, departementId);

        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new IllegalArgumentException("Département non trouvé avec l'ID: " + departementId));

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + medecinId));

        // Vérifiez si le médecin appartient déjà au département
        if (medecin.getDepartement() == null || !medecin.getDepartement().getId().equals(departementId)) {
            medecin.setDepartement(departement);
            medecinRepository.save(medecin);
            log.info("Le médecin a été assigné au département");
        }

        departement.setChefDepartement(medecin);
        return departementRepository.save(departement);
    }
}