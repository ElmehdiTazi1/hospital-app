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
import java.util.Map;

/**
 * Contrôleur responsable de la gestion des départements via l'interface web.
 */
@Controller
@RequestMapping("/departements")
@AllArgsConstructor
@Slf4j
public class DepartementController {

    private final DepartementService departementService;
    private final MedecinService medecinService;

    /**
     * Affiche la liste des départements avec pagination et filtrage.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param page Numéro de page (0-indexed)
     * @param size Taille de la page
     * @param keyword Mot-clé de recherche
     * @return Le nom de la vue à afficher
     */
    @GetMapping
    public String listDepartements(Model model,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "5") int size,
                                   @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Departement> pageDepartements = departementService.findByNomContains(keyword, PageRequest.of(page, size));

        model.addAttribute("listDepartements", pageDepartements.getContent());
        model.addAttribute("pages", new int[pageDepartements.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        // Statistiques
        Map<String, Long> medecinsByDept = departementService.countMedecinsByDepartement();
        model.addAttribute("medecinsByDept", medecinsByDept);

        return "departements/list";
    }

    /**
     * Affiche le formulaire d'ajout d'un nouveau département.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire
     */
    @GetMapping("/nouveau")
    public String showAddForm(Model model) {
        // Liste des médecins disponibles pour être chef de département
        List<Medecin> medecins = medecinService.findAvailableMedecins();

        model.addAttribute("departement", new Departement());
        model.addAttribute("medecins", medecins);
        model.addAttribute("title", "Ajouter un département");
        model.addAttribute("isEdit", false);

        return "departements/form";
    }

    /**
     * Affiche le formulaire d'édition d'un département existant.
     *
     * @param id L'identifiant du département à éditer
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire ou redirection si le département n'existe pas
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Departement departement = departementService.getDepartementById(id)
                .orElseThrow(() -> new IllegalArgumentException("Département invalide avec l'ID: " + id));

        // Liste des médecins disponibles pour être chef de département
        List<Medecin> medecins = medecinService.findAvailableMedecins();

        model.addAttribute("departement", departement);
        model.addAttribute("medecins", medecins);
        model.addAttribute("title", "Modifier le département");
        model.addAttribute("isEdit", true);

        return "departements/form";
    }

    /**
     * Traite la soumission du formulaire d'ajout ou d'édition d'un département.
     *
     * @param departement Le département à sauvegarder
     * @param bindingResult Résultats de la validation du formulaire
     * @param chefId ID du médecin chef sélectionné
     * @return Redirection vers la liste des départements ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/save")
    public String saveDepartement(@Valid Departement departement,
                                  BindingResult bindingResult,
                                  @RequestParam(required = false) Long chefId,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            List<Medecin> medecins = medecinService.findAvailableMedecins();
            model.addAttribute("medecins", medecins);
            model.addAttribute("title", departement.getId() == null ? "Ajouter un département" : "Modifier le département");
            model.addAttribute("isEdit", departement.getId() != null);
            return "departements/form";
        }

        // Sauvegarde du département
        Departement savedDepartement = departementService.saveDepartement(departement);

        // Associer le chef de département si spécifié
        if (chefId != null) {
            departementService.assignChefDepartement(savedDepartement.getId(), chefId);
        }

        return "redirect:/departements?keyword=" + departement.getNom();
    }

    /**
     * Supprime un département.
     *
     * @param id L'identifiant du département à supprimer
     * @param keyword Mot-clé de recherche pour la redirection
     * @param page Page actuelle pour la redirection
     * @return Redirection vers la liste des départements
     */
    @GetMapping("/delete/{id}")
    public String deleteDepartement(@PathVariable Long id,
                                    @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                    @RequestParam(name = "page", defaultValue = "0") int page) {

        departementService.deleteDepartement(id);
        return "redirect:/departements?page=" + page + "&keyword=" + keyword;
    }

    /**
     * Affiche les détails d'un département.
     *
     * @param id L'identifiant du département
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des détails du département
     */
    @GetMapping("/{id}")
    public String showDepartementDetails(@PathVariable Long id, Model model) {
        Departement departement = departementService.getDepartementById(id)
                .orElseThrow(() -> new IllegalArgumentException("Département invalide avec l'ID: " + id));

        // Récupérer les médecins du département
        List<Medecin> medecins = medecinService.findByDepartement(id);

        model.addAttribute("departement", departement);
        model.addAttribute("medecins", medecins);

        return "departements/details";
    }

    /**
     * Active ou désactive un département.
     *
     * @param id L'identifiant du département
     * @param actif Nouveau statut d'activation
     * @return Redirection vers les détails du département
     */
    @PostMapping("/{id}/statut")
    public String toggleStatus(@PathVariable Long id, @RequestParam boolean actif) {
        departementService.toggleDepartementStatus(id, actif);
        return "redirect:/departements/" + id;
    }

    /**
     * Assigne un médecin comme chef de département.
     *
     * @param departementId L'identifiant du département
     * @param medecinId L'identifiant du médecin
     * @return Redirection vers les détails du département
     */
    @PostMapping("/{departementId}/chef")
    public String assignerChefDepartement(@PathVariable Long departementId,
                                          @RequestParam Long medecinId) {

        departementService.assignChefDepartement(departementId, medecinId);
        return "redirect:/departements/" + departementId;
    }
}