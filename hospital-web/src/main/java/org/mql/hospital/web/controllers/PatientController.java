package org.mql.hospital.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.Prescription;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.entities.User;
import org.mql.hospital.service.MedecinService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
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
    private final MedecinService medecinService;
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

        // Prochain rendez-vous
        RendezVous prochainRendezVous = getProchainRendezVous(patient);
        model.addAttribute("prochainRendezVous", prochainRendezVous);

        // Prescriptions actives
        List<Prescription> prescriptionsActives = getPrescriptionsActives(patient);
        model.addAttribute("prescriptionsActives", prescriptionsActives);

        // Liste des médecins disponibles (pour prise de rendez-vous)
        List<Medecin> medecinsDisponibles = medecinService.findAvailableMedecins();
        model.addAttribute("medecinsDisponibles", medecinsDisponibles);

        return "patients/dashboard";
    }

    /**
     * Affiche l'historique des rendez-vous du patient.
     */
    @GetMapping("/rendez-vous")
    public String patientRendezVous(Model model) {
        // Récupérer le patient connecté
        Patient patient = getCurrentPatient();
        if (patient == null) {
            return "redirect:/logout";
        }

        // Récupérer tous les rendez-vous du patient
        List<RendezVous> rendezVous = rendezVousService.findByPatient(patient.getId());
        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("patient", patient);

        return "patient/rendez-vous";
    }

    /**
     * Affiche les prescriptions du patient.
     */
    @GetMapping("/prescriptions")
    public String patientPrescriptions(Model model) {
        // Récupérer le patient connecté
        Patient patient = getCurrentPatient();
        if (patient == null) {
            return "redirect:/logout";
        }

        // Récupérer toutes les prescriptions du patient
        List<Prescription> prescriptions = prescriptionService.findByPatient(patient.getId());
        model.addAttribute("prescriptions", prescriptions);

        // Prescriptions actives
        List<Prescription> prescriptionsActives = prescriptionService.findActivePrescriptionsByPatient(patient.getId());
        model.addAttribute("prescriptionsActives", prescriptionsActives);

        model.addAttribute("patient", patient);

        return "patient/prescriptions";
    }

    /**
     * Affiche les détails d'une prescription.
     */
    @GetMapping("/prescriptions/{id}")
    public String prescriptionDetails(@PathVariable Long id, Model model) {
        // Récupérer le patient connecté
        Patient patient = getCurrentPatient();
        if (patient == null) {
            return "redirect:/logout";
        }

        // Vérifier que la prescription appartient bien au patient
        Optional<Prescription> prescription = prescriptionService.getPrescriptionById(id);
        if (prescription.isPresent() && prescription.get().getPatient().getId().equals(patient.getId())) {
            model.addAttribute("prescription", prescription.get());
            model.addAttribute("patient", patient);
            return "patient/prescription-details";
        }

        return "redirect:/patient/prescriptions";
    }

    /**
     * Affiche le profil du patient.
     */
    @GetMapping("/profile")
    public String patientProfile(Model model) {
        // Récupérer le patient connecté
        Patient patient = getCurrentPatient();
        if (patient == null) {
            return "redirect:/logout";
        }

        model.addAttribute("patient", patient);
        return "patient/profile";
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

    /**
     * Récupère le prochain rendez-vous d'un patient.
     */
    private RendezVous getProchainRendezVous(Patient patient) {
        List<RendezVous> rendezVous = rendezVousService.findByPatient(patient.getId());
        LocalDateTime maintenant = LocalDateTime.now();

        return rendezVous.stream()
                .filter(rdv -> rdv.getDateHeure().isAfter(maintenant)
                        && rdv.getStatut() != RendezVous.StatutRendezVous.ANNULE)
                .min((rdv1, rdv2) -> rdv1.getDateHeure().compareTo(rdv2.getDateHeure()))
                .orElse(null);
    }

    /**
     * Récupère les prescriptions actives d'un patient.
     */
    private List<Prescription> getPrescriptionsActives(Patient patient) {
        return prescriptionService.findActivePrescriptionsByPatient(patient.getId());
    }
}