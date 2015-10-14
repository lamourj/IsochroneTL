/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

public class TestGraph {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.

    @Test
    @Ignore
    public void namesAreOk() {
        // Graph n'a aucune méthode publique à ce stade...

        Set<Stop> stops = null;
        Stop stop = null;
        Graph.Builder gb = new Graph.Builder(stops);
        gb.addTripEdge(stop, stop, 0, 0);
        gb.addAllWalkEdges(0, 0);
        gb.build();
    }

    @Test
    public void BuilderGraphTestCreation() {
        Stop s = new Stop("Arret", new PointWGS84(0.5, 0.5));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s);

        // Test d'un builder avec un set de Stop
        @SuppressWarnings("unused")
        Graph.Builder gb1 = new Graph.Builder(stops);
        // Test d'un builder avec un set vide de stop
        @SuppressWarnings("unused")
        Graph.Builder gb2 = new Graph.Builder(Collections.<Stop> emptySet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void BuilderGraphTestAddTripEdgeException1() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addTripEdge(s1, s2, -3, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void BuilderGraphTestAddTripEdgeException2() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addTripEdge(s1, s2, 3, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void BuilderGraphTestAddTripEdgeException3() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addTripEdge(s1, s2, 10, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void BuilderGraphTestAddTripEdgeException4() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Stop s3 = new Stop("Arret4", new PointWGS84(0.8, 0.8));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addTripEdge(s3, s2, 1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGraphTestAddTripEdgeException5() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Stop s3 = new Stop("Arret4", new PointWGS84(0.8, 0.8));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addTripEdge(s1, s3, 1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGraphTestaddAllWalkEdgesException1() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addAllWalkEdges(-10, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGraphTestaddAllWalkEdgesException2() {
        Stop s1 = new Stop("Arret1", new PointWGS84(0.5, 0.5));
        Stop s2 = new Stop("Arret2", new PointWGS84(0.7, 0.7));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s1);
        stops.add(s2);
        Graph.Builder gb1 = new Graph.Builder(stops);

        gb1.addAllWalkEdges(10, -10);
    }

    // POUR ACTIVER CE TEST, il est nécessaire de décommenter la méthode
    // getOutgoingEdges() de la classe Graph.
//    @Ignore
//    @Test
//    public void testAddAllWalkEdges() {
//        PointWGS84 p1 = new PointWGS84(0.6555, 0.9);
//        PointWGS84 p2 = new PointWGS84(0.6556, 0.9);
//        PointWGS84 p3 = new PointWGS84(0.6556, 0.9001);
//        Stop s1 = new Stop("Arret1", p1);
//        Stop s2 = new Stop("Arret2", p2);
//        Stop s3 = new Stop("Arret3", p3);
//
//        Set<Stop> stops = new HashSet<Stop>();
//        stops.add(s1);
//        stops.add(s2);
//        stops.add(s3);
//
//        Graph.Builder gb1 = new Graph.Builder(stops);
//
//        int walkingSpeed = 5;
//
//        int maxWalkingTime = 140;
//
//        gb1.addAllWalkEdges(maxWalkingTime, walkingSpeed);
//
//        Graph g = gb1.build();
//
//        Map<Stop, List<GraphEdge>> outgoingEdges = g.getOutgoingEdges();
//
//        List<Stop> availableDestinationFromS1 = new LinkedList<>();
//        List<Stop> availableDestinationFromS2 = new LinkedList<>();
//        List<Stop> availableDestinationFromS3 = new LinkedList<>();
//
//        for (GraphEdge ge : outgoingEdges.get(s1)) {
//            availableDestinationFromS1.add(ge.destination());
//        }
//
//        for (GraphEdge ge : outgoingEdges.get(s2)) {
//            availableDestinationFromS2.add(ge.destination());
//        }
//
//        for (GraphEdge ge : outgoingEdges.get(s3)) {
//            availableDestinationFromS3.add(ge.destination());
//        }
//
//        // On fait les calculs séparément puis on trouve que les trajets
//        // possibles en moins de 140 secondes sont
//        // S1<->S2, S2<->S3 mais PAS s1<->S3
//        //
//        // On vérifie que ça correspond aux résultats donnés par la méthode addAllWalkEdges
//        assertTrue(availableDestinationFromS1.contains(s2));
//        assertFalse(availableDestinationFromS1.contains(s3));
//        assertTrue(availableDestinationFromS2.contains(s3));
//
//        assertTrue(availableDestinationFromS2.contains(s1));
//        assertFalse(availableDestinationFromS3.contains(s1));
//        assertTrue(availableDestinationFromS3.contains(s2));
//    }

    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFastestPathUncontainedStopException(){
        Set<Stop> stops = new HashSet<Stop>();
        Graph.Builder gb1 = new Graph.Builder(stops);
        int walkingSpeed = 5;
        int maxWalkingTime = 140;
        gb1.addAllWalkEdges(maxWalkingTime, walkingSpeed);
        Graph g = gb1.build();

        PointWGS84 p1 = new PointWGS84(0.6555, 0.9);
        Stop startingStop = new Stop("Arret1", p1);
        
        int departureTime=110;
        
        @SuppressWarnings("unused")
        FastestPathTree fpt = g.fastestPaths(startingStop, departureTime);
    }

}
