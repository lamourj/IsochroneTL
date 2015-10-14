/** 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

public class TestTimeTableReader {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() throws IOException {
        TimeTableReader r = new TimeTableReader("");
        TimeTable t = r.readTimeTable();
        Graph g = r.readGraphForServices(t.stops(),
                Collections.<Service> emptySet(), 0, 0d);
        System.out.println(g); // Evite l'avertissement que g n'est pas utilisé
    }

    @Test
    public void testReadTimeTable() {
        try {
            
            TimeTableReader ttr = new TimeTableReader("/time-table/");
            TimeTable ttb = ttr.readTimeTable();
            

            boolean containsS1 = false;
            boolean containsS2 = false;
            
            
            for(Stop s : ttb.stops()){
                if(s.name().equals("Beaulieu") && s.position().latitude()-46.5280924839<0.00001)
                    containsS1=true;
                
                if(s.name().equals("Rosiaz") && s.position().longitude()-6.66227652505<0.00001)
                    containsS2=true;
            }
            
            assertTrue(containsS1);
            assertTrue(containsS2);
        
        
        
        } catch (IllegalArgumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}