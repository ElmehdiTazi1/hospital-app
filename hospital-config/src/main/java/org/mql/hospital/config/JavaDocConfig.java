package org.mql.hospital.config;

/**
 * Configuration centrale pour les standards de documentation JavaDoc du projet.
 * <p>
 * Cette classe contient des constantes et des informations utilisées dans les JavaDocs
 * à travers le projet. Elle sert également d'exemple pour les standards de documentation.
 * </p>
 *
 * @author [Votre nom]
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class JavaDocConfig {

    /**
     * Version actuelle de l'application.
     */
    public static final String APP_VERSION = "0.0.1-SNAPSHOT";

    /**
     * Nom de l'application.
     */
    public static final String APP_NAME = "Hospital Management System";

    /**
     * Préfixe de package pour toutes les classes du projet.
     */
    public static final String BASE_PACKAGE = "org.mql.hospital";

    /**
     * URL de la documentation du projet.
     */
    public static final String DOC_URL = "https://hospital-management-docs.example.com";

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private JavaDocConfig() {
        // Classe utilitaire ne devant pas être instanciée
    }

    /**
     * Génère un lien de documentation standard vers une classe.
     *
     * @param className Le nom de la classe pour laquelle générer un lien
     * @return L'URL complète vers la documentation de la classe
     */
    public static String getClassDocUrl(String className) {
        return DOC_URL + "/api/" + className.replace('.', '/') + ".html";
    }
}