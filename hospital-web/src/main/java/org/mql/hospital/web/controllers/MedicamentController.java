package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.service.MedicamentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

/**
 * Contrôleur responsable de la gestion des médicaments via l'interface web.
 */
@Controller
@RequestMapping("/medicaments")
@AllArgsConstructor
@Slf4j
public class MedicamentController {

    private final MedicamentService medicamentService;

    /**
     * Affiche la liste des médicaments avec pagination et filtrage.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @param page Numéro de page (0-indexed)
     * @param size Taille de la page
     * @param keyword Mot-clé de recherche
     * @return Le nom de la vue à afficher
     */
    @GetMapping
    public String listMedicaments(Model model,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Medicament> pageMedicaments = medicamentService.findByNomContains(keyword, PageRequest.of(page, size));

        model.addAttribute("listMedicaments", pageMedicaments.getContent());
        model.addAttribute("pages", new int[pageMedicaments.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        // Statistiques
        model.addAttribute("medicamentsEnAlerte", medicamentService.findMedicamentsEnAlerte().size());

        return "medicaments/list";
    }

    /**
     * Affiche le formulaire d'ajout d'un nouveau médicament.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire
     */
    @GetMapping("/nouveau")
    public String showAddForm(Model model) {
        model.addAttribute("medicament", new Medicament());
        model.addAttribute("title", "Ajouter un médicament");
        model.addAttribute("isEdit", false);

        return "medicaments/form";
    }

    /**
     * Affiche le formulaire d'édition d'un médicament existant.
     *
     * @param id L'identifiant du médicament à éditer
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue du formulaire ou redirection si le médicament n'existe pas
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Medicament medicament = medicamentService.getMedicamentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament invalide avec l'ID: " + id));

        model.addAttribute("medicament", medicament);
        model.addAttribute("title", "Modifier le médicament");
        model.addAttribute("isEdit", true);

        return "medicaments/form";
    }

    /**
     * Traite la soumission du formulaire d'ajout ou d'édition d'un médicament.
     *
     * @param medicament Le médicament à sauvegarder
     * @param bindingResult Résultats de la validation du formulaire
     * @return Redirection vers la liste des médicaments ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/save")
    public String saveMedicament(@Valid Medicament medicament,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("title", medicament.getId() == null ? "Ajouter un médicament" : "Modifier le médicament");
            model.addAttribute("isEdit", medicament.getId() != null);
            return "medicaments/form";
        }

        medicamentService.saveMedicament(medicament);
        return "redirect:/medicaments?keyword=" + medicament.getNom();
    }

    /**
     * Supprime un médicament.
     *
     * @param id L'identifiant du médicament à supprimer
     * @param keyword Mot-clé de recherche pour la redirection
     * @param page Page actuelle pour la redirection
     * @return Redirection vers la liste des médicaments
     */
    @GetMapping("/delete/{id}")
    public String deleteMedicament(@PathVariable Long id,
                                   @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                   @RequestParam(name = "page", defaultValue = "0") int page) {

        medicamentService.deleteMedicament(id);
        return "redirect:/medicaments?page=" + page + "&keyword=" + keyword;
    }

    /**
     * Affiche les détails d'un médicament.
     *
     * @param id L'identifiant du médicament
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des détails du médicament
     */
    @GetMapping("/{id}")
    public String showMedicamentDetails(@PathVariable Long id, Model model) {
        Medicament medicament = medicamentService.getMedicamentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament invalide avec l'ID: " + id));

        model.addAttribute("medicament", medicament);
        return "medicaments/details";
    }

    /**
     * Affiche la page d'alerte de stock.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des médicaments en alerte
     */
    @GetMapping("/alerte")
    public String showAlerteStock(Model model) {
        List<Medicament> medicamentsEnAlerte = medicamentService.findMedicamentsEnAlerte();
        model.addAttribute("medicaments", medicamentsEnAlerte);
        return "medicaments/alerte";
    }

    /**
     * Affiche la page d'alerte d'expiration.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue des médicaments proches de l'expiration
     */
    @GetMapping("/expiration")
    public String showExpirationAlerte(Model model) {
        // On recherche les médicaments qui expirent dans les 30 prochains jours
        Date dateLimit = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        List<Medicament> medicamentsExpirant = medicamentService.findMedicamentsExpiringBefore(dateLimit);
        model.addAttribute("medicaments", medicamentsExpirant);
        model.addAttribute("dateLimit", dateLimit);
        return "medicaments/expiration";
    }

    /**
     * Affiche le formulaire de mise à jour du stock.
     *
     * @param id L'identifiant du médicament
     * @param model Le modèle pour les données à envoyer à la vue
     * @return La vue du formulaire de stock
     */
    @GetMapping("/{id}/stock")
    public String showStockForm(@PathVariable Long id, Model model) {
        Medicament medicament = medicamentService.getMedicamentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament invalide avec l'ID: " + id));

        model.addAttribute("medicament", medicament);
        model.addAttribute("quantite", 0);
        return "medicaments/stock-form";
    }

    /**
     * Traite la soumission du formulaire de mise à jour du stock.
     *
     * @param id L'identifiant du médicament
     * @param quantite La quantité à ajouter/soustraire
     * @return Redirection vers les détails du médicament
     */
    @PostMapping("/{id}/stock")
    public String updateStock(@PathVariable Long id,
                              @RequestParam Integer quantite,
                              RedirectAttributes redirectAttributes) {
        try {
            medicamentService.updateStock(id, quantite);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Stock mis à jour avec succès. Ajout de " + quantite + " unités.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/medicaments/" + id;
    }

    /**
     * Change le statut de disponibilité d'un médicament.
     *
     * @param id L'identifiant du médicament
     * @param disponible Nouveau statut de disponibilité
     * @return Redirection vers les détails du médicament
     */
    @PostMapping("/{id}/disponibilite")
    public String toggleDisponibilite(@PathVariable Long id,
                                      @RequestParam boolean disponible,
                                      RedirectAttributes redirectAttributes) {
        try {
            medicamentService.toggleAvailability(id, disponible);
            String message = disponible ?
                    "Le médicament est maintenant disponible." :
                    "Le médicament est maintenant indisponible.";
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/medicaments/" + id;
    }
}