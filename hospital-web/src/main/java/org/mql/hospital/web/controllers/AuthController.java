package org.mql.hospital.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.User;
import org.mql.hospital.service.UserService;
import org.mql.hospital.service.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        // Vérifier si l'utilisateur est déjà connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

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
            userService.registerNewPatientUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

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

        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}