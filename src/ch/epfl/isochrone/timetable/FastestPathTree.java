/**
 * Modelise un arbre des trajets les plus rapides
 *
 * 10.03.14
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.timetable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// CLASSE IMMUABLE
public final class FastestPathTree {
    private final Stop startingStop;
    private final Map<Stop, Integer> arrivalTime;
    private Map<Stop, Stop> predecessor;

    /**
     * Constructeur de FastestPathTree
     * 
     * @param startingStop
     *            L'arrêt de départ de l'arbre
     * @param arrivalTime
     *            Map associant chaque arrêt à son heure d'arrivée à cet arrêt
     * @param predecessor
     *            Map associant chaque arrêt à son prédecesseur dans le graphe
     *            FastestPathTree
     * @throws IllegalArgumentException
     *             Si le tableau des predecesseurs et celui des heures d'arrivée
     *             ne correspondent pas
     */
    public FastestPathTree(Stop startingStop, Map<Stop, Integer> arrivalTime,
            Map<Stop, Stop> predecessor) throws IllegalArgumentException {

        // Set utile uniquement pour tester que les entrées sont les mêmes dans
        // les deux tableaux.
        Set<Stop> set = new HashSet<>(predecessor.keySet());
        set.add(startingStop);

        if (!(arrivalTime.keySet().equals(set))) {
            throw new IllegalArgumentException(
                    "Pas les mêmes entrées dans les 2 tableaux");
        }

        this.startingStop = startingStop;
        this.arrivalTime = new HashMap<Stop, Integer>(arrivalTime);
        this.predecessor = new HashMap<Stop, Stop>(predecessor);
    }

    /**
     * Retourne l'arrêt de départ
     * 
     * @return Un objet Stop qui est l'arrêt de départ.
     */
    public Stop startingStop() {
        return startingStop;
    }

    /**
     * Retourne l'heure de départ, qui est l'heure de première arrivée à l'arrêt
     * de départ.
     * 
     * @return L'heure de départ sous forme de int en seconde après minuit.
     */
    public int startingTime() {

        return arrivalTime.get(startingStop);

    }

    /**
     * Retourne l'ensemble des arrêts pour lesquels une heure de première
     * arrivée existe.
     * 
     * @return Un Set de Stop contenant les arrêts pour lesquels une heure de
     *         première arrivée existe.
     */
    public Set<Stop> stops() {
        return Collections.unmodifiableSet(arrivalTime.keySet());
    }

    /**
     * Retourne l'heure d'arrivée à l'arrêt donné ou
     * SecondsPastMidnight.INFINITE si l'arrêt donné n'est pas dans la table des
     * heures d'arrivée passée au constructeur.
     * 
     * @see ch.epfl.isochrone.timetable.SecondsPastMidnight
     * @param stop
     *            Le Stop auquel on cherche l'heure d'arriver
     * @return l'heure d'arrivée à l'arrêt donné sous forme de int; en seconde
     *         après minuit.
     * 
     */
    public int arrivalTime(Stop stop) {
        if (arrivalTime.containsKey(stop)) {
            return arrivalTime.get(stop);
        } else {
            return SecondsPastMidnight.INFINITE;
        }
    }

    /**
     * Retourne le chemin pour aller de l'arrêt de départ à celui passé en
     * argument
     * 
     * @param stop
     *            Arrêt que l'on veut "atteindre"
     * @return Une liste de Stop dans l'ordre de l'arrêt de départ passé en
     *         argument jusqu'à l'arrêt actuel.
     * @throws IllegalArgumentException
     *             Si l'arrêt passé n'est pas présent dans la table des heures
     *             d'arrivée
     */
    public List<Stop> pathTo(Stop stop) throws IllegalArgumentException {

        if (!arrivalTime.containsKey(stop)) {
            throw new IllegalArgumentException("Stop not present in the list");
        }

        LinkedList<Stop> stopsList = new LinkedList<Stop>();

        Stop currentStop = stop;
        // On parcours la liste de predecessor jusqu'à arriver à l'arrêt de
        // départ
        while (currentStop != startingStop) {

            stopsList.add(currentStop);

            currentStop = predecessor.get(currentStop);

        }
        stopsList.add(startingStop);
        // La liste est inversée, començant par l'arrêt de fin jusqu'à l'arrêt
        // de départ, on l'inverse
        Collections.reverse(stopsList);

        return stopsList;
    }

    // Classe imbriquée. Batisseur
    public static final class Builder {
        private final Stop startingStop;
        private final int startingTime;
        private final Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
        private final Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();

        /**
         * Constucteur de FastestPathTree.Builder. Construit un bâtisseur pour
         * un arbre des trajets les plus rapides avec l'arrêt et l'heure de
         * départ donnés.
         * 
         * @param startingStop
         *            Arrêt "Stop" de départ
         * @param startingTime
         *            Heure ("int" en secondes après minuit) de première arrivée
         *            de l'arrêt de départ
         * @throws IllegalArgumentException
         *             Si l'heure de départ est négative
         */
        public Builder(Stop startingStop, int startingTime)
                throws IllegalArgumentException {
            if (startingTime < 0) {
                throw new IllegalArgumentException("Negative time");
            }

            this.startingStop = startingStop;
            this.startingTime = startingTime;

            this.arrivalTime.put(startingStop, startingTime);
        }

        /**
         * (Re)définit l'heure de première arrivée et le prédécesseur de l'arrêt
         * donné dans l'arbre en construction.
         * 
         * @param stop
         *            Arrêt Stop actuel
         * @param time
         *            Heure (en secondes après minuit) de première arrivée
         * @param predecessor
         *            Arrêt précédent
         * @return This pour les appèls chaînés
         */
        public Builder setArrivalTime(Stop stop, int time, Stop predecessor) {
            if (time < startingTime) {
                throw new IllegalArgumentException(
                        "time anterior to departureTime");
            }

            arrivalTime.put(stop, time);
            this.predecessor.put(stop, predecessor);

            return this;
        }

        /**
         * Retourne l'heure de première arrivée à l'arrêt donné, ou
         * SecondsPastMidnight.INFINITE si aucune heure d'arrivée n'a été
         * attribuée à cet arrêt jusqu'ici.
         * 
         * @param stop
         *            Stop duquel on veut l'heure de première arrivée
         * @return L'heure ("int" en secondes après minuit) de première arrivée
         */
        public int arrivalTime(Stop stop) {
            if (!arrivalTime.containsKey(stop)) {
                return SecondsPastMidnight.INFINITE;
            } else {
                return arrivalTime.get(stop);
            }
        }

        /**
         * Construit l'arbre des trajets les plus rapides avec les nœuds ajoutés
         * jusqu'ici.
         * 
         * @return Un FastestPathTree des trajets les plus rapides.
         */
        public FastestPathTree build() {
            return new FastestPathTree(startingStop, arrivalTime, predecessor);
        }

    }
}