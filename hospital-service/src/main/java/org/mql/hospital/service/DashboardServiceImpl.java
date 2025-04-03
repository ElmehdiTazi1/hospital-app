package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Implémentation du service de tableau de bord.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final DepartementRepository departementRepository;
    private final RendezVousRepository rendezVousRepository;
    private final MedicamentRepository medicamentRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public Map<String, Long> getGeneralStatistics() {
        log.info("Récupération des statistiques générales pour le tableau de bord");

        Map<String, Long> stats = new HashMap<>();

        // Nombre total de patients
        long patientCount = patientRepository.count();
        stats.put("patientCount", patientCount);

        // Nombre total de médecins
        long medecinCount = medecinRepository.count();
        stats.put("medecinCount", medecinCount);

        // Nombre total de départements
        long departementCount = departementRepository.count();
        stats.put("departementCount", departementCount);

        // Nombre total de rendez-vous
        long rendezVousCount = rendezVousRepository.count();
        stats.put("rendezVousCount", rendezVousCount);

        // Nombre total de médicaments
        long medicamentCount = medicamentRepository.count();
        stats.put("medicamentCount", medicamentCount);

        // Nombre total de prescriptions
        long prescriptionCount = prescriptionRepository.count();
        stats.put("prescriptionCount", prescriptionCount);

        // Nombre de patients malades
        long patientsMaladesCount = patientRepository.countByMaladeTrue();
        stats.put("patientsMalades", patientsMaladesCount);

        // Nombre de médecins disponibles
        long medecinsDisponiblesCount = medecinRepository.countByDisponibleTrue();
        stats.put("medecinsDisponibles", medecinsDisponiblesCount);

        // Nombre de rendez-vous d'aujourd'hui
        long rendezVousDuJour = getRendezVousDuJourCount();
        stats.put("rendezVousDuJour", rendezVousDuJour);

        return stats;
    }

    @Override
    public long getMedicamentsEnAlerteCount() {
        log.info("Comptage des médicaments en alerte de stock");
        return medicamentRepository.countByQuantiteStockLessThanEqualSeuilAlerte();
    }

    @Override
    public long getMedicamentsExpirationProche(int joursLimite) {
        log.info("Comptage des médicaments proches de l'expiration (limite: {} jours)", joursLimite);

        // Calcul de la date limite
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, joursLimite);
        Date dateLimite = calendar.getTime();

        return medicamentRepository.countByDateExpirationBeforeAndQuantiteStockGreaterThan(dateLimite, 0);
    }

    @Override
    public long getRendezVousDuJourCount() {
        log.info("Comptage des rendez-vous du jour");

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);

        return rendezVousRepository.countByDateHeureBetweenAndStatutNot(
                debutJour,
                finJour,
                RendezVous.StatutRendezVous.ANNULE);
    }

    @Override
    public List<RendezVous> getRendezVousDuJour() {
        log.info("Récupération des rendez-vous du jour");

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);

        return rendezVousRepository.findByDateHeureBetweenAndStatutNotOrderByDateHeureAsc(
                debutJour,
                finJour,
                RendezVous.StatutRendezVous.ANNULE);
    }
}