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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour le tableau de bord et les statistiques.
 */
@Controller
@RequestMapping("/dashboard")
@AllArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;
    private final MedicamentService medicamentService;

    /**
     * Affiche la page du tableau de bord avec les statistiques.
     *
     * @param model Le modèle pour les données à envoyer à la vue
     * @return Le nom de la vue à afficher
     */
    @GetMapping
    public String dashboard(Model model) {
        log.info("Affichage du tableau de bord avec les statistiques");

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

        return "dashboard/main";
    }
}