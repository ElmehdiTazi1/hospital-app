package org.mql.hospital.web.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.entities.RendezVous;
import org.mql.hospital.service.DashboardService;
import org.mql.hospital.service.MedicamentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la page d'accueil avec statistiques.
 */
@Controller
@AllArgsConstructor
@Slf4j
public class HomeController {

    private final DashboardService dashboardService;
    private final MedicamentService medicamentService;

    /**
     * Affiche la page d'accueil avec les statistiques basiques.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue à afficher
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        log.info("Affichage de la page d'accueil avec les statistiques");

        try {
            // Récupérer les statistiques générales
            Map<String, Long> stats = dashboardService.getGeneralStatistics();
            model.addAllAttributes(stats);

            // Récupérer le nombre de médicaments en alerte
            long medicamentsEnAlerte = dashboardService.getMedicamentsEnAlerteCount();
            model.addAttribute("medicamentsEnAlerte", medicamentsEnAlerte);

            // Récupérer le nombre de médicaments proches de l'expiration (30 jours)
            long medicamentsExpiration = dashboardService.getMedicamentsExpirationProche(30);
            model.addAttribute("medicamentsExpiration", medicamentsExpiration);

            // Récupérer les rendez-vous du jour
            List<RendezVous> rendezVousDuJour = (List<RendezVous>) dashboardService.getRendezVousDuJour();
            model.addAttribute("rendezVousDuJour", rendezVousDuJour);

            // Récupérer les médicaments en alerte pour l'encart d'alertes
            List<Medicament> alerteMedicaments = medicamentService.findMedicamentsEnAlerte();
            model.addAttribute("alerteMedicaments", alerteMedicaments);
        } catch (Exception e) {
            // En cas d'erreur avec les services, log l'erreur mais continuer l'affichage
            log.error("Erreur lors du chargement des statistiques: {}", e.getMessage());
            model.addAttribute("statsError", true);
        }

        return "home";
    }
}