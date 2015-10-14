/**
 *Représentation d'une table horaire
 *
 *03.03.14
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612) 
 */

package ch.epfl.isochrone.timetable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TimeTable {

    private final Set<Stop> stops;
    private final Set<Service> services;

    /**
     * Constructeur de TimeTable Copie les collections qu'il reçoit afin de
     * garantir l'immuabilité des instances.
     * 
     * @param stops
     * @param services
     * @return Un nouvel horaire ayant les arrêts et les services donnés.
     */
    public TimeTable(Set<Stop> stops, Collection<Service> services) {
        this.stops = Collections.unmodifiableSet(stops);
        this.services = new HashSet<>(services);
    }

    /**
     * Retourne l'ensemble des arrêts.
     * 
     * @return l'ensemble des arrêts.
     */
    public Set<Stop> stops() {
        return stops;
    }

    /**
     * Retourne l'ensemble des services actifs le jour donné.
     * 
     * @param date
     * @return l'ensemble des services du jour date passé en paramètre
     */
    public Set<Service> servicesForDate(Date date) {
        Set<Service> services2 = new HashSet<>();

        for (Service s : services) {
            if (s.isOperatingOn(date)) {
                services2.add(s);
            }
        }
        return Collections.unmodifiableSet(services2);
    }

    /**
     * Classe pour la construction incrémentale d'horaires
     * 
     */
    public static class Builder {
        private Set<Stop> stops;
        private Collection<Service> services;

        /**
         * Constructeur de Builder Initialise stops et services en tant
         * qu'ensemble vides
         */
        public Builder() {
            this.stops = new HashSet<Stop>();
            this.services = new HashSet<Service>();
        }

        /**
         * Ajoute un nouvel arrêt à l'horaire en cours de construction
         * 
         * @param newStop
         *            Le Stop à ajouter
         * @return this afin de permettre les appels chaînés.
         */
        public Builder addStop(Stop newStop) {
            stops.add(newStop);
            return this;
        }

        /**
         * Ajoute un nouveau service à l'horaire en cours de construction
         * 
         * @param newService
         *            Le Service à ajouter
         * @return this afin de permettre les appels chaînés.
         */
        public Builder addService(Service newService) {
            services.add(newService);
            return this;
        }

        /**
         * Retourne un nouvel horaire possédant les arrêts et services ajoutés
         * jusqu'ici au bâtisseur
         * 
         * @return un nouvel horaire qui possède les arrêts et services ajoutés
         */
        public TimeTable build() {
            return new TimeTable(stops, services);
        }
    }

}