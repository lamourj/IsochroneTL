/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
import org.junit.Ignore;
import org.junit.Test;
 
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.FastestPathTree.Builder;
 
public class TestFastestPathTree {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Stop stop = null;
        Map<Stop, Integer> arrivalTimes = null;
        Map<Stop, Stop> predecessors = null;
        FastestPathTree f = new FastestPathTree(stop, arrivalTimes,
                predecessors);
        Stop s = f.startingStop();
        int i = f.startingTime();
        Set<Stop> ss = f.stops();
        i = f.arrivalTime(stop);
        List<Stop> p = f.pathTo(stop);
        System.out.println("" + s + i + ss + p);
 
        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop, 0);
        fb.setArrivalTime(stop, 0, stop);
        i = fb.arrivalTime(stop);
        f = fb.build();
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void builderOK1() { // le deuxième arret de predecessor est pas dans
                               // arrialTime
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(new Stop("test", new PointWGS84(1.17, 1.11)), 1000);
        arrivalTime.put(startingStop, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(new Stop("test", new PointWGS84(1.17, 1.11)), new Stop(
                "test", new PointWGS84(1.117, 1.11)));
        predecessor.put(new Stop("test", new PointWGS84(1.117, 1.11)),
                new Stop("test", new PointWGS84(1.137, 1.11)));
        new FastestPathTree(startingStop, arrivalTime, predecessor);
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void builderOK2() { // le deuxième arret de predecessor est pas dans
                               // arrialTime
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a1 = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a3 = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a4 = new Stop("test", new PointWGS84(1.127, 1.11));
 
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(a1, 1000);
        arrivalTime.put(startingStop, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a1, a3);
        predecessor.put(a4, startingStop);
        new FastestPathTree(startingStop, arrivalTime, predecessor);
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void builderOK3() { // le deuxième arret de predecessor est pas dans
                               // arrivalTime
        Stop startingStop = new Stop("test", new PointWGS84(1.1, 1.11));
        Stop a1 = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a2 = new Stop("test", new PointWGS84(1.37, 1.11));
        Stop a3 = new Stop("test", new PointWGS84(1.127, 1.11));
        Stop a5 = new Stop("test", new PointWGS84(1.237, 1.11));
 
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(a1, 1000);
        arrivalTime.put(a2, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a1, a3);
        predecessor.put(a2, a5);
        new FastestPathTree(startingStop, arrivalTime, predecessor);
    }
 
    @Test
    public void startingStopOK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
 
        Stop a2 = new Stop("test", new PointWGS84(1.127, 1.11));
 
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(startingStop, 1000);
        arrivalTime.put(a2, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a2, startingStop);
        FastestPathTree a = new FastestPathTree(startingStop, arrivalTime,
                predecessor);
        assertTrue(a.startingStop() == startingStop);
    }
 
    @Test
    public void stopsOK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
 
        Stop a2 = new Stop("test", new PointWGS84(1.127, 1.11));
 
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(startingStop, 1000);
        arrivalTime.put(a2, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a2, startingStop);
        FastestPathTree a = new FastestPathTree(startingStop, arrivalTime,
                predecessor);
        assertTrue(a.stops().equals(arrivalTime.keySet()));
    }
 
    @Test
    public void startingTimeOK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
 
        Stop a2 = new Stop("test", new PointWGS84(1.127, 1.11));
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(startingStop, 1000);
        arrivalTime.put(a2, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a2, startingStop);
        FastestPathTree a = new FastestPathTree(startingStop, arrivalTime,
                predecessor);
        assertTrue(a.startingTime() == 1000);
    }
 
    @Test
    public void arrivalTime1OK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
 
        Stop a2 = new Stop("test", new PointWGS84(1.127, 1.11));
 
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(startingStop, 1000);
        arrivalTime.put(a2, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a2, startingStop);
        FastestPathTree a = new FastestPathTree(startingStop, arrivalTime,
                predecessor);
        assertTrue(a.arrivalTime(a2) == 1200);
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void pathToOK1() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a2 = new Stop("test", new PointWGS84(1.127, 1.11));
 
        Stop a3 = new Stop("test", new PointWGS84(1.127, 1.11));
        Map<Stop, Integer> arrivalTime = new HashMap<>();
        arrivalTime.put(startingStop, 1000);
        arrivalTime.put(a2, 1200);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(a2, startingStop);
        FastestPathTree a = new FastestPathTree(startingStop, arrivalTime,
                predecessor);
        a.pathTo(a3);
    }
 
    @Test
    public void pathToOK2() {
        /*
         * L'arbre ressemble à ça
         * 
         * startingStop / | \ a1 a3 a5 / / \ a2 a4 a6
         */
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a1 = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop a2 = new Stop("test", new PointWGS84(1.37, 1.11));
        Stop a3 = new Stop("test", new PointWGS84(1.127, 1.11));
        Stop a4 = new Stop("test", new PointWGS84(1.127, 1.11));
        Stop a5 = new Stop("test", new PointWGS84(1.237, 1.11));
        Stop a6 = new Stop("test", new PointWGS84(1.237, 1.11));
        FastestPathTree.Builder f = new FastestPathTree.Builder(startingStop,
                112);
        f.setArrivalTime(a1, 115, startingStop);
        f.setArrivalTime(a2, 117, a1);
        f.setArrivalTime(a3, 118, startingStop);
        f.setArrivalTime(a4, 130, a3);
        f.setArrivalTime(a6, 132, a3);
        f.setArrivalTime(a5, 125, startingStop);
        FastestPathTree fFini = f.build();
        List<Stop> ar = new ArrayList<>();
        ar.add(a1);
        ar.add(0, startingStop);
        assertEquals(fFini.pathTo(a1), ar);
        ar.add(a2);
        assertEquals(fFini.pathTo(a2), ar);
        ar.clear();
        ar.add(a5);
        ar.add(0, startingStop);
        assertEquals(fFini.pathTo(a5), ar);
        ar.clear();
        ar.add(a3);
        ar.add(a4);
        ar.add(0, startingStop);
        assertEquals(fFini.pathTo(a4), ar);
        ar.clear();
        ar.add(a3);
        ar.add(a6);
        ar.add(0, startingStop);
        assertEquals(fFini.pathTo(a6), ar);
    }
 
    // encore a tester si pathTo marche
 
    @Test(expected = IllegalArgumentException.class)
    public void builder1OK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        new Builder(startingStop, -1000);
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void builderSetArrivalTimeOK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop s1 = new Stop("test", new PointWGS84(1.17, 1.11));
        FastestPathTree.Builder b = new Builder(startingStop, 1000);
        b.setArrivalTime(s1, 100, startingStop);
    }
 
    @Test
    public void builderArrivalTimeOK() {
        Stop startingStop = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop s1 = new Stop("test", new PointWGS84(1.17, 1.11));
        Stop s2 = new Stop("test", new PointWGS84(1.17, 1.11));
        FastestPathTree.Builder b = new Builder(startingStop, 1000);
        b.setArrivalTime(s1, 1100, startingStop);
        assertTrue(b.arrivalTime(s1) == 1100);
        assertTrue(b.arrivalTime(s2) == SecondsPastMidnight.INFINITE);
    }
 
    // A compléter avec de véritables méthodes de test...
}
