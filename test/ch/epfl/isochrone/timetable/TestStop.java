/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

public class TestStop {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Stop s = new Stop("invalid", new PointWGS84(6.57, 46.52));
        s.name();
        s.position();
    }

    // A compléter avec de véritables méthodes de test...

    @Test
    public void testName() {
        Stop s = new Stop("essai", new PointWGS84(1, 1));
        Stop t = new Stop("deuxieme essai", new PointWGS84(0.2, 0.2));
        assertEquals(s.name(), "essai");
        assertEquals(t.name(), "deuxieme essai");
    }

    @Test
    public void testPosition(){
        PointWGS84 point1  = new PointWGS84(1,1);
        Stop s = new Stop("essai", point1);
        
        assertTrue(s.position().longitude()==point1.longitude());
        assertTrue(s.position().latitude()==point1.latitude());
    }
    
    @Test
    public void testToString(){
        PointWGS84 point1 = new PointWGS84(0.2,0.2);
        Stop s = new Stop("essai", point1);
        
        assertEquals(s.name(),s.toString());
    }
}
