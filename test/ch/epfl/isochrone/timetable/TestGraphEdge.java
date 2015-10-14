/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.timetable.SecondsPastMidnight.fromHMS;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

public class TestGraphEdge {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        int i1 = GraphEdge.packTrip(0, 0);
        i1 = GraphEdge.unpackTripDepartureTime(0);
        i1 = GraphEdge.unpackTripDuration(0);
        i1 = GraphEdge.unpackTripArrivalTime(0) + i1;
        Stop s = null;
        GraphEdge e = new GraphEdge(s, 0, Collections.<Integer> emptySet());
        s = e.destination();
        i1 = e.earliestArrivalTime(0);
        GraphEdge.Builder b = new GraphEdge.Builder(s);
        b.setWalkingTime(0);
        b.addTrip(0, 0);
        e = b.build();
    }

    // A compléter avec de véritables méthodes de test...
    @Test
    public void testPackTrip() {
        int i = GraphEdge.packTrip(50000, 51200);
        int iExpected = 50000 * 10000 + 1200;
        assertEquals(i, iExpected);
        int j = GraphEdge.packTrip(86450, 86550);
        int jExpected = 86450 * 10000 + 100;
        assertEquals(j, jExpected);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPackTripTooBigDepartureTimeException() {
        GraphEdge.packTrip(108000, 51200);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPackTripTooBigDurationException() {
        GraphEdge.packTrip(12345, 12345 + 10000);
    }

    @Test
    public void testUnpackTripDepartureTime() {
        int departureTime = 12345;
        int packedTrip = GraphEdge.packTrip(departureTime, departureTime + 500);
        assertEquals(departureTime,
                GraphEdge.unpackTripDepartureTime(packedTrip));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testGraphEdgeConstructorException() {
        PointWGS84 point = new PointWGS84(2.81, 0.12);
        Stop stop = new Stop("stop", point);
        int wrongWalkingTime = -2;
        Set<Integer> packedTrips = new HashSet<>();
        @SuppressWarnings("unused")
        GraphEdge ge = new GraphEdge(stop, wrongWalkingTime, packedTrips);
    }

    @Test
    public void testUnpackTripDuration() {
        int departureTime = 12345;
        int tripDuration = 300;
        int arrivalTime = departureTime + tripDuration;
        int packedTrip = GraphEdge.packTrip(departureTime, arrivalTime);
        assertEquals(tripDuration, GraphEdge.unpackTripDuration(packedTrip));
    }

    @Test
    public void testUnpackTripArrivalTime() {
        int departureTime = 12345;
        int arrivalTime = departureTime + 542;
        int packedTrip = GraphEdge.packTrip(departureTime, arrivalTime);
        assertEquals(arrivalTime, GraphEdge.unpackTripArrivalTime(packedTrip));
    }

    @Test
    public void testDestination() {
        PointWGS84 point = new PointWGS84(2.81, 0.12);
        Stop stop = new Stop("stop", point);
        GraphEdge e = new GraphEdge(stop, 0, Collections.<Integer> emptySet());
        assertEquals(stop, e.destination());
    }

    @Test
    public void testEarliestArrivalTime() {
        PointWGS84 point = new PointWGS84(2.81, 0.12);
        Stop stop = new Stop("stop", point);
        GraphEdge.Builder b = new GraphEdge.Builder(stop);
        int departureTime1 = fromHMS(8, 0, 0);
        int arrivalTime1 = fromHMS(8, 10, 0);
        b.addTrip(departureTime1, arrivalTime1);

        int departureTime2 = fromHMS(10, 00, 00);
        int arrivalTime2 = fromHMS(10, 59, 24);
        b.addTrip(departureTime2, arrivalTime2);

        int departureTime3 = fromHMS(23, 59, 01);
        int arrivalTime3 = fromHMS(24, 05, 00);
        b.addTrip(departureTime3, arrivalTime3);

        GraphEdge g = b.build();

        assertEquals(g.earliestArrivalTime(departureTime1), arrivalTime1);
        assertEquals(g.earliestArrivalTime(departureTime2), arrivalTime2);
        assertEquals(g.earliestArrivalTime(departureTime3), arrivalTime3);

        b.setWalkingTime(650);

        g = b.build();

        assertEquals(g.earliestArrivalTime(departureTime1), arrivalTime1);
        assertEquals(g.earliestArrivalTime(departureTime2),
                departureTime2 + 650);
        assertEquals(g.earliestArrivalTime(departureTime3), arrivalTime3);

        b = new GraphEdge.Builder(stop);

        b.addTrip(departureTime1, arrivalTime1);
        b.addTrip(departureTime2, arrivalTime2);

        departureTime3 = fromHMS(13, 55, 00);
        arrivalTime3 = fromHMS(14, 00, 00);
        b.addTrip(departureTime3, arrivalTime3);

        g = b.build();

        assertEquals(g.earliestArrivalTime(departureTime2), arrivalTime2);
        assertEquals(g.earliestArrivalTime(departureTime3 + 1),
                SecondsPastMidnight.INFINITE);

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilderSetWalkingTime() {
        PointWGS84 point = new PointWGS84(2.81, 0.12);
        Stop stop = new Stop("stop", point);
        GraphEdge.Builder b = new GraphEdge.Builder(stop);
        b.setWalkingTime(12);
        b.setWalkingTime(-2);
    }

}