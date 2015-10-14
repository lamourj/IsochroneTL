/**
 * Méthode main, affiche les données par ordre alphabétique comme sur la présentation sur cs108.ch
 * 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import javax.imageio.ImageIO;

import ch.epfl.isochrone.tiledmap.ColorTable;
import ch.epfl.isochrone.tiledmap.IsochroneTileProvider;
import ch.epfl.isochrone.tiledmap.Tile;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Service;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

public class TimeTableSearch {

    public static void main(String[] args) throws IllegalArgumentException,
            IOException {
        String nomArretDepart = args[0];
        String[] dateDeDepart = args[1].split("-");
        String[] heureDeDepart = args[2].split(":");

        Date dateDep = new Date(Integer.parseInt(dateDeDepart[2]),
                Integer.parseInt(dateDeDepart[1]),
                Integer.parseInt(dateDeDepart[0]));

        int departureTime = SecondsPastMidnight.fromHMS(
                Integer.parseInt(heureDeDepart[0]),
                Integer.parseInt(heureDeDepart[1]),
                Integer.parseInt(heureDeDepart[2]));

        int maxWalkingTime = SecondsPastMidnight.fromHMS(0, 5, 0);
        double walkingSpeed = 1.25;

        TimeTableReader ttr = new TimeTableReader("/time-table/");
        TimeTable tt = ttr.readTimeTable();

        Set<Stop> stops = tt.stops();
        Set<Service> services = tt.servicesForDate(dateDep);

        Stop startingStop = null;

        for (Stop stop : stops)
            if (stop.name().equals(nomArretDepart)){
                startingStop = stop;
                break;
            }

        Graph g = ttr.readGraphForServices(stops, services, maxWalkingTime,
                walkingSpeed);

        FastestPathTree fpt = g.fastestPaths(startingStop, departureTime);

        Stop[] sortedArrayOfStops = fpt.stops().toArray(new Stop[fpt.stops().size()]);


        Arrays.sort(sortedArrayOfStops, new Comparator<Stop>() {

            @Override
            public int compare(Stop stop1, Stop stop2) {
                return (stop1.name().compareTo(stop2.name()));
            }
        });

        for (Stop stop : sortedArrayOfStops) {
            StringBuilder s = new StringBuilder(stop.name());
            s.append(" : ");
            s.append(SecondsPastMidnight.toString(fpt.arrivalTime(stop)));
            System.out.println(s.toString());
            s = new StringBuilder(" Via : ");
            s.append(fpt.pathTo(stop).toString());
            System.out.println(s.toString());
        }

        ArrayList<Color> lc = new ArrayList<Color>();

        lc.add(new Color(255, 0, 0));
        lc.add(new Color(255, 122, 0));
        lc.add(new Color(255, 255, 0));
        lc.add(new Color(122, 255, 0));
        lc.add(new Color(0, 255, 0));
        lc.add(new Color(0, 122, 122));
        lc.add(new Color(0, 0, 255));
        lc.add(new Color(0, 0, 122));
        lc.add(new Color(0, 0, 0));        

        ColorTable ct = new ColorTable(300, lc);

        IsochroneTileProvider ttp = new IsochroneTileProvider(fpt, ct, 1.25);
        Tile t = ttp.tileAt(11, 1061, 724);

        try {
            ImageIO.write(t.image(), "png", new File("imageIsochrone.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}