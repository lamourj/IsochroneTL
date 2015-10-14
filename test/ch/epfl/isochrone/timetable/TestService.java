/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;

public class TestService {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Date startingDate = new Date(1, Month.JANUARY, 2000);
        Date endingDate = new Date(2, Month.FEBRUARY, 2000);
        Service s = new Service("service", startingDate, endingDate,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet());
        s.name();
        s.isOperatingOn(startingDate);

        Service.Builder sb = new Service.Builder("s", startingDate, endingDate);
        sb.name();
        sb.addOperatingDay(DayOfWeek.MONDAY);
        sb.addExcludedDate(startingDate);
        sb.addIncludedDate(endingDate);
        sb.build();
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testServiceFirstException() {
        Date earliestDate = new Date(1, Month.JANUARY, 2000);
        Date latestDate = new Date(2, Month.FEBRUARY, 2000);
        Service s = new Service("s", latestDate, earliestDate,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet());
        assertEquals(s.name(), "s");
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testServiceSecondException() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(2, Month.JANUARY, 2000);
        Date f = new Date(3, Month.JANUARY, 2000);
        Set<Date> excludedDates = new HashSet<>();
        Set<Date> includedDates = new HashSet<>();
        excludedDates.add(f);
        @SuppressWarnings("unused")
        Service s = new Service("s", d, e,
                Collections.<Date.DayOfWeek> emptySet(), excludedDates,
                includedDates);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testServiceThirdException() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(2, Month.JANUARY, 2000);
        Date f = new Date(3, Month.JANUARY, 2000);
        Set<Date> excludedDates = new HashSet<>();
        Set<Date> includedDates = new HashSet<>();
        includedDates.add(e);
        excludedDates.add(e);
        @SuppressWarnings("unused")
        Service s = new Service("s", d, f,
                Collections.<Date.DayOfWeek> emptySet(), excludedDates,
                includedDates);
    }

    @Test
    public void testName() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Service s = new Service("s", d, d,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet());
        assertEquals(s.name(), "s");
    }

    @Test
    public void testToString() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Service s = new Service("s", d, d,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet());
        assertEquals(s.toString(), "s");
    }

    @Test
    public void testIsOperatingOn() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(2, Month.JANUARY, 2000);
        Date f = new Date(3, Month.JANUARY, 2000);
        Date g = new Date(4, Month.JANUARY, 2000);
        Set<Date> excludedDates = new HashSet<>();
        Set<Date> includedDates = new HashSet<>();
        includedDates.add(e);
        excludedDates.add(f);
        Service s = new Service("s", d, g,
                Collections.<Date.DayOfWeek> emptySet(), excludedDates,
                includedDates);
        assertTrue(s.isOperatingOn(e));
        assertFalse(s.isOperatingOn(f));
    }

    @Test
    public void testServiceIsImmuable() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Date e = new Date(3, Month.JANUARY, 2000);
        Date f = new Date(4, Month.JANUARY, 2000);
        Set<Date> excludedDates = new HashSet<>();
        Set<Date> includedDates = new HashSet<>();
        Set<Date.DayOfWeek> operatingDays = new HashSet<>();
        operatingDays.add(DayOfWeek.MONDAY);
        Service s = new Service("s", d, f, operatingDays, excludedDates,
                includedDates);
        assertTrue(s.isOperatingOn(e));
        excludedDates.add(e);
        assertTrue(s.isOperatingOn(e));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilderException() {
        Date earliestDate = new Date(1, Month.JANUARY, 2000);
        Date latestDate = new Date(2, Month.FEBRUARY, 2000);

        @SuppressWarnings("unused")
        Service.Builder sb = new Service.Builder("service", latestDate,
                earliestDate);
    }

    @Test
    public void testBuilderBuilds() {
        Date startingDate = new Date(1, Month.JANUARY, 2000);
        Date endingDate = new Date(3, Month.FEBRUARY, 2000);
        Date included = new Date(31, Month.JANUARY, 2000);
        Date excluded = new Date(29, Month.JANUARY, 2000);
        Date shouldBeIncluded = new Date(13, Month.JANUARY, 2000);
        Service.Builder sb = new Service.Builder("service", startingDate,
                endingDate);
        sb.addOperatingDay(DayOfWeek.MONDAY);
        sb.addOperatingDay(DayOfWeek.TUESDAY);
        sb.addOperatingDay(DayOfWeek.WEDNESDAY);
        sb.addOperatingDay(DayOfWeek.THURSDAY);
        sb.addOperatingDay(DayOfWeek.FRIDAY);
        sb.addIncludedDate(included);
        sb.addExcludedDate(excluded);

        Service s = sb.build();

        assertTrue(s.isOperatingOn(included));
        assertFalse(s.isOperatingOn(excluded));
        assertTrue(s.isOperatingOn(shouldBeIncluded));
    }
}
