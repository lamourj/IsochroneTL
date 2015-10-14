/**
 *28.02.14
 *
 * Classe non instanciable, offre des méthodes pour effectuer 
 * des conversions entre heures, minutes et secondes
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;

public final class SecondsPastMidnight {

    /**
     * Constante Un nombre de secondes après minuit qui est garanti plus grand
     * que toutes les valeurs valides
     */
    public static final int INFINITE = 200000;

    /**
     * Constructeur privé et vide, pour empêcher l'instanciation
     */
    private SecondsPastMidnight() {
    }

    /**
     * Convertit un triplet (heures,minutes,secondes) en un nombre de secondes
     * après minuit
     * 
     * @param hours
     *            Les heures
     * @param minutes
     *            Les minutes
     * @param seconds
     *            Les secondes
     * @return le nombre de secondes après minuit de l'heure passée en paramètre
     * @throws IllegalArgumentException
     *             Si l'une des trois valeurs passées en argument est invalide
     */
    public static int fromHMS(int hours, int minutes, int seconds)
            throws IllegalArgumentException {
        if (seconds < 0 || seconds >= 60) {
            throw new IllegalArgumentException("Illegal seconds");
        }
        if (minutes < 0 || minutes >= 60) {
            throw new IllegalArgumentException("Illegal minutes");
        }
        if (hours < 0 || hours >= 30) {
            throw new IllegalArgumentException("Illegal hours");
        }
        return hours * 3600 + minutes * 60 + seconds;
    }

    /**
     * Convertit l'heure d'une date Java (instance de java.util.Date) en un
     * nombre de secondes après minuit
     * 
     * @param date
     *            Une date Java (instance de java.util.Date)
     * @return le nombre de secondes après minuit de la date passée en paramètre
     */
    @SuppressWarnings("deprecation")
    public static int fromJavaDate(java.util.Date date) {
        return fromHMS(date.getHours(), date.getMinutes(), date.getSeconds());
    }

    /**
     * Retourne le nombre d'heures de l'heure (représentée par un nombre de
     * secondes après minuit) passée en paramètre
     * 
     * @param spm
     *            Le nombre de secondes après minuit
     * @return Le nombre d'heures
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en argument est négatif ou
     *             représente une heure au delà de 29 h 59 min 59 s
     */
    public static int hours(int spm) throws IllegalArgumentException {
        if ((spm < 0) || (spm > 29 * 3600 + 59 * 60 + 59)) {
            throw new IllegalArgumentException("Illegal spm");
        }
        return divF(spm, 3600);
    }

    /**
     * Retourne le nombre de minutes de l'heure (représentée par un nombre de
     * secondes après minuit) passée en paramètre
     * 
     * @param spm
     *            Le nombre de secondes après minuit
     * @return Le nombre de minutes de l'heure
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en argument est négatif ou
     *             représente une heure au delà de 29 h 59 min 59 s
     */
    public static int minutes(int spm) throws IllegalArgumentException {
        if ((spm < 0) || (spm > 29 * 3600 + 59 * 60 + 59)) {
            throw new IllegalArgumentException("Illegal spm");
        }
        return divF(spm - 3600 * hours(spm), 60);
    }

    /**
     * Retourne le nombre de secondes de l'heure (représentée par un nombre de
     * secondes après minuit) passée en paramètre
     * 
     * @param spm
     *            Le nombre de secondes après minuit
     * @return Le nombre de secondes de la minute de l'heure
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en argument est négatif ou
     *             représente une heure au delà de 29 h 59 min 59 s
     */
    public static int seconds(int spm) throws IllegalArgumentException {
        if ((spm < 0) || (spm > 29 * 3600 + 59 * 60 + 59)) {
            throw new IllegalArgumentException("Illegal spm");
        }
        return modF(spm - 60 * minutes(spm), 60);
    }

    /**
     * Retourne la représentation textuelle du nombre de secondes après minuit
     * passée en argument
     * 
     * @param spm
     *            Le nombre de secondes après minuit
     * @return La représentation textuelle du nombre de secondes après minuit
     *         passée en argument, sous la forme hh:mm:ss
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en argument est négatif ou
     *             représente une heure au delà de 29 h 59 min 59 s
     * 
     */
    public static String toString(int spm) throws IllegalArgumentException {
        if ((spm < 0) || (spm > 29 * 3600 + 59 * 60 + 59)) {
            throw new IllegalArgumentException("Illegal spm");
        }
        return String.format("%02d:%02d:%02d", hours(spm), minutes(spm),
                seconds(spm));
    }

}