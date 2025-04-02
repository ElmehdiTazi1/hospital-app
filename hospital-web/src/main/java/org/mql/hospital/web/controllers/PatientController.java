package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrôleur responsable de la gestion des patients via l'interface web.
 * Gère les requêtes HTTP liées aux opérations sur les patients.
 */
@AllArgsConstructor
@Controller
@Slf4j
public class PatientController {
    // Utilisation du service au lieu du repository directement
    private PatientService patientService;

    /**
     * Affiche la liste des patients avec pagination et filtrage.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param p Numéro de page (0-indexed)
     * @param s Taille de la page
     * @param kw Mot-clé de recherche
     * @return Le nom de la vue à afficher
     */
    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int p,
                        @RequestParam(name = "size", defaultValue = "4") int s,
                        @RequestParam(name = "keyword", defaultValue = "") String kw
    ){
        Page<Patient> pagePatients = patientService.findByNomContains(kw, PageRequest.of(p, s));
        model.addAttribute("listPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        return "patients/list";
    }

    /**
     * Traite la suppression d'un patient.
     *
     * @param id ID du patient à supprimer
     * @param keyword Mot-clé courant pour la redirection
     * @param page Page courante pour la redirection
     * @return Redirection vers la liste des patients
     */
    @GetMapping("/delete")
    public String delete(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page){
        patientService.deletePatient(id);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    /**
     * Affiche le formulaire de création d'un patient.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire
     */
    @GetMapping("/formPatients")
    public String formPatient(Model model){
        model.addAttribute("patient", new Patient());
        model.addAttribute("title", "Ajouter le patient");
        return "patients/form";
    }

    /**
     * Traite la sauvegarde d'un patient (création ou mise à jour).
     *
     * @param patient Le patient à sauvegarder
     * @param bindingResult Résultat de la validation
     * @return Redirection ou affichage du formulaire en cas d'erreur
     */
    @PostMapping("/savePatient")
    public String savePatient(@Valid Patient patient, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "patients/form";
        }
        patientService.savePatient(patient);
        return "redirect:/index?keyword=" + patient.getNom();
    }

    /**
     * Affiche le formulaire d'édition d'un patient.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param id ID du patient à éditer
     * @return Le nom de la vue du formulaire d'édition
     */
    @GetMapping("/editPatient")
    public String editPatient(Model model, @RequestParam(name = "id") Long id){
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient invalide avec l'ID: " + id));
        model.addAttribute("patient", patient);
        model.addAttribute("title", "Modifier le patient");

        return "patients/form";
    }
}