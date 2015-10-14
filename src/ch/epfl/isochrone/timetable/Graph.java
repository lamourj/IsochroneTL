/**
 * Modélise un graphe d'horaire.
 *
 * 10.03.14
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public final class Graph {
    private final Set<Stop> stops;
    private final Map<Stop, List<GraphEdge>> outgoingEdges;

    /**
     * Constructeur privé, passage obligé par le builder
     * 
     * @param stops
     *            Liste de stops dans le graph
     * @param outgoingEdges
     *            Listes des trajets possibles
     */
    private Graph(Set<Stop> stops, Map<Stop, List<GraphEdge>> outgoingEdges) {
        this.stops = stops;
        this.outgoingEdges = outgoingEdges;

        /*
         * Vérification des arguments: On s'assure que tous les arrêts qui
         * apparaissent comme clefs dans la table associative sont présents dans
         * l'ensemble des arrêts passé en premier argument, de même que tous
         * ceux qui servent de destination aux arcs stockés comme valeurs dans
         * cette même table.
         */
        assert stops.containsAll(outgoingEdges.keySet());
        for (List<GraphEdge> lge : outgoingEdges.values()) {
            assert stops.contains(lge);
            for (GraphEdge ge : lge)
                assert stops.contains(ge.destination());
        }
    }

    // Méthode à décommenter pour executer le test testAddAllWalkEdges() dans
    // TestGraph
    //
    // public Map<Stop, List<GraphEdge>> getOutgoingEdges() {
    // return outgoingEdges;
    // }

    /**
     * Retourne l'arbre des trajets les plus rapides pour l'arrêt et l'heure de
     * départ donnés
     * 
     * @param startingStop
     *            Le Stop de départ
     * @param departureTime
     *            L'heure de départ en secondes après minuit
     * @return L'arbre des trajets les plus rapides, instance de FastestPathTree
     * @throws IllegalArgumentException
     *             Si l'arrêt donné ne fait pas partie du graphe ou si l'heure
     *             de départ est inférieure à zéro
     */
    public FastestPathTree fastestPaths(Stop startingStop, int departureTime)
            throws IllegalArgumentException {

        if (!stops.contains(startingStop))
            throw new IllegalArgumentException(
                    "L'arrêt donné ne fait pas partie du graphe.");
        if (departureTime < 0)
            throw new IllegalArgumentException("Heure de départ inférieure à 0");

        final FastestPathTree.Builder fptb = new FastestPathTree.Builder(
                startingStop, departureTime);

        // Queue de priorité triant les horaires par ordre d'arrivée à l'arrêt
        PriorityQueue<Stop> queue = new PriorityQueue<>(stops.size() - 1,
                new Comparator<Stop>() {

                    @Override
                    public int compare(Stop stop1, Stop stop2) {
                        return (Integer.compare(fptb.arrivalTime(stop1),
                                fptb.arrivalTime(stop2)));
                    }
                });

        queue.addAll(stops);

        // Algorithme de Djikstra
        while (!queue.isEmpty()) {

            Stop currentStop = queue.remove();

            if (fptb.arrivalTime(currentStop) == SecondsPastMidnight.INFINITE) {
                return fptb.build();
            }

            for (GraphEdge ge : outgoingEdges.get(currentStop)) {

                int h = ge.earliestArrivalTime(fptb.arrivalTime(currentStop));
                if (h < fptb.arrivalTime(ge.destination())) {
                    fptb.setArrivalTime(ge.destination(), h, currentStop);
                    queue.remove(ge.destination());
                    queue.add(ge.destination());

                }
            }
        }
        return fptb.build();

    }

    /**
     * Classe imbriquée statiquement, Bâtisseur d'un graph
     */
    public static final class Builder {
        private final Set<Stop> stops;
        private Map<Stop, Map<Stop, GraphEdge.Builder>> buildMap;

        public Builder(Set<Stop> stops) {
            this.stops = Collections.unmodifiableSet(stops);
            this.buildMap = new HashMap<>();
        }

        /**
         * Ajoute au graphe en construction un trajet entre les arrêts de départ
         * et d'arrivée donnés, aux heures données (en secondes après minuit).
         * 
         * @param fromStop
         *            L'arrêt de départ
         * @param toStop
         *            L'arrêt d'arrivée
         * @param departureTime
         *            Heure de départ de l'arrêt de départ
         * @param arrivalTime
         *            Heure d'arrivée à l'arrêt d'arrivée
         * @return this afin de permettre les appels chaînés.
         * 
         * @throws IllegalArgumentException
         *             Si un des stops ne fait pas parti des stops déjà dans le
         *             constructeur, si l'une des deux heures est négative ou si
         *             l'heure d'arrivée est avant l'heure de départ
         */
        public Builder addTripEdge(Stop fromStop, Stop toStop,
                int departureTime, int arrivalTime) {
            if (!(stops.contains(fromStop) && stops.contains(toStop))) {
                throw new IllegalArgumentException(
                        "Un des deux stops ne fait pas partie des stops noeuds.");
            }

            if (departureTime < 0 || arrivalTime < 0) {
                throw new IllegalArgumentException("Heures négatives");
            }

            if (departureTime > arrivalTime) {
                throw new IllegalArgumentException(
                        "Heure d'arrivée avant heure de départ");
            }

            if (!buildMap.containsKey(fromStop)) {
                buildMap.put(fromStop, new HashMap<Stop, GraphEdge.Builder>());
            }

            Map<Stop, ch.epfl.isochrone.timetable.GraphEdge.Builder> temp = buildMap
                    .get(fromStop);

            GraphEdge.Builder geb;

            if (temp.containsKey(toStop)) {
                geb = temp.get(toStop);
            } else {
                geb = new GraphEdge.Builder(toStop);
            }

            geb.addTrip(departureTime, arrivalTime);
            temp.put(toStop, geb);
            buildMap.put(fromStop, temp);
            return this;
        }

        /**
         * Ajoute au graphe en construction la totalité des trajets à pied qu'il
         * est possible d'effectuer entre n'importe quelle paire d'arrêts, en un
         * temps inférieur ou égal au temps maximum de marche passé (en
         * secondes), à la vitesse de marche donnée (en mètres par seconde).
         * 
         * @param maxWalkingTime
         *            Temps de marche maximale
         * @param walkingSpeed
         *            Vitesse de marche
         * @return this afin de permettre les appels chaînés.
         * 
         * @throws IllegalArgumentException
         *             Si le temps de marche est négatif, ou si la vitesse de
         *             marche est négative ou nulle
         * 
         */
        public Builder addAllWalkEdges(int maxWalkingTime, double walkingSpeed) {
            if (!(maxWalkingTime >= 0)) {
                throw new IllegalArgumentException("Temps de marche négatif");
            }
            if (!(walkingSpeed > 0)) {
                throw new IllegalArgumentException(
                        "Vitesse de marche négative ou nulle");

            }

            for (Stop stopDep : stops) {
                for (Stop stopArr : stops) {
                    double distanceTo = stopDep.position().distanceTo(
                            stopArr.position());
                    if (distanceTo <= maxWalkingTime * walkingSpeed
                            && !stopDep.equals(stopArr)) {

                        if (!buildMap.containsKey(stopDep)) {
                            buildMap.put(stopDep,
                                    new HashMap<Stop, GraphEdge.Builder>());
                        }

                        Map<Stop, GraphEdge.Builder> temp = buildMap
                                .get(stopDep);

                        if (!temp.containsKey(stopArr)) {
                            temp.put(stopArr, new GraphEdge.Builder(stopArr));
                        }
                        buildMap.put(stopDep, temp);

                        buildMap.get(stopDep)
                                .get(stopArr)
                                .setWalkingTime(
                                        (int) Math.round(distanceTo
                                                / walkingSpeed));
                    }
                }
            }

            return this;
        }

        /**
         * Construit et retourne un nouveau graphe avec les nœuds passés à la
         * construction du bâtisseur et les arcs ajoutés jusqu'à présent.
         * 
         * @return un nouveau graphe avec les nœuds passés à la construction du
         *         bâtisseur et les arcs ajoutés
         */
        public Graph build() {

            // On créé a partir de la builMap la map OutgoingEdges
            Map<Stop, List<GraphEdge>> outgoingEdges = new HashMap<>();
            
            for (Stop stopDep : buildMap.keySet()) {
                List<GraphEdge> list = new ArrayList<>();
                for (GraphEdge.Builder geb : buildMap.get(stopDep).values()) {
                    list.add(geb.build());
                }
                outgoingEdges.put(stopDep, list);
            }

            for (Stop s : stops) {
                if (!outgoingEdges.containsKey(s)) {
                    outgoingEdges.put(s, new ArrayList<GraphEdge>());
                }
            }

            return new Graph(stops, outgoingEdges);
        }

    }

}