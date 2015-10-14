/**
 * 18.02.14
 * Représentation d'une date
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.math.Math.*;

public final class Date implements Comparable<Date>{
    /**
     * Enumeration des jours de la semaine, de MONDAY à SUNDAY
     */
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    };

    /**
     * Enumeration des mois de l'annee du calendrier grégorien, de JANUARY à
     * DECEMBER
     */
    public enum Month {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    };

    private final int day;
    private final Month month;
    private final int year;

    /**
     * Constructeur de Date du calendrier grégorien étant donnés un jour, un
     * mois et une année
     * 
     * @param day
     *            Le jour de la date, un entier
     * @param month
     *            Le mois de la date, instance de l'énumération Month
     * @param year
     *            L'année de la date, un entier
     * @throws IllegalArgumentException
     *             Si le jour n'est pas valide
     */
    public Date(int day, Month month, int year) throws IllegalArgumentException {
        if (day < 1 || day > daysInMonth(month, year)) {
            throw new IllegalArgumentException("Illegal day");
        }
        this.day = day;
        this.month = month;
        this.year = year;


    }

    /**
     * Constructeur de Date du calendrier grégorien étant donnés un jour, un
     * numéro de mois et une année
     * 
     * @param day
     *            Le jour de la date, un entier
     * @param month
     *            Le mois de la date, un entier
     * @param year
     *            L'année de la date, un entier
     * @throws IllegalArgumentException
     *             Si le jour n'est pas valide
     */
    public Date(int day, int month, int year) throws IllegalArgumentException {
        this(day, intToMonth(month), year);

    }

    /**
     * Constructeur de Date en fonction d'une date Java
     * 
     * @param date
     *            Une date, instance de la classe java.util.Date
     */
    @SuppressWarnings("deprecation")
    public Date(java.util.Date date) {
        this(date.getDate(), Month.values()[(date.getMonth())],
                date.getYear() + 1900);
    }

    /**
     * Retourne un booléen indiquant si l'année est bissextile.
     * 
     * @param year
     *            L'année
     * @return true, si l'année passée en paramètre est bissextile, false sinon
     */
    private static boolean isLeapYear(int year) {
        if (modF(year, 4) == 0 && modF(year, 100) != 0) {
            return true;
        } else if (modF(year, 400) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retourne le nombre de jours dans le mois de l'année
     * 
     * @param month
     *            Le mois
     * @param year
     *            L'année
     * @return Le nombre de jours dans le mois month de l'année year.
     */
    private static int daysInMonth(Month month, int year) {
        switch (month) {
        case JANUARY:
            return 31;
        case FEBRUARY:
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        case MARCH:
            return 31;
        case APRIL:
            return 30;
        case MAY:
            return 31;
        case JUNE:
            return 30;
        case JULY:
            return 31;
        case AUGUST:
            return 31;
        case SEPTEMBER:
            return 30;
        case OCTOBER:
            return 31;
        case NOVEMBER:
            return 30;
        case DECEMBER:
            return 31;
        default:
            throw new Error("Illegal Month");
        }
    }

    /**
     * Getter
     * 
     * @return Le jour du mois de la date.
     */
    public int day() {
        return day;
    }

    /**
     * Getter
     * 
     * @return Le mois de la date
     */
    public Month month() {
        return month;
    }

    /**
     * Retourne le mois de la date sous forme d'entier
     * 
     * @return le mois de la date sous forme d'entier (1 pour janvier, 2 pour
     *         février,...)
     */
    public int intMonth() {
        return monthToInt(month);
    }

    /**
     * Retourne l'année de la date
     * 
     * @return l'année de la date
     */
    public int year() {
        return year;
    }

    /**
     * Transforme un numéro de mois en mois, avec la convention que l'entier 1
     * correspond au mois de janvier, 2 à février, etc...
     * 
     * @param m
     *            Le mois
     * @return Le mois correspondant, instance de l'énumération Month
     * @throws IllegalArgumentException
     *             si le mois m saisi est invalide
     */
    private static Month intToMonth(int m) throws IllegalArgumentException {
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("Illegal month");
        }
        return Month.values()[m - 1];
    }

    /**
     * L'inverse de la méthode intToMonth Transforme un mois en numéro de mois
     * 
     * @param m
     *            , le mois à convertir, instance de l'énumération Month
     * @return l'entier correspondant au mois passé en paramètre
     */
    private static int monthToInt(Month m) {
        return m.ordinal() + 1;
    }

    /**
     * Retourne le jour de la semaine de la date
     * 
     * @return le jour de la semaine de la date, instance de DayOfWeek
     */
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.values()[modF(fixed() - 1, 7)];
    }

    /**
     * Transforme un triplet de valeurs (jour,mois,année) en un entier
     * 
     * @param day
     *            Le jour
     * @param month
     *            Le mois
     * @param year
     *            L'année
     * @return Le nombre de jours écoulés entre le 01.01.01 et la date saisie.
     */
    private static int dateToFixed(int day, Month month, int year) {
        int c;
        int y = year - 1;
        int m = monthToInt(month);
        if (m <= 2) {
            c = 0;
        } else if (m > 2 && isLeapYear(year)) {
            c = -1;
        } else {
            c = -2;
        }

        return 365 * y + divF(y, 4) - divF(y, 100) + divF(y, 400)
                + divF(((367 * m) - 362), 12) + c + day;
    }

    /**
     * L'inverse de la méthode dateToFixed, qui transforme un entier en date du
     * calendrier grégorien
     * 
     * @param n
     *            Le nombre de jours écoulés entre le 01.01.01 et la date
     *            souhaitée.
     * @return La date correspondante à l'entier n passé en paramètre.
     */
    private static Date fixedToDate(int n) {
        int year;
        int month;
        int day;

        int p;
        int c;

        int d0 = n - 1;
        int n400 = divF(d0, 146097);
        int d1 = modF(d0, 146097);
        int n100 = divF(d1, 36524);
        int d2 = modF(d1, 36524);
        int n4 = divF(d2, 1461);
        int d3 = modF(d2, 1461);
        int n1 = divF(d3, 365);
        int y0 = 400 * n400 + 100 * n100 + 4 * n4 + n1;

        if (n100 == 4 || n1 == 400) {
            year = y0;
        } else {
            year = y0 + 1;
        }

        p = n - dateToFixed(1, intToMonth(1), year);

        if (n < dateToFixed(1, intToMonth(3), year)) {
            c = 0;
        } else if (n >= dateToFixed(1, intToMonth(3), year) && isLeapYear(year)) {
            c = 1;
        } else {
            c = 2;
        }

        month = divF((12 * (p + c) + 373), 367);

        day = n - dateToFixed(1, intToMonth(month), year) + 1;

        return new Date(day, intToMonth(month), year);
    }

    /**
     * Retourne la date sous forme d'un entier
     * 
     * @return la date sous forme d'un entier (méthode dateToFixed)
     */
    private int fixed() {
        return dateToFixed(day, month, year);
    }

    /**
     * Retourne la date distante du nombre de jours donnés de la date à laquelle
     * on l'applique
     * 
     * @param daysDiff
     *            Le nombre de jours de différence
     * @return la date distante du nombre de jours donnés de la date à laquelle
     *         on l'applique
     */
    public Date relative(int daysDiff) {
        return fixedToDate(fixed() + daysDiff);
    }

    /**
     * Retourne la date Java correspondant à la date
     * 
     * @return la date Java (instance de java.util.Date) correspondant à la date
     */
    @SuppressWarnings("deprecation")
    public java.util.Date toJavaDate() {
        return new java.util.Date(year - 1900, monthToInt(month) - 1, day);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     * 
     * Retourne la représentation textuelle de la date
     * 
     * @return la représentation textuelle de la date, sous la forme yyyy-mm-dd
     * *
     */
    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", year, monthToInt(month), day);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * 
     * Compare la date à laquelle on l'applique à la date passée en argument
     * 
     * @return true si l'Object passé en paramètre est une instance de Date, et
     * si oui, si elle est égale à celle à laquelle on l'applique
     */
    @Override
    public boolean equals(Object that) {
        if (that instanceof Date) {
            return (that.hashCode() - hashCode() == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     * 
     * Retourne l'entier correspondant à la date
     * 
     * @return l'entier correspondant à la date (méthode fixed())
     */
    @Override
    public int hashCode() {
        return fixed();
    }

    /**
     * Compare la date à laquelle on l'applique avec la date passée en argument
     * 
     * @param that
     *            L'autre date à comparer
     * @return 1 si la date passée en argument est strictement inférieure à
     *         celle à laquelle on l'applique 0 si les deux dates sont égales -1
     *         si la date passée en argument est strictement supérieure à celle
     *         à laquelle on l'applique
     */
    public int compareTo(Date that) {
        return (int) Math.signum(fixed() - that.fixed());
    }
}