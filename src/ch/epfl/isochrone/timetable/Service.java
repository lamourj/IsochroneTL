/**
 * 03.03.14
 * Représentation d'un service de transport
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 *
 */

package ch.epfl.isochrone.timetable;

import java.util.HashSet;
import java.util.Set;

public final class Service {

    private final String name;
    private final Date startingDate;
    private final Date endingDate;
    private final Set<Date.DayOfWeek> operatingDays;
    private final Set<Date> excludedDates;
    private final Set<Date> includedDates;

    /**
     * Constructeur d'un service
     * 
     * @param name
     *            Le nom du service
     * @param startingDate
     *            Date de début du service
     * @param endingDate
     *            Date de fin du service
     * @param operatingDays
     *            Jour de la semaine où le service est actif
     * @param excludedDates
     *            Jour dans les jours sensés être actifs où le service n'est
     *            exceptionellement non-actifs
     * @param includedDates
     *            Jour dans les jours sensés être non-actifs où le service est
     *            exceptionellement actifs
     * @throws IllegalArgumentException
     *             Si la date de fin est avant la date de début. Si une des
     *             exception est en dehors de la plage où le service est actif.
     *             Si une date est à la fois exceptionnellement incluse et
     *             exclue
     */
    public Service(String name, Date startingDate, Date endingDate,
            Set<Date.DayOfWeek> operatingDays, Set<Date> excludedDates,
            Set<Date> includedDates) throws IllegalArgumentException {

        if (endingDate.compareTo(startingDate) < 0) {
            throw new IllegalArgumentException("endingDate before startingDate");
        }
        
        for(Date date : includedDates){
            if ((date.compareTo(startingDate) < 0)
                    || date.compareTo(endingDate) > 0) {
                throw new IllegalArgumentException(
                        "Date in includedDates not between startingDate and endingDate");
            }
        }

        for (Date date : excludedDates) {
            if ((date.compareTo(startingDate) < 0)
                    || date.compareTo(endingDate) > 0) {
                throw new IllegalArgumentException(
                        "Date in excludedDates not between startingDate and endingDate");
            }
            if (includedDates.contains(date)) {
                throw new IllegalArgumentException(
                        "Date in excludedDates and includedDates");
            }
        }

        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.operatingDays = new HashSet<Date.DayOfWeek>(operatingDays);
        this.excludedDates = new HashSet<Date>(excludedDates);
        this.includedDates = new HashSet<Date>(includedDates);
    }

    /**
     * Retourne le nom du service
     * 
     * @return le nom du service
     */
    public String name() {
        return name;
    }

    /**
     * Retourne vrai si le service est actif à la date passée en paramêtre
     * 
     * @param date
     *            La date à tester
     * @return true ssi le service est actif le jour donné. (Dans la plage de
     *         validité du service, un jour valide, hormis les jours exlus, ou
     *         dans les jours exceptionnellement inclus)
     */
    public boolean isOperatingOn(Date date) {
        if ((date.compareTo(startingDate) < 0)
                || (date.compareTo(endingDate) > 0)) {
            return false;
        }
        if (includedDates.contains(date)) {
            return true;
        }
        if (operatingDays.contains(date.dayOfWeek())
                && !excludedDates.contains(date)) {
            return true;
        }
        return false;
    }

    /*
     * Retourne une représentation textuelle du service, c'est à dire son nom.
     * 
     * @Override (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     * 
     * @return Le nom du service
     */
    public String toString() {
        return name;
    }

    /**
     * Classe Builder de Service, permet la construction d'un Service par étapes
     * 
     * @author Josselin Held (239612)
     * @author Julien Lamour (236517)
     */
    public static final class Builder {
        private final String name;
        private final Date startingDate;
        private final Date endingDate;
        private Set<Date.DayOfWeek> operatingDays = new HashSet<Date.DayOfWeek>();
        private Set<Date> excludedDates = new HashSet<Date>();
        private Set<Date> includedDates = new HashSet<Date>();

        /**
         * Constructeur de Service.Builder
         * 
         * @param name
         *            Le nom du service
         * @param startingDate
         *            Date de début du service
         * @param endingDate
         *            Date de fin du service
         * @throws IllegalArgumentException
         *             Si la date de fin est avant la date de début.
         */
        public Builder(String name, Date startingDate, Date endingDate)
                throws IllegalArgumentException {
            if (endingDate.compareTo(startingDate) < 0) {
                throw new IllegalArgumentException(
                        "endingDate before startingDate");
            }

            this.name = name;
            this.startingDate = startingDate;
            this.endingDate = endingDate;

        }

        /**
         * Retourne le nom du service
         * 
         * @return le nom du service
         */
        public String name() {
            return name;
        }

        /**
         * Ajoute le jour de la semaine donné aux jours de circulation. Retourne
         * this afin de permettre les appels chaînés.
         * 
         * @param day
         *            Jour de la semaine où le service sera actif
         * @return this afin de permettre les appels chaînés
         */
        public Builder addOperatingDay(Date.DayOfWeek day) {
            operatingDays.add(day);
            return this;
        }

        /**
         * Ajoute la date donnée aux jours exceptionnellement exclus du service.
         * Retourne this afin de permettre les appels chaînés.
         * 
         * @param date
         *            La date à exclure
         * @return this afin de permettre les appels chaînés
         * @throws IllegalArgumentException
         *             Si la date n'est pas pas dans la plage de validité du
         *             service en construction, ou si elle fait partie des dates
         *             exceptionnellement incluses.
         */
        public Builder addExcludedDate(Date date)
                throws IllegalArgumentException {
            if ((date.compareTo(startingDate) < 0)
                    || (date.compareTo(endingDate) > 0)) {
                throw new IllegalArgumentException(
                        "Not between startingDate and endingDate");
            }
            if (includedDates.contains(date)) {
                throw new IllegalArgumentException(
                        "Trying to add a date in excludedDates already present in includedDates");
            }
            excludedDates.add(date);
            return this;
        }

        /**
         * Ajoute la date donnée aux jours exceptionnellement incluses du
         * service. Retourne this afin de permettre les appels chaînés.
         * 
         * @param date
         *            La date à inclure
         * @return this afin de permettre les appels chaînés
         * @throws IllegalArgumentException
         *             Si la date n'est pas dans la palge de validité du service
         *             en construction, où si elle fait partie des dates
         *             exceptionnelement exclues
         */
        public Builder addIncludedDate(Date date)
                throws IllegalArgumentException {
            if ((date.compareTo(startingDate) < 0)
                    || (date.compareTo(endingDate) > 0)) {
                throw new IllegalArgumentException(
                        "Not between startingDate and endingDate");
            }
            if (excludedDates.contains(date)) {
                throw new IllegalArgumentException(
                        "Trying to add a date in includedDates already present in excludedDates");
            }
            includedDates.add(date);
            return this;
        }

        /**
         * Retourne un nouveau service avec le nom, la plage de validité, les
         * jours de circulation et les exceptions ajoutées jusqu'ici au
         * builder.
         * 
         * @return le service construit.
         */
        public Service build() {
            return new Service(name, startingDate, endingDate, operatingDays,
                    excludedDates, includedDates);
        }
    }
}