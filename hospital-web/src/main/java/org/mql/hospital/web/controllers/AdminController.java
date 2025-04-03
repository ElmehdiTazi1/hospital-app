package org.mql.hospital.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medecin;
import org.mql.hospital.service.*;
import org.mql.hospital.service.UserRegistrationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour gérer les fonctionnalités d'administration.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j

public class AdminController {

    private final DashboardService dashboardService;
    private final PatientService patientService;
    private final MedecinService medecinService;
    private final DepartementService departementService;
    private final UserService userService;

    /**
     * Affiche le tableau de bord administrateur.
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        log.info("Affichage du tableau de bord administrateur");

        // Récupérer les statistiques pour le tableau de bord
        Map<String, Long> stats = dashboardService.getGeneralStatistics();
        model.addAllAttributes(stats);

        // Statistiques supplémentaires spécifiques à l'admin
        model.addAttribute("medecinsSansCompte", countMedecinsSansCompte());

        return "admin/dashboard"; // Changé de "dashboard" à "admin/dashboard"
    }

    /**
     * Affiche la liste des médecins pour la gestion des comptes.
     */
    @GetMapping("/medecins")
    public String listMedecins(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Medecin> pageMedecins = medecinService.findByNomContains(keyword, PageRequest.of(page, size));

        model.addAttribute("listMedecins", pageMedecins.getContent());
        model.addAttribute("pages", new int[pageMedecins.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("accountCreationMode", true); // Pour différencier le comportement de la vue

        return "admin/medecins-list";
    }

    /**
     * Affiche le formulaire de création de compte pour un médecin.
     */
    @GetMapping("/medecins/{id}/create-account")
    public String showCreateAccountForm(@PathVariable Long id, Model model) {
        Medecin medecin = medecinService.getMedecinById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));

        // Vérifier si le médecin a déjà un compte
        if (medecin.getUser() != null) {
            return "redirect:/admin/medecins?error=Le médecin possède déjà un compte";
        }

        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setNom(medecin.getNom() + " " + medecin.getPrenom());
        userDto.setEmail(medecin.getEmail()); // Utiliser l'email du médecin s'il existe

        model.addAttribute("user", userDto);
        model.addAttribute("medecin", medecin);

        return "admin/create-medecin-account";
    }

    /**
     * Traite la création d'un compte pour un médecin.
     */
    @PostMapping("/medecins/{id}/create-account")
    public String createMedecinAccount(
            @PathVariable Long id,
            @Valid @ModelAttribute("user") UserRegistrationDto userDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Vérifier si les mots de passe correspondent
        if (!userDto.getPassword().equals(userDto.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.user", "Les mots de passe ne correspondent pas");
        }

        if (result.hasErrors()) {
            Medecin medecin = medecinService.getMedecinById(id).orElse(null);
            model.addAttribute("medecin", medecin);
            return "admin/create-medecin-account";
        }

        try {
            // Créer le compte utilisateur pour le médecin
            userService.registerNewMedecinUser(userDto, id);
            redirectAttributes.addFlashAttribute("successMessage", "Compte médecin créé avec succès!");
            return "redirect:/admin/medecins";
        } catch (Exception e) {
            log.error("Erreur lors de la création du compte médecin: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/medecins/" + id + "/create-account";
        }
    }

    /**
     * Gestion des utilisateurs - liste tous les utilisateurs.
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        // Logique pour lister tous les utilisateurs
        return "admin/users-list";
    }

    /**
     * Compte le nombre de médecins sans compte utilisateur.
     */
    private long countMedecinsSansCompte() {
        List<Medecin> medecins = medecinService.getAllMedecins();
        return medecins.stream().filter(m -> m.getUser() == null).count();
    }
}