package org.mql.hospital.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.entities.User;
import org.mql.hospital.service.MedecinService;
import org.mql.hospital.service.RendezVousService;
import org.mql.hospital.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour gérer les fonctionnalités spécifiques aux médecins.
 */
@Controller
@RequestMapping("/medecin")
@PreAuthorize("hasRole('MEDECIN')")
@RequiredArgsConstructor
@Slf4j
public class MedecinController {

    private final MedecinService medecinService;
    private final RendezVousService rendezVousService;
    private final UserService userService;

    /**
     * Affiche le tableau de bord du médecin.
     */
    @GetMapping("/dashboard")
    public String medecinDashboard(Model model) {
        log.info("Affichage du tableau de bord médecin");

        // Récupérer le médecin connecté
        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/logout"; // Redirection vers la déconnexion si le médecin n'est pas trouvé
        }

        // Données du médecin
        model.addAttribute("medecin", medecin);

        // Rendez-vous du jour
        List<RendezVous> rendezVousDuJour = getRendezVousDuJour(medecin);
        model.addAttribute("rendezVousDuJour", rendezVousDuJour);

        // Statistiques du médecin
        long rendezVousAVenir = getRendezVousAVenir(medecin);
        model.addAttribute("rendezVousAVenir", rendezVousAVenir);

        // Nombre de patients différents consultés
        long patientsConsultesCount = getPatientsConsultes(medecin);
        model.addAttribute("patientsConsultes", patientsConsultesCount);

        return "medecins/dashboard";
    }

    /**
     * Affiche le planning des rendez-vous du médecin.
     */
    @GetMapping("/planning")
    public String medecinPlanning(Model model) {
        // Récupérer le médecin connecté
        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/logout";
        }

        // Récupérer tous les rendez-vous du médecin
        List<RendezVous> rendezVous = rendezVousService.findByMedecin(medecin.getId());
        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("medecin", medecin);

        return "medecin/planning";
    }

    /**
     * Affiche les détails d'un patient pour le médecin.
     */
    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable Long id, Model model) {
        // Récupérer le médecin connecté
        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/logout";
        }

        // Recherche des rendez-vous du médecin avec ce patient
        // Logique pour récupérer les détails du patient et les rendez-vous associés

        return "medecin/patient-details";
    }

    /**
     * Affiche le profil du médecin.
     */
    @GetMapping("/profile")
    public String medecinProfile(Model model) {
        // Récupérer le médecin connecté
        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/logout";
        }

        model.addAttribute("medecin", medecin);
        return "medecin/profile";
    }

    /**
     * Récupère le médecin actuellement connecté.
     */
    private Medecin getCurrentMedecin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Optional<User> user = userService.getUserByUsername(username);

            if (user.isPresent() && user.get().getMedecin() != null) {
                return user.get().getMedecin();
            }
        }
        return null;
    }

    /**
     * Récupère les rendez-vous du jour pour un médecin.
     */
    private List<RendezVous> getRendezVousDuJour(Medecin medecin) {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);

        return rendezVousService.findByMedecinAndPeriode(medecin.getId(), debutJour, finJour);
    }

    /**
     * Récupère le nombre de rendez-vous à venir pour un médecin.
     */
    private long getRendezVousAVenir(Medecin medecin) {
        LocalDateTime maintenant = LocalDateTime.now();
        List<RendezVous> rendezVous = rendezVousService.findByMedecin(medecin.getId());

        return rendezVous.stream()
                .filter(rdv -> rdv.getDateHeure().isAfter(maintenant)
                        && rdv.getStatut() != RendezVous.StatutRendezVous.ANNULE)
                .count();
    }

    /**
     * Récupère le nombre de patients différents consultés par un médecin.
     */
    private long getPatientsConsultes(Medecin medecin) {
        List<RendezVous> rendezVous = rendezVousService.findByMedecin(medecin.getId());

        return rendezVous.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.TERMINE)
                .map(RendezVous::getPatient)
                .map(Patient::getId)
                .distinct()
                .count();
    }
}