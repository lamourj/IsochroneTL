/**
 * Modélise un arc annoté du graphe des horaires
 * 
 * 06.03.14
 * Classe uniquement visible dans son paquetage.
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;
import static java.util.Arrays.binarySearch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class GraphEdge {

    private final Stop destination;
    private final int walkingTime;
    @SuppressWarnings("unused")
    private final Set<Integer> packedTrips;
    // Tableau d'entier utile à la recherche dichotomique de la méthode
    // earliestArrivalTime
    private final Integer[] sortedArrayOfPackedTrips;

    /**
     * Constructeur de GraphEdge. Construit un arc ayant l'arrêt de destination,
     * le temps de marche et les trajets donnés.
     * 
     * @param destination
     *            L'arrêt de destination.
     * @param walkingTime
     *            Le temps de marche exprimé en seconde. Vaut -1 si il est trop
     *            long d'effectuer le trajet à pied.
     * @param packedTrips
     *            L'ensemble des voyages proposés.
     * @throws IllegalArgumentException
     *             Si le temps de marche est illégal (<-1).
     */
    public GraphEdge(Stop destination, int walkingTime, Set<Integer> packedTrips)
            throws IllegalArgumentException {
        if (walkingTime < -1) {
            throw new IllegalArgumentException("Illegal walkingTime");
        }

        this.destination = destination;
        this.walkingTime = walkingTime;
        this.packedTrips = Collections.unmodifiableSet(packedTrips);
        // Remplissage du tableau sortedArrayOfPackedTrips avec les valeurs
        // contenues dans le set packedTrips 
        sortedArrayOfPackedTrips = packedTrips.toArray(new Integer[packedTrips.size()]);
        
        // Tri des valeurs du tableau pour l'utiliser dans la recherche
        // dichotomique de la recherche earliestArrivalTime
        java.util.Arrays.sort(sortedArrayOfPackedTrips);
    }

    /**
     * Encode en entier un trajet donné par son heure de départ et son heure
     * d'arrivée.
     * 
     * L'heure de départ est placée dans les 6 chiffres de poids forts, la durée
     * du trajet est placée dans les 4 chiffres de poids faible.
     * 
     * @param departureTime
     *            L'heure de départ, en nombre de secondes après minuit.
     * @param arrivalTime
     *            L'heure d'arrivée, en nombre de secondes après minuit.
     * @return Un trajet encodé en entier
     * @throws IllegalArgumentException
     *             Si l'heure de départ est invalide ou si la durée du trajet
     *             est invalide.
     */
    public static int packTrip(int departureTime, int arrivalTime)
            throws IllegalArgumentException {
        if (departureTime > 107999 || departureTime < 0) {
            throw new IllegalArgumentException("Illegal departureTime");
        }
        if (arrivalTime - departureTime > 9999) {
            throw new IllegalArgumentException("Illegal trip duration (>9999s)");
        }
        if (departureTime > arrivalTime) {
            throw new IllegalArgumentException(
                    "Illegal departureTime (>arrivalTime)");
        }
        return departureTime * 10000 + (arrivalTime - departureTime);
    }

    /**
     * Extrait l'heure de départ d'un trajet encodé.
     * 
     * @param packedTrip
     *            L'entier représentant le trajet encodé.
     * @return L'heure de départ sous forme d'entier.
     */
    public static int unpackTripDepartureTime(int packedTrip) {
        return divF(packedTrip, 10000);
    }

    /**
     * Extrait la durée d'un trajet encodé
     * 
     * @param packedTrip
     *            L'entier représentant le trajet encodé
     * @return La durée, exprimée en secondes
     */
    public static int unpackTripDuration(int packedTrip) {
        return modF(packedTrip, 10000);
    }

    /**
     * Extrait l'heure d'arrivée d'un trajet encodé
     * 
     * @param packedTrip
     *            L'entier représentant le trajet encodé.
     * @return L'heure d'arrivée sous forme d'entier.
     */
    public static int unpackTripArrivalTime(int packedTrip) {
        return unpackTripDepartureTime(packedTrip)
                + unpackTripDuration(packedTrip);
    }

    /**
     * Retourne l'arrêt de destination de l'arc.
     * 
     * @return L'arrêt de destination de l'arc, un Stop.
     */
    public Stop destination() {
        return destination;
    }

    /**
     * Retourne la première heure d'arrivée possible à la destination donnée,
     * étant donnée l'heure de départ.
     * 
     * Fonctionne avec une recherche dichotomique.
     * 
     * @param departureTime
     *            L'heure de départ en nombre de secondes après minuit
     * @return La première heure d'arrivée possible à l'arc, ou
     *         SecondsPastMidnight.INFINITE si il n'est pas possible d'effectuer
     *         le trajet à l'heure de départ donnée.
     */
    public int earliestArrivalTime(int departureTime) {
        int pos = binarySearch(sortedArrayOfPackedTrips, departureTime * 10000);
        int earliest;

        if (sortedArrayOfPackedTrips.length == 0) {
            earliest = SecondsPastMidnight.INFINITE;
        } else {
            if (departureTime > unpackTripDepartureTime(sortedArrayOfPackedTrips[sortedArrayOfPackedTrips.length - 1])) {
                earliest = SecondsPastMidnight.INFINITE;
            } else if (pos < 0) {
                earliest = unpackTripArrivalTime(sortedArrayOfPackedTrips[-pos - 1]);
            } else {
                earliest = unpackTripArrivalTime(sortedArrayOfPackedTrips[pos]);
            }
        }
        if (walkingTime == -1) {
            return earliest;
        }

        return Math.min(earliest, departureTime + walkingTime);
    }

    /**
     * Classe imbriquée statiquement Bâtisseur de la classe GraphEdge
     */
    public static final class Builder {
        private final Stop destination;
        private int walkingTime;
        private Set<Integer> packedTrips;

        /**
         * Construit un bâtisseur pour un arc ayant l'arrêt donné comme
         * destination, aucun trajet et un temps de marche de -1, signifiant
         * qu'il est trop long d'effectuer le trajet à pied.
         * 
         * @param destination
         *            L'arrêt de destination.
         */
        public Builder(Stop destination) {
            this.destination = destination;
            this.walkingTime = -1;
            this.packedTrips = new HashSet<Integer>();
        }

        /**
         * Change le temps de marche pour qu'il soit égal à la valeur passée
         * en argument.
         * 
         * @param newWalkingTime
         *            Le nouveau temps de marche à appliquer, en secondes.
         * @return this
         * @throws IllegalArgumentException
         *             Si le temps de marche passé en argument est illégal.
         */
        public Builder setWalkingTime(int newWalkingTime)
                throws IllegalArgumentException {
            if (newWalkingTime < -1) {
                throw new IllegalArgumentException("Illegal newWalkingTime");
            }
            this.walkingTime = newWalkingTime;
            return this;
        }

        /**
         * Ajoute un trajet avec les heures de départ et d'arrivée données et
         * exprimées en nombre de secondes après minuit.
         * 
         * @param departureTime
         *            L'heure de départ, en nombre de secondes après minuit.
         * @param arrivalTime
         *            L'heure d'arrivée, en nombre de secondes après minuit.
         * @return this
         */
        public Builder addTrip(int departureTime, int arrivalTime) {
            packedTrips.add(packTrip(departureTime, arrivalTime));
            return this;
        }

        /**
         * Retourne un nouvel arc avec la destination, le temps de marche et les
         * trajets ajoutés jusqu'ici au bâtisseur.
         * 
         * @return Un nouvel arc avec la destination, le temps de marche et les
         *         trajets ajoutés.
         */
        public GraphEdge build() {
            return new GraphEdge(destination, walkingTime, packedTrips);
        }
    }
}