package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Departement;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.service.DepartementService;
import org.mql.hospital.service.MedecinService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur responsable de la gestion des médecins via l'interface web.
 */
@Controller
@RequestMapping("/medecins")
@AllArgsConstructor
@Slf4j
public class MedecinController {

    private final MedecinService medecinService;
    private final DepartementService departementService;

    /**
     * Affiche la liste des médecins avec pagination et filtrage.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param page Numéro de page (0-indexed)
     * @param size Taille de la page
     * @param keyword Mot-clé de recherche
     * @return Le nom de la vue à afficher
     */
    @GetMapping
    public String listMedecins(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Medecin> pageMedecins = medecinService.findByNomContains(keyword, PageRequest.of(page, size));

        model.addAttribute("listMedecins", pageMedecins.getContent());
        model.addAttribute("pages", new int[pageMedecins.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "medecins/list";
    }

    /**
     * Affiche le formulaire d'ajout d'un nouveau médecin.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire
     */
    @GetMapping("/nouveau")
    public String showAddForm(Model model) {
        List<Departement> departements = departementService.findActiveDepartements();

        model.addAttribute("medecin", new Medecin());
        model.addAttribute("departements", departements);
        model.addAttribute("title", "Ajouter un médecin");
        model.addAttribute("isEdit", false);

        return "medecins/form";
    }

    /**
     * Affiche le formulaire d'édition d'un médecin existant.
     *
     * @param id L'identifiant du médecin à éditer
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire ou redirection si le médecin n'existe pas
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Medecin medecin = medecinService.getMedecinById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + id));

        List<Departement> departements = departementService.findActiveDepartements();

        model.addAttribute("medecin", medecin);
        model.addAttribute("departements", departements);
        model.addAttribute("title", "Modifier le médecin");
        model.addAttribute("isEdit", true);

        return "medecins/form";
    }

    /**
     * Traite la soumission du formulaire d'ajout ou d'édition d'un médecin.
     *
     * @param medecin Le médecin à sauvegarder
     * @param bindingResult Résultats de la validation du formulaire
     * @param departementId ID du département sélectionné
     * @return Redirection vers la liste des médecins ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/save")
    public String saveMedecin(@Valid Medecin medecin,
                              BindingResult bindingResult,
                              @RequestParam(required = false) Long departementId,
                              Model model) {

        if (bindingResult.hasErrors()) {
            List<Departement> departements = departementService.findActiveDepartements();
            model.addAttribute("departements", departements);
            model.addAttribute("title", medecin.getId() == null ? "Ajouter un médecin" : "Modifier le médecin");
            model.addAttribute("isEdit", medecin.getId() != null);
            return "medecins/form";
        }

        // Associer le département sélectionné
        if (departementId != null) {
            departementService.getDepartementById(departementId).ifPresent(medecin::setDepartement);
        }

        medecinService.saveMedecin(medecin);
        return "redirect:/medecins?keyword=" + medecin.getNom();
    }

    /**
     * Supprime un médecin.
     *
     * @param id L'identifiant du médecin à supprimer
     * @param keyword Mot-clé de recherche pour la redirection
     * @param page Page actuelle pour la redirection
     * @return Redirection vers la liste des médecins
     */
    @GetMapping("/delete/{id}")
    public String deleteMedecin(@PathVariable Long id,
                                @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                @RequestParam(name = "page", defaultValue = "0") int page) {

        medecinService.deleteMedecin(id);
        return "redirect:/medecins?page=" + page + "&keyword=" + keyword;
    }

    /**
     * Affiche les détails d'un médecin.
     *
     * @param id L'identifiant du médecin
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des détails du médecin
     */
    @GetMapping("/{id}")
    public String showMedecinDetails(@PathVariable Long id, Model model) {
        Medecin medecin = medecinService.getMedecinById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + id));

        model.addAttribute("medecin", medecin);
        return "medecins/details";
    }

    /**
     * Change le statut de disponibilité d'un médecin.
     *
     * @param id L'identifiant du médecin
     * @param disponible Nouveau statut de disponibilité
     * @return Redirection vers les détails du médecin
     */
    @PostMapping("/{id}/disponibilite")
    public String toggleDisponibilite(@PathVariable Long id, @RequestParam boolean disponible) {
        Medecin medecin = medecinService.getMedecinById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin invalide avec l'ID: " + id));

        medecin.setDisponible(disponible);
        medecinService.saveMedecin(medecin);

        return "redirect:/medecins/" + id;
    }

    /**
     * Assigne un médecin comme chef de département.
     *
     * @param medecinId L'identifiant du médecin
     * @param departementId L'identifiant du département
     * @return Redirection vers les détails du médecin
     */
    @PostMapping("/{medecinId}/chef-departement")
    public String assignerChefDepartement(@PathVariable Long medecinId,
                                          @RequestParam Long departementId) {

        medecinService.setChefDepartement(medecinId, departementId);
        return "redirect:/medecins/" + medecinId;
    }
}