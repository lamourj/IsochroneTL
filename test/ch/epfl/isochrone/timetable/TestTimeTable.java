/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;

public class TestTimeTable {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.

    @Test
    public void namesAreOk() {
        TimeTable t = new TimeTable(Collections.<Stop> emptySet(),
                Collections.<Service> emptySet());
        t.stops();
        t.servicesForDate(new Date(1, Month.JANUARY, 2000));

        TimeTable.Builder b = new TimeTable.Builder();
        b.addStop(new Stop("s", new PointWGS84(0, 0)));
        Date d = new Date(1, Month.APRIL, 2000);
        b.addService(new Service("s", d, d, Collections.<DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet()));
        b.build();
    }

    @Test
    public void testTimeTableStop() {

        TimeTable.Builder TTB1 = new TimeTable.Builder();

        // Ajoute stop builder;
        Stop stop1 = new Stop("essai", new PointWGS84(1, 1));
        TTB1.addStop(stop1);

        // Crée timetable;
        TimeTable TT1 = TTB1.build();

        // Test de la bonne recopie du builder sur stop;
        Set<Stop> stoptest = new HashSet<Stop>();
        stoptest.add(stop1);

        assertTrue(TT1.stops().equals(stoptest));
    }

    @Test
    public void testTimeTableRecopieStop() {

        TimeTable.Builder TTB1 = new TimeTable.Builder();

        // Ajoute stop builder;
        Stop stop1 = new Stop("essai", new PointWGS84(1, 1));
        TTB1.addStop(stop1);

        // Crée timetable;
        TimeTable TT1 = TTB1.build();

        // Test de la bonne recopie du builder sur stop;
        Set<Stop> stoptest = new HashSet<Stop>();
        stoptest.add(stop1);

        // Test de la bonne recopie du builder sur stop;
        Stop stop2 = new Stop("essai2", new PointWGS84(1.01, 1.01));

        TTB1.addStop(stop2);

        assertTrue(TT1.stops().equals(stoptest));
    }

    @Test
    public void testTimeTableService() {

        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(2, Month.FEBRUARY, 2000);

        Service.Builder h = new Service.Builder("s", d, e);
        h.addOperatingDay(DayOfWeek.MONDAY);
        h.addExcludedDate(e);
        h.addIncludedDate(new Date(5, Month.JANUARY, 2000));
        Service hs = h.build();

        // Ajoute service builder
        TimeTable.Builder TTB1 = new TimeTable.Builder();
        TTB1.addService(hs);

        // Crée timetable;
        TimeTable TT1 = TTB1.build();

        // Test de la bonne recopie du builder sur service;
        Set<Service> servicetest = new HashSet<Service>();
        servicetest.add(hs);

        assertTrue(TT1.servicesForDate(e).equals(servicetest));
    }

    @Test
    public void testTimeTableRecopieService() {

        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(2, Month.FEBRUARY, 2000);
        Date f = new Date(3, Month.MARCH, 2000);
        Date g = new Date(4, Month.APRIL, 2000);

        Service.Builder h = new Service.Builder("s", d, e);
        h.addOperatingDay(DayOfWeek.MONDAY);
        h.addExcludedDate(e);
        h.addIncludedDate(new Date(5, Month.JANUARY, 2000));
        Service hs = h.build();

        Service.Builder i = new Service.Builder("s", f, g);
        i.addOperatingDay(DayOfWeek.THURSDAY);
        i.addExcludedDate(f);
        i.addIncludedDate(new Date(5, Month.MARCH, 2000));
        Service is = i.build();

        // Ajoute service builder
        TimeTable.Builder TTB1 = new TimeTable.Builder();
        TTB1.addService(hs);

        // Crée timetable;
        TimeTable TT1 = TTB1.build();

        // Test indépendance builder;
        TTB1.addService(is);

        // Test de la bonne recopie du builder sur service;
        Set<Service> servicetest = new HashSet<Service>();
        servicetest.add(hs);

        assertTrue(TT1.servicesForDate(e).equals(servicetest));

    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testTimeTableImmuService() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(2, Month.FEBRUARY, 2000);
        Date f = new Date(3, Month.MARCH, 2000);
        Date g = new Date(4, Month.APRIL, 2000);

        Service.Builder h = new Service.Builder("s", d, e);
        h.addOperatingDay(DayOfWeek.MONDAY);
        h.addExcludedDate(e);
        h.addIncludedDate(new Date(5, Month.JANUARY, 2000));
        Service hs = h.build();

        Service.Builder i = new Service.Builder("s", f, g);
        i.addOperatingDay(DayOfWeek.THURSDAY);
        i.addExcludedDate(f);
        i.addIncludedDate(new Date(5, Month.MARCH, 2000));
        Service is = i.build();

        // Ajoute service builder
        TimeTable.Builder TTB1 = new TimeTable.Builder();
        TTB1.addService(hs);

        // Crée timetable;
        TimeTable TT1 = TTB1.build();

        TT1.servicesForDate(e).add(is);
    }

    @Test (expected = java.lang.UnsupportedOperationException.class)
    public void testTimeTableImmuStop() {
        TimeTable.Builder TTB1 = new TimeTable.Builder();

        // Ajoute stop builder;
        Stop stop1 = new Stop("essai", new PointWGS84(1, 1));
        TTB1.addStop(stop1);

        // Crée timetable;
        TimeTable TT1 = TTB1.build();
        
        Stop stop2 = new Stop("essai2", new PointWGS84(1.01, 1.01));

        
        TT1.stops().add(stop2);
    }
}
