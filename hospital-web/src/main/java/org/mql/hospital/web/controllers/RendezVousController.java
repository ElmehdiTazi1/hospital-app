package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.service.MedecinService;
import org.mql.hospital.service.PatientService;
import org.mql.hospital.service.RendezVousService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur responsable de la gestion des rendez-vous via l'interface web.
 */
@Controller
@RequestMapping("/rendezvous")
@AllArgsConstructor
@Slf4j
public class RendezVousController {

    private final RendezVousService rendezVousService;
    private final PatientService patientService;
    private final MedecinService medecinService;

    /**
     * Affiche la liste des rendez-vous avec pagination et filtrage.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param page Numéro de page (0-indexed)
     * @param size Taille de la page
     * @param medecinId ID du médecin pour filtrer (optionnel)
     * @param patientId ID du patient pour filtrer (optionnel)
     * @param statut Statut des rendez-vous pour filtrer (optionnel)
     * @param debut Date de début pour filtrer (optionnel)
     * @param fin Date de fin pour filtrer (optionnel)
     * @return Le nom de la vue à afficher
     */
    @GetMapping
    public String listRendezVous(Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "medecinId", required = false) Long medecinId,
                                 @RequestParam(name = "patientId", required = false) Long patientId,
                                 @RequestParam(name = "statut", required = false) RendezVous.StatutRendezVous statut,
                                 @RequestParam(name = "debut", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                                 @RequestParam(name = "fin", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        LocalDateTime debutDateTime = debut != null ? debut.atStartOfDay() : null;
        LocalDateTime finDateTime = fin != null ? fin.atTime(LocalTime.MAX) : null;

        Page<RendezVous> pageRendezVous = rendezVousService.searchRendezVous(
                medecinId, patientId, statut, debutDateTime, finDateTime, PageRequest.of(page, size));

        // Récupérer la liste des médecins et patients pour les filtres
        List<Medecin> medecins = medecinService.findAvailableMedecins();
        List<Patient> patients = patientService.getAllPatients();

        model.addAttribute("listRendezVous", pageRendezVous.getContent());
        model.addAttribute("pages", new int[pageRendezVous.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("medecins", medecins);
        model.addAttribute("patients", patients);
        model.addAttribute("statuts", RendezVous.StatutRendezVous.values());
        model.addAttribute("medecinId", medecinId);
        model.addAttribute("patientId", patientId);
        model.addAttribute("statut", statut);
        model.addAttribute("debut", debut);
        model.addAttribute("fin", fin);

        return "rendezvous/list";
    }

    /**
     * Affiche le formulaire d'ajout d'un nouveau rendez-vous.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire
     */
    @GetMapping("/nouveau")
    public String showAddForm(Model model) {
        RendezVous rendezVous = new RendezVous();
        // Par défaut, le rendez-vous est planifié avec une durée de 30 minutes
        rendezVous.setStatut(RendezVous.StatutRendezVous.PLANIFIE);
        rendezVous.setDuree(30);

        prepareFormModel(model, rendezVous, false);
        return "rendezvous/form";
    }

    /**
     * Affiche le formulaire d'édition d'un rendez-vous existant.
     *
     * @param id L'identifiant du rendez-vous à éditer
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire ou redirection si le rendez-vous n'existe pas
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        RendezVous rendezVous = rendezVousService.getRendezVousById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous invalide avec l'ID: " + id));

        prepareFormModel(model, rendezVous, true);
        return "rendezvous/form";
    }

    /**
     * Traite la soumission du formulaire d'ajout ou d'édition d'un rendez-vous.
     *
     * @param rendezVous Le rendez-vous à sauvegarder
     * @param bindingResult Résultats de la validation du formulaire
     * @param patientId ID du patient sélectionné
     * @param medecinId ID du médecin sélectionné
     * @return Redirection vers la liste des rendez-vous ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/save")
    public String saveRendezVous(@Valid RendezVous rendezVous,
                                 BindingResult bindingResult,
                                 @RequestParam Long patientId,
                                 @RequestParam Long medecinId,
                                 Model model) {

        // Associer le patient et le médecin
        Patient patient = patientService.getPatientById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + patientId));
        rendezVous.setPatient(patient);

        Medecin medecin = medecinService.getMedecinById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + medecinId));
        rendezVous.setMedecin(medecin);

        try {
            rendezVousService.saveRendezVous(rendezVous);
            return "redirect:/rendezvous";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Capturer les erreurs de validation du service
            bindingResult.rejectValue("dateHeure", "error.rendezVous", e.getMessage());
            prepareFormModel(model, rendezVous, rendezVous.getId() != null);
            return "rendezvous/form";
        }
    }

    /**
     * Supprime un rendez-vous.
     *
     * @param id L'identifiant du rendez-vous à supprimer
     * @return Redirection vers la liste des rendez-vous
     */
    @GetMapping("/delete/{id}")
    public String deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return "redirect:/rendezvous";
    }

    /**
     * Affiche les détails d'un rendez-vous.
     *
     * @param id L'identifiant du rendez-vous
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des détails du rendez-vous
     */
    @GetMapping("/{id}")
    public String showRendezVousDetails(@PathVariable Long id, Model model) {
        RendezVous rendezVous = rendezVousService.getRendezVousById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous invalide avec l'ID: " + id));

        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("statuts", RendezVous.StatutRendezVous.values());
        return "rendezvous/details";
    }

    /**
     * Change le statut d'un rendez-vous.
     *
     * @param id L'identifiant du rendez-vous
     * @param statut Le nouveau statut
     * @return Redirection vers les détails du rendez-vous
     */
    @PostMapping("/{id}/statut")
    public String updateStatut(@PathVariable Long id, @RequestParam RendezVous.StatutRendezVous statut) {
        rendezVousService.updateRendezVousStatus(id, statut);
        return "redirect:/rendezvous/" + id;
    }

    /**
     * Affiche les rendez-vous pour un patient spécifique.
     *
     * @param patientId L'identifiant du patient
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des rendez-vous du patient
     */
    @GetMapping("/patient/{patientId}")
    public String showPatientRendezVous(@PathVariable Long patientId, Model model) {
        List<RendezVous> rendezVous = rendezVousService.findByPatient(patientId);

        Patient patient = patientService.getPatientById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + patientId));

        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("patient", patient);

        return "rendezvous/patient";
    }

    /**
     * Affiche les rendez-vous pour un médecin spécifique.
     *
     * @param medecinId L'identifiant du médecin
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des rendez-vous du médecin
     */
    @GetMapping("/medecin/{medecinId}")
    public String showMedecinRendezVous(@PathVariable Long medecinId, Model model) {
        List<RendezVous> rendezVous = rendezVousService.findByMedecin(medecinId);

        Medecin medecin = medecinService.getMedecinById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + medecinId));

        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("medecin", medecin);

        return "rendezvous/medecin";
    }

    /**
     * Prépare le modèle pour le formulaire de rendez-vous.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param rendezVous Le rendez-vous à éditer ou créer
     * @param isEdit Indique s'il s'agit d'une édition ou création
     */
    private void prepareFormModel(Model model, RendezVous rendezVous, boolean isEdit) {
        List<Medecin> medecins = medecinService.findAvailableMedecins();
        List<Patient> patients = patientService.getAllPatients();

        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("medecins", medecins);
        model.addAttribute("patients", patients);
        model.addAttribute("statuts", RendezVous.StatutRendezVous.values());
        model.addAttribute("title", isEdit ? "Modifier le rendez-vous" : "Nouveau rendez-vous");
        model.addAttribute("isEdit", isEdit);
    }
}