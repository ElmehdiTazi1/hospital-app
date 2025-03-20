package org.mql.hospital.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Classe utilitaire pour la manipulation des dates.
 */
public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DateUtils() {
        // Constructeur privé pour empêcher l'instanciation
    }

    /**
     * Calcule l'âge en années à partir d'une date de naissance.
     *
     * @param dateNaissance La date de naissance
     * @return L'âge en années
     */
    public static int calculateAge(Date dateNaissance) {
        if (dateNaissance == null) {
            return 0;
        }

        Date now = new Date();
        long diffInMillis = now.getTime() - dateNaissance.getTime();
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        return (int) (days / 365);
    }

    /**
     * Formate une date selon le pattern "yyyy-MM-dd".
     *
     * @param date La date à formater
     * @return La date formatée en chaîne de caractères
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }
}