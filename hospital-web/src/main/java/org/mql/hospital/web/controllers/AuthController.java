package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.User;
import org.mql.hospital.service.UserService;
import org.mql.hospital.service.UserRegistrationDto;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour gérer l'authentification des utilisateurs.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Affiche la page de connexion.
     */
    @GetMapping("/login")
    public String login() {
        // Vérifier si l'utilisateur est déjà connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    /**
     * Affiche la page d'inscription pour les patients.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Vérifier si l'utilisateur est déjà connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    /**
     * Traite l'inscription d'un nouveau patient.
     */
    @PostMapping("/register")
    public String registerUserAccount(
            @ModelAttribute("user") @Valid UserRegistrationDto userDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Vérifier si les mots de passe correspondent
        if (!userDto.getPassword().equals(userDto.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.user", "Les mots de passe ne correspondent pas");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            // Enregistrer le nouvel utilisateur patient
            userService.registerNewPatientUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Redirige l'utilisateur vers sa page d'accueil appropriée après la connexion.
     */
    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = userService.getUserByUsername(username).orElse(null);

            if (user != null) {
                if (user.isAdmin()) {
                    return "redirect:/admin/dashboard";
                } else if (user.isMedecin()) {
                    return "redirect:/medecin/dashboard";
                } else if (user.isPatient()) {
                    return "redirect:/patient/dashboard";
                }
            }
        }

        // Rediriger vers la page de connexion si l'utilisateur n'est pas authentifié ou n'a pas de rôle reconnu
        return "redirect:/login";
    }

    /**
     * Affiche la page d'accès refusé.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}