package org.mql.hospital.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.Prescription;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.entities.User;
import org.mql.hospital.service.PatientService;
import org.mql.hospital.service.PrescriptionService;
import org.mql.hospital.service.RendezVousService;
import org.mql.hospital.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour gérer les fonctionnalités spécifiques aux patients.
 */
@Controller
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final RendezVousService rendezVousService;
    private final PrescriptionService prescriptionService;
    private final UserService userService;

    /**
     * Affiche le tableau de bord du patient.
     */
    @GetMapping("/dashboard")
    public String patientDashboard(Model model) {
        log.info("Affichage du tableau de bord patient");

        // Récupérer le patient connecté
        Patient patient = getCurrentPatient();
        if (patient == null) {
            return "redirect:/logout"; // Redirection vers la déconnexion si le patient n'est pas trouvé
        }

        // Données du patient
        model.addAttribute("patient", patient);

        // Récupérer tous les rendez-vous du patient
        List<RendezVous> rendezVous = rendezVousService.findByPatient(patient.getId());
        model.addAttribute("rendezVous", rendezVous);

        // Récupérer toutes les prescriptions du patient
        List<Prescription> prescriptions = prescriptionService.findByPatient(patient.getId());
        model.addAttribute("prescriptions", prescriptions);

        return "patients/dashboard";
    }

    /**
     * Récupère le patient actuellement connecté.
     */
    private Patient getCurrentPatient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Optional<User> user = userService.getUserByUsername(username);

            if (user.isPresent() && user.get().getPatient() != null) {
                return user.get().getPatient();
            }
        }
        return null;
    }
}