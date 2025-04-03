package org.mql.hospital.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour les statistiques et tableaux de bord.
 * Centralise les différentes métriques utilisées dans le dashboard.
 */
public interface DashboardService {

    /**
     * Récupère les statistiques générales pour le tableau de bord.
     *
     * @return Une map contenant les différentes statistiques (nombre de patients, médecins, etc.)
     */
    Map<String, Long> getGeneralStatistics();

    /**
     * Récupère le nombre de médicaments en alerte de stock.
     *
     * @return Le nombre de médicaments dont le stock est inférieur ou égal au seuil d'alerte
     */
    long getMedicamentsEnAlerteCount();

    /**
     * Récupère le nombre de médicaments proche de la date d'expiration.
     *
     * @param joursLimite Le nombre de jours avant expiration pour considérer un médicament comme "bientôt expiré"
     * @return Le nombre de médicaments qui vont expirer dans les jours spécifiés
     */
    long getMedicamentsExpirationProche(int joursLimite);

    /**
     * Récupère le nombre de rendez-vous prévus pour aujourd'hui.
     *
     * @return Le nombre de rendez-vous du jour
     */
    long getRendezVousDuJourCount();

    /**
     * Récupère les rendez-vous prévus pour aujourd'hui.
     *
     * @return La liste des rendez-vous du jour
     */
    List<?> getRendezVousDuJour();
}