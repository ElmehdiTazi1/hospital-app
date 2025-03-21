package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.repository.MedecinRepository;
import org.mql.hospital.repository.PatientRepository;
import org.mql.hospital.repository.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des rendez-vous.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Override
    public List<RendezVous> getAllRendezVous() {
        log.info("Récupération de tous les rendez-vous");
        return rendezVousRepository.findAll();
    }

    @Override
    public Optional<RendezVous> getRendezVousById(Long id) {
        log.info("Récupération du rendez-vous avec l'ID: {}", id);
        return rendezVousRepository.findById(id);
    }

    @Override
    public RendezVous saveRendezVous(RendezVous rendezVous) {
        validateRendezVous(rendezVous);

        if (rendezVous.getId() == null) {
            log.info("Création d'un nouveau rendez-vous pour le patient ID: {} avec le médecin ID: {} à la date: {}",
                    rendezVous.getPatient().getId(), rendezVous.getMedecin().getId(), rendezVous.getDateHeure());
        } else {
            log.info("Mise à jour du rendez-vous avec l'ID: {}", rendezVous.getId());
        }

        return rendezVousRepository.save(rendezVous);
    }

    @Override
    public void deleteRendezVous(Long id) {
        log.info("Suppression du rendez-vous avec l'ID: {}", id);
        rendezVousRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findByPatient(Long patientId) {
        log.info("Recherche des rendez-vous pour le patient ID: {}", patientId);
        return rendezVousRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findByMedecin(Long medecinId) {
        log.info("Recherche des rendez-vous pour le médecin ID: {}", medecinId);
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findByMedecinAndStatut(Long medecinId, RendezVous.StatutRendezVous statut) {
        log.info("Recherche des rendez-vous pour le médecin ID: {} avec le statut: {}", medecinId, statut);
        return rendezVousRepository.findByMedecinIdAndStatut(medecinId, statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        log.info("Recherche des rendez-vous entre {} et {}", debut, fin);
        return rendezVousRepository.findByDateHeureBetween(debut, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findByMedecinAndPeriode(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        log.info("Recherche des rendez-vous pour le médecin ID: {} entre {} et {}", medecinId, debut, fin);
        return rendezVousRepository.findByMedecinIdAndDateHeureBetween(medecinId, debut, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RendezVous> findByPatientAndStatut(Long patientId, RendezVous.StatutRendezVous statut, Pageable pageable) {
        log.info("Recherche paginée des rendez-vous pour le patient ID: {} avec le statut: {}", patientId, statut);
        return rendezVousRepository.findByPatientIdAndStatut(patientId, statut, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RendezVous> searchRendezVous(
            Long medecinId,
            Long patientId,
            RendezVous.StatutRendezVous statut,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Pageable pageable) {
        log.info("Recherche avancée de rendez-vous avec critères: medecinId={}, patientId={}, statut={}, dateDebut={}, dateFin={}",
                medecinId, patientId, statut, dateDebut, dateFin);
        return rendezVousRepository.rechercheRendezVous(medecinId, patientId, statut, dateDebut, dateFin, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMedecinAvailable(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        log.info("Vérification de la disponibilité du médecin ID: {} entre {} et {}", medecinId, debut, fin);

        // Vérifiez d'abord si le médecin existe et est disponible
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + medecinId));

        if (!medecin.isDisponible()) {
            log.info("Le médecin ID: {} n'est pas disponible", medecinId);
            return false;
        }

        // Vérifiez ensuite si le médecin a des rendez-vous qui se chevauchent
        return !rendezVousRepository.existsByMedecinIdAndDateHeureBetween(medecinId, debut, fin);
    }

    @Override
    public RendezVous updateRendezVousStatus(Long id, RendezVous.StatutRendezVous statut) {
        log.info("Mise à jour du statut du rendez-vous ID: {} vers {}", id, statut);

        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));

        rendezVous.setStatut(statut);
        return rendezVousRepository.save(rendezVous);
    }

    /**
     * Valide les données d'un rendez-vous avant de le sauvegarder.
     *
     * @param rendezVous Le rendez-vous à valider
     */
    private void validateRendezVous(RendezVous rendezVous) {
        // Vérifier si le patient existe
        Patient patient = patientRepository.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé avec l'ID: " + rendezVous.getPatient().getId()));
        rendezVous.setPatient(patient);

        // Vérifier si le médecin existe et est disponible
        Medecin medecin = medecinRepository.findById(rendezVous.getMedecin().getId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + rendezVous.getMedecin().getId()));

        if (!medecin.isDisponible()) {
            throw new IllegalStateException("Le médecin ID: " + medecin.getId() + " n'est pas disponible");
        }
        rendezVous.setMedecin(medecin);

        // Vérifier si la date du rendez-vous est dans le futur
        if (rendezVous.getDateHeure().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date du rendez-vous doit être dans le futur");
        }

        // Vérifier les chevauchements (pour les nouveaux rendez-vous uniquement)
        if (rendezVous.getId() == null) {
            LocalDateTime fin = rendezVous.getDateHeure().plusMinutes(rendezVous.getDuree());
            if (rendezVousRepository.existsByMedecinIdAndDateHeureBetween(
                    medecin.getId(), rendezVous.getDateHeure(), fin)) {
                throw new IllegalStateException("Le médecin a déjà un rendez-vous planifié pour ce créneau");
            }
        }
    }
}