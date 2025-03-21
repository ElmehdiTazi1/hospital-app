package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.*;
import org.mql.hospital.service.MedecinService;
import org.mql.hospital.service.MedicamentService;
import org.mql.hospital.service.PatientService;
import org.mql.hospital.service.PrescriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Contrôleur responsable de la gestion des prescriptions via l'interface web.
 */
@Controller
@RequestMapping("/prescriptions")
@AllArgsConstructor
@Slf4j
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PatientService patientService;
    private final MedecinService medecinService;
    private final MedicamentService medicamentService;

    /**
     * Affiche la liste des prescriptions avec pagination et filtrage.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param page Numéro de page (0-indexed)
     * @param size Taille de la page
     * @param patientId ID du patient pour filtrer (optionnel)
     * @param medecinId ID du médecin pour filtrer (optionnel)
     * @param statut Statut des prescriptions pour filtrer (optionnel)
     * @param dateDebut Date de début pour filtrer (optionnel)
     * @param dateFin Date de fin pour filtrer (optionnel)
     * @return Le nom de la vue à afficher
     */
    @GetMapping
    public String listPrescriptions(Model model,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size,
                                    @RequestParam(name = "patientId", required = false) Long patientId,
                                    @RequestParam(name = "medecinId", required = false) Long medecinId,
                                    @RequestParam(name = "statut", required = false) Prescription.StatutPrescription statut,
                                    @RequestParam(name = "dateDebut", required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
                                    @RequestParam(name = "dateFin", required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {

        Page<Prescription> pagePrescriptions = prescriptionService.searchPrescriptions(
                patientId, medecinId, statut, dateDebut, dateFin, PageRequest.of(page, size));

        // Récupérer la liste des patients et médecins pour les filtres
        List<Patient> patients = patientService.getAllPatients();
        List<Medecin> medecins = medecinService.getAllMedecins();

        model.addAttribute("listPrescriptions", pagePrescriptions.getContent());
        model.addAttribute("pages", new int[pagePrescriptions.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("patients", patients);
        model.addAttribute("medecins", medecins);
        model.addAttribute("statuts", Prescription.StatutPrescription.values());
        model.addAttribute("patientId", patientId);
        model.addAttribute("medecinId", medecinId);
        model.addAttribute("statut", statut);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);

        return "prescriptions/list";
    }

    /**
     * Affiche le formulaire d'ajout d'une nouvelle prescription.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire
     */
    @GetMapping("/nouvelle")
    public String showAddForm(Model model) {
        Prescription prescription = new Prescription();
        prescription.setDatePrescription(new Date());
        prescription.setDureeValidite(30); // 30 jours par défaut
        prescription.setStatut(Prescription.StatutPrescription.ACTIVE);

        prepareFormModel(model, prescription, false);
        return "prescriptions/form";
    }

    /**
     * Affiche le formulaire d'édition d'une prescription existante.
     *
     * @param id L'identifiant de la prescription à éditer
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire ou redirection si la prescription n'existe pas
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription invalide avec l'ID: " + id));

        prepareFormModel(model, prescription, true);
        return "prescriptions/form";
    }

    /**
     * Traite la soumission du formulaire d'ajout ou d'édition d'une prescription.
     *
     * @param prescription La prescription à sauvegarder
     * @param bindingResult Résultats de la validation du formulaire
     * @param patientId ID du patient sélectionné
     * @param medecinId ID du médecin sélectionné
     * @return Redirection vers les détails de la prescription ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/save")
    public String savePrescription(@Valid Prescription prescription,
                                   BindingResult bindingResult,
                                   @RequestParam Long patientId,
                                   @RequestParam Long medecinId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // Associer le patient et le médecin
        try {
            Patient patient = patientService.getPatientById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + patientId));
            prescription.setPatient(patient);

            Medecin medecin = medecinService.getMedecinById(medecinId)
                    .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + medecinId));
            prescription.setMedecin(medecin);

            // Initialiser les lignes de prescription si nécessaire
            if (prescription.getLignePrescriptions() == null) {
                prescription.setLignePrescriptions(new HashSet<>());
            }

            if (bindingResult.hasErrors()) {
                prepareFormModel(model, prescription, prescription.getId() != null);
                return "prescriptions/form";
            }

            Prescription savedPrescription = prescriptionService.savePrescription(prescription);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription sauvegardée avec succès.");

            return "redirect:/prescriptions/" + savedPrescription.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepareFormModel(model, prescription, prescription.getId() != null);
            return "prescriptions/form";
        }
    }

    /**
     * Supprime une prescription.
     *
     * @param id L'identifiant de la prescription à supprimer
     * @return Redirection vers la liste des prescriptions
     */
    @GetMapping("/delete/{id}")
    public String deletePrescription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.deletePrescription(id);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/prescriptions";
    }

    /**
     * Affiche les détails d'une prescription.
     *
     * @param id L'identifiant de la prescription
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des détails de la prescription
     */
    @GetMapping("/{id}")
    public String showPrescriptionDetails(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription invalide avec l'ID: " + id));

        boolean isValide = prescriptionService.isPrescriptionValide(id);

        model.addAttribute("prescription", prescription);
        model.addAttribute("isValide", isValide);
        model.addAttribute("statuts", Prescription.StatutPrescription.values());
        model.addAttribute("lignePrescription", new LignePrescription());
        model.addAttribute("medicaments", medicamentService.findAvailableMedicaments());
        model.addAttribute("momentsPrise", LignePrescription.MomentPrise.values());

        return "prescriptions/details";
    }

    /**
     * Affiche les prescriptions pour un patient spécifique.
     *
     * @param patientId L'identifiant du patient
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des prescriptions du patient
     */
    @GetMapping("/patient/{patientId}")
    public String showPatientPrescriptions(@PathVariable Long patientId, Model model) {
        List<Prescription> prescriptions = prescriptionService.findByPatient(patientId);

        Patient patient = patientService.getPatientById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + patientId));

        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("patient", patient);
        model.addAttribute("activePrescriptions", prescriptionService.findActivePrescriptionsByPatient(patientId));

        return "prescriptions/patient";
    }

    /**
     * Ajoute une ligne de prescription à une prescription existante.
     *
     * @param prescriptionId L'identifiant de la prescription
     * @param lignePrescription La ligne de prescription à ajouter
     * @return Redirection vers les détails de la prescription
     */
    @PostMapping("/{prescriptionId}/lignes/add")
    public String addLignePrescription(@PathVariable Long prescriptionId,
                                       @Valid LignePrescription lignePrescription,
                                       BindingResult bindingResult,
                                       @RequestParam Long medicamentId,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Associer le médicament
            Medicament medicament = medicamentService.getMedicamentById(medicamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Médicament invalide avec l'ID: " + medicamentId));
            lignePrescription.setMedicament(medicament);

            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur de validation: Veuillez vérifier les données saisies.");
                return "redirect:/prescriptions/" + prescriptionId;
            }

            prescriptionService.addLignePrescription(prescriptionId, lignePrescription);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament ajouté à la prescription avec succès.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/prescriptions/" + prescriptionId;
    }

    /**
     * Supprime une ligne de prescription.
     *
     * @param prescriptionId L'identifiant de la prescription
     * @param ligneId L'identifiant de la ligne à supprimer
     * @return Redirection vers les détails de la prescription
     */
    @GetMapping("/{prescriptionId}/lignes/delete/{ligneId}")
    public String deleteLignePrescription(@PathVariable Long prescriptionId,
                                          @PathVariable Long ligneId,
                                          RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.deleteLignePrescription(ligneId);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament retiré de la prescription avec succès.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/prescriptions/" + prescriptionId;
    }

    /**
     * Change le statut d'une prescription.
     *
     * @param id L'identifiant de la prescription
     * @param statut Le nouveau statut
     * @return Redirection vers les détails de la prescription
     */
    @PostMapping("/{id}/statut")
    public String updateStatut(@PathVariable Long id,
                               @RequestParam Prescription.StatutPrescription statut,
                               RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.updatePrescriptionStatus(id, statut);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Statut de la prescription mis à jour: " + statut.name());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/prescriptions/" + id;
    }

    /**
     * Prépare le modèle pour le formulaire de prescription.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param prescription La prescription à éditer ou créer
     * @param isEdit Indique s'il s'agit d'une édition ou création
     */
    private void prepareFormModel(Model model, Prescription prescription, boolean isEdit) {
        List<Patient> patients = patientService.getAllPatients();
        List<Medecin> medecins = medecinService.getAllMedecins();

        model.addAttribute("prescription", prescription);
        model.addAttribute("patients", patients);
        model.addAttribute("medecins", medecins);
        model.addAttribute("statuts", Prescription.StatutPrescription.values());
        model.addAttribute("title", isEdit ? "Modifier la prescription" : "Nouvelle prescription");
        model.addAttribute("isEdit", isEdit);
    }
}