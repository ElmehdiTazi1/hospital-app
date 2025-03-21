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

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des médecins.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final DepartementRepository departementRepository;

    @Override
    public List<Medecin> getAllMedecins() {
        log.info("Récupération de tous les médecins");
        return medecinRepository.findAll();
    }

    @Override
    public Optional<Medecin> getMedecinById(Long id) {
        log.info("Récupération du médecin avec l'ID: {}", id);
        return medecinRepository.findById(id);
    }

    @Override
    public Optional<Medecin> getMedecinByMatricule(String matricule) {
        log.info("Récupération du médecin avec le matricule: {}", matricule);
        return medecinRepository.findByMatricule(matricule);
    }

    @Override
    public Medecin saveMedecin(Medecin medecin) {
        if (medecin.getId() == null) {
            log.info("Création d'un nouveau médecin: {} {}", medecin.getPrenom(), medecin.getNom());
        } else {
            log.info("Mise à jour du médecin avec l'ID: {}", medecin.getId());
        }
        return medecinRepository.save(medecin);
    }

    @Override
    public void deleteMedecin(Long id) {
        log.info("Suppression du médecin avec l'ID: {}", id);
        medecinRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Medecin> findByNomContains(String nom, Pageable pageable) {
        log.info("Recherche de médecins contenant '{}' dans leur nom", nom);
        return medecinRepository.findByNomContainsIgnoreCase(nom, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> findBySpecialite(String specialite) {
        log.info("Recherche de médecins par spécialité: {}", specialite);
        return medecinRepository.findBySpecialite(specialite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> findAvailableMedecins() {
        log.info("Recherche de médecins disponibles");
        return medecinRepository.findByDisponibleTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> findByDepartement(Long departementId) {
        log.info("Recherche de médecins par département ID: {}", departementId);
        return medecinRepository.findByDepartementId(departementId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Medecin> searchMedecins(
            String nom,
            String prenom,
            String specialite,
            Long departementId,
            Boolean disponible,
            Pageable pageable) {
        log.info("Recherche avancée de médecins avec critères: nom={}, prenom={}, specialite={}, departementId={}, disponible={}",
                nom, prenom, specialite, departementId, disponible);
        return medecinRepository.rechercheMedecins(nom, prenom, specialite, departementId, disponible, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean medecinExists(Long id) {
        return medecinRepository.existsById(id);
    }

    @Override
    public Medecin setChefDepartement(Long medecinId, Long departementId) {
        log.info("Définition du médecin ID: {} comme chef du département ID: {}", medecinId, departementId);

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + medecinId));

        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new IllegalArgumentException("Département non trouvé avec l'ID: " + departementId));

        // Vérifiez si le médecin appartient au département
        if (medecin.getDepartement() == null || !medecin.getDepartement().getId().equals(departementId)) {
            medecin.setDepartement(departement);
            log.info("Le médecin a été assigné au département");
        }

        // Définir le médecin comme chef de département
        departement.setChefDepartement(medecin);
        departementRepository.save(departement);

        return medecin;
    }
}