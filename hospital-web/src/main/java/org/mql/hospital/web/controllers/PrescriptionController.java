package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.*;
import org.mql.hospital.service.LignePrescriptionService;
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
    private final LignePrescriptionService lignePrescriptionService;
    private final PatientService patientService;
    private final MedecinService medecinService;
    private final MedicamentService medicamentService;

    /**
     * Affiche la liste des prescriptions avec pagination et filtrage.
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

        try {
            // Recherche des prescriptions avec les critères fournis
            Page<Prescription> pagePrescriptions = prescriptionService.searchPrescriptions(
                    patientId, medecinId, statut, dateDebut, dateFin, PageRequest.of(page, size));

            // Récupération des listes pour les filtres
            List<Patient> patients = patientService.getAllPatients();
            List<Medecin> medecins = medecinService.getAllMedecins();

            // Ajout des attributs au modèle
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

            log.info("Affichage de la liste des prescriptions. Nombre de résultats: {}", pagePrescriptions.getTotalElements());
            return "prescriptions/list";
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des prescriptions", e);
            model.addAttribute("errorMessage", "Une erreur est survenue : " + e.getMessage());
            return "prescriptions/list";
        }
    }

    /**
     * Affiche le formulaire d'ajout d'une nouvelle prescription.
     */
    @GetMapping("/nouvelle")
    public String showAddForm(Model model) {
        try {
            // Création d'une nouvelle instance de prescription avec des valeurs par défaut
            Prescription prescription = new Prescription();
            prescription.setDatePrescription(new Date());
            prescription.setDureeValidite(30); // 30 jours par défaut
            prescription.setStatut(Prescription.StatutPrescription.ACTIVE);

            // Préparation du modèle pour le formulaire
            prepareFormModel(model, prescription, false);
            log.info("Affichage du formulaire d'ajout de prescription");
            return "prescriptions/form";
        } catch (Exception e) {
            log.error("Erreur lors de l'affichage du formulaire d'ajout", e);
            model.addAttribute("errorMessage", "Une erreur est survenue : " + e.getMessage());
            return "prescriptions/form";
        }
    }

    /**
     * Affiche le formulaire d'édition d'une prescription existante.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Récupération de la prescription
            Prescription prescription = prescriptionService.getPrescriptionById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Prescription invalide avec l'ID: " + id));

            // Vérification que la prescription est active (seules les prescriptions actives peuvent être modifiées)
            if (prescription.getStatut() != Prescription.StatutPrescription.ACTIVE) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Impossible de modifier une prescription qui n'est pas active");
                return "redirect:/prescriptions/" + id;
            }

            // Préparation du modèle pour le formulaire
            prepareFormModel(model, prescription, true);
            log.info("Affichage du formulaire d'édition pour la prescription ID: {}", id);
            return "prescriptions/form";
        } catch (IllegalArgumentException e) {
            log.error("Prescription non trouvée - ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/prescriptions";
        } catch (Exception e) {
            log.error("Erreur lors de l'affichage du formulaire d'édition", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue : " + e.getMessage());
            return "redirect:/prescriptions";
        }
    }

    /**
     * Traite la soumission du formulaire d'ajout ou d'édition d'une prescription.
     */
    @PostMapping("/save")
    public String savePrescription(@Valid Prescription prescription,
                                   BindingResult bindingResult,
                                   @RequestParam Long patientId,
                                   @RequestParam Long medecinId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        try {
            // Association des entités liées
            Patient patient = patientService.getPatientById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + patientId));
            prescription.setPatient(patient);

            Medecin medecin = medecinService.getMedecinById(medecinId)
                    .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + medecinId));
            prescription.setMedecin(medecin);

            // Initialisation de la collection de lignes si nécessaire
            if (prescription.getLignePrescriptions() == null) {
                prescription.setLignePrescriptions(new HashSet<>());
            }

            // Validation du formulaire
            if (bindingResult.hasErrors()) {
                prepareFormModel(model, prescription, prescription.getId() != null);
                return "prescriptions/form";
            }

            // Sauvegarde de la prescription
            Prescription savedPrescription = prescriptionService.savePrescription(prescription);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription sauvegardée avec succès.");
            log.info("Prescription {} sauvegardée avec succès", savedPrescription.getId());

            return "redirect:/prescriptions/" + savedPrescription.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Erreur de validation lors de la sauvegarde de la prescription", e);
            model.addAttribute("errorMessage", e.getMessage());
            prepareFormModel(model, prescription, prescription.getId() != null);
            return "prescriptions/form";
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la sauvegarde de la prescription", e);
            model.addAttribute("errorMessage", "Une erreur inattendue est survenue : " + e.getMessage());
            prepareFormModel(model, prescription, prescription.getId() != null);
            return "prescriptions/form";
        }
    }

    /**
     * Supprime une prescription.
     */
    @GetMapping("/delete/{id}")
    public String deletePrescription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.deletePrescription(id);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription supprimée avec succès.");
            log.info("Prescription ID: {} supprimée avec succès", id);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la prescription ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/prescriptions";
    }

    /**
     * Affiche les détails d'une prescription.
     */
    @GetMapping("/{id}")
    public String showPrescriptionDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Récupération de la prescription avec ses lignes
            Prescription prescription = prescriptionService.getPrescriptionById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Prescription invalide avec l'ID: " + id));

            // Vérification de la validité de la prescription
            boolean isValide = prescriptionService.isPrescriptionValide(id);

            // Préparation du modèle pour la vue de détails
            model.addAttribute("prescription", prescription);
            model.addAttribute("isValide", isValide);
            model.addAttribute("statuts", Prescription.StatutPrescription.values());
            model.addAttribute("lignePrescription", new LignePrescription());
            model.addAttribute("medicaments", medicamentService.findAvailableMedicaments());
            model.addAttribute("momentsPrise", LignePrescription.MomentPrise.values());

            log.info("Affichage des détails de la prescription ID: {}", id);
            return "prescriptions/details";
        } catch (IllegalArgumentException e) {
            log.error("Prescription non trouvée - ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/prescriptions";
        } catch (Exception e) {
            log.error("Erreur lors de l'affichage des détails de la prescription ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue : " + e.getMessage());
            return "redirect:/prescriptions";
        }
    }

    /**
     * Affiche les prescriptions pour un patient spécifique.
     */
    @GetMapping("/patient/{patientId}")
    public String showPatientPrescriptions(@PathVariable Long patientId, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Récupération des prescriptions du patient
            List<Prescription> prescriptions = prescriptionService.findByPatient(patientId);
            List<Prescription> activePrescriptions = prescriptionService.findActivePrescriptionsByPatient(patientId);

            // Récupération du patient
            Patient patient = patientService.getPatientById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + patientId));

            // Préparation du modèle
            model.addAttribute("prescriptions", prescriptions);
            model.addAttribute("patient", patient);
            model.addAttribute("activePrescriptions", activePrescriptions);

            log.info("Affichage des prescriptions pour le patient ID: {}", patientId);
            return "prescriptions/patient";
        } catch (IllegalArgumentException e) {
            log.error("Patient non trouvé - ID: {}", patientId, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/patients";
        } catch (Exception e) {
            log.error("Erreur lors de l'affichage des prescriptions du patient ID: {}", patientId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue : " + e.getMessage());
            return "redirect:/patients";
        }
    }

    /**
     * Ajoute une ligne de prescription à une prescription existante.
     */
    @PostMapping("/{prescriptionId}/lignes/add")
    public String addLignePrescription(@PathVariable Long prescriptionId,
                                       @Valid LignePrescription lignePrescription,
                                       BindingResult bindingResult,
                                       @RequestParam Long medicamentId,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Vérification si la prescription existe et est active
            if (!prescriptionService.getPrescriptionById(prescriptionId).isPresent()) {
                throw new IllegalArgumentException("Prescription non trouvée avec l'ID: " + prescriptionId);
            }

            // Vérification si le médicament existe déjà dans la prescription
            if (lignePrescriptionService.isMedicamentInPrescription(prescriptionId, medicamentId)) {
                throw new IllegalStateException("Ce médicament est déjà présent dans la prescription");
            }

            // Association du médicament
            Medicament medicament = medicamentService.getMedicamentById(medicamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Médicament invalide avec l'ID: " + medicamentId));
            lignePrescription.setMedicament(medicament);

            // Validation du formulaire
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur de validation: Veuillez vérifier les données saisies.");
                return "redirect:/prescriptions/" + prescriptionId;
            }

            // Ajout de la ligne de prescription
            prescriptionService.addLignePrescription(prescriptionId, lignePrescription);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament ajouté à la prescription avec succès.");
            log.info("Ligne de prescription ajoutée avec succès à la prescription ID: {}", prescriptionId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Erreur lors de l'ajout d'une ligne de prescription", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'ajout d'une ligne de prescription", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue est survenue : " + e.getMessage());
        }

        return "redirect:/prescriptions/" + prescriptionId;
    }

    /**
     * Supprime une ligne de prescription.
     */
    @GetMapping("/{prescriptionId}/lignes/delete/{ligneId}")
    public String deleteLignePrescription(@PathVariable Long prescriptionId,
                                          @PathVariable Long ligneId,
                                          RedirectAttributes redirectAttributes) {
        try {
            // Suppression de la ligne
            prescriptionService.deleteLignePrescription(ligneId);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament retiré de la prescription avec succès.");
            log.info("Ligne de prescription ID: {} supprimée avec succès", ligneId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Erreur lors de la suppression de la ligne de prescription ID: {}", ligneId, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de la ligne de prescription ID: {}", ligneId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue est survenue : " + e.getMessage());
        }

        return "redirect:/prescriptions/" + prescriptionId;
    }

    /**
     * Change le statut d'une prescription.
     */
    @PostMapping("/{id}/statut")
    public String updateStatut(@PathVariable Long id,
                               @RequestParam Prescription.StatutPrescription statut,
                               RedirectAttributes redirectAttributes) {
        try {
            // Mise à jour du statut
            prescriptionService.updatePrescriptionStatus(id, statut);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Statut de la prescription mis à jour: " + statut.name());
            log.info("Statut de la prescription ID: {} mis à jour: {}", id, statut);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la mise à jour du statut de la prescription ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour du statut de la prescription ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue est survenue : " + e.getMessage());
        }

        return "redirect:/prescriptions/" + id;
    }

    /**
     * Prépare le modèle pour le formulaire de prescription.
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