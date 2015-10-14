/**
 * Contient les méthodes de lecture des horaires
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.Date.DayOfWeek;

public final class TimeTableReader {
    private final String baseResourceName;

    /**
     * Construit un lecteur d'horaire ayant la chaîne donnée comme préfixe des
     * ressources.
     * 
     * @param baseResourceName
     *            La chaîne préfixe des ressources
     */
    public TimeTableReader(String baseResourceName) {
        this.baseResourceName = baseResourceName;
    }

    /**
     * Lit et retourne l'horaire
     * 
     * @return L'horaire, une instance de TimeTable, qui ne contient pas les
     *         trajets.
     * @throws IOException
     *             En cas d'erreur d'entrée-sortie
     * @throws IllegalArgumentException
     *             En cas d'erreur de format de données
     */
    public TimeTable readTimeTable() throws IOException,
            IllegalArgumentException {

        TimeTable.Builder ttb = new TimeTable.Builder();

        readStops(ttb);
        readCalendar(ttb);

        return ttb.build();
    }

    /**
     * Extrait une date contenue dans une String
     * 
     * @param s
     *            La String contenant la date, au format YYYYMMJJ
     * @return Une instance de Date représentant la date contenue dans la String
     *         passée en argument
     */
    private Date extractDate(String s) {
        int year = parseInt(s.substring(0, 4));
        int month = parseInt(s.substring(4, 6));
        int day = parseInt(s.substring(6, 8));
        return new Date(day, month, year);
    }

    /**
     * Lecture des Stops. Prend en argument un TimeTable.Builder à mettre à jour
     * avec les nouvelles données de stops.
     * 
     * @param ttb
     *            L'horaire à mettre à jour
     * @throws IOException
     *             En cas d'erreur d'entrée-sortie.
     * @throws IllegalArgumentException
     *             En cas d'erreur de format de données.
     */
    private void readStops(TimeTable.Builder ttb) throws IOException,
            IllegalArgumentException {
        InputStream inStream = getClass().getResourceAsStream(
                baseResourceName + "stops.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                String name = data[0];

                double latitude = parseDouble(data[1]);
                double longitude = parseDouble(data[2]);

                latitude = Math.toRadians(latitude);
                longitude = Math.toRadians(longitude);

                PointWGS84 position = new PointWGS84(longitude, latitude);
                Stop newStop = new Stop(name, position);

                ttb.addStop(newStop);
            }
            reader.close();
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException
                | IOException e) {
            throw new IllegalArgumentException(
                    "Erreur de données dans stops.csv");
        }

    }

    /**
     * Lecture des services. Prend en argument un TimeTable.Builder à mettre à
     * jour avec les nouvelles données de services.
     * 
     * @param ttb
     *            L'horaire à mettre à jour
     * @throws IOException
     *             En cas d'erreur d'entrée-sortie.
     * @throws IllegalArgumentException
     *             En cas d'erreur de format de données.
     */
    private void readCalendar(TimeTable.Builder ttb) throws IOException,
            IllegalArgumentException {

        Map<String, Service.Builder> servicesBuilders = new HashMap<String, Service.Builder>();

        InputStream inStream = getClass().getResourceAsStream(
                baseResourceName + "calendar.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                String name = data[0];

                ArrayList<DayOfWeek> operatingDays = new ArrayList<DayOfWeek>();

                Date.DayOfWeek[] daysOfWeek = DayOfWeek.values();

                for (int j = 1; j <= 7; j++) {
                    if (data[j].equals("1")) {
                        operatingDays.add(daysOfWeek[j - 1]);
                    }
                }

                Date startingDate = extractDate(data[8]);
                Date endingDate = extractDate(data[9]);

                Service.Builder newServiceBuilder = new Service.Builder(name,
                        startingDate, endingDate);

                for (DayOfWeek d : operatingDays) {
                    newServiceBuilder.addOperatingDay(d);
                }
                servicesBuilders.put(name, newServiceBuilder);
                ttb.addService(newServiceBuilder.build());
            }
            reader.close();
            readCalendarDates(servicesBuilders);
        } catch (IOException | StringIndexOutOfBoundsException
                | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Erreur de données dans calendar.csv");
        }
    }

    /**
     * Lecture des exceptions aux jours de circulation. Prend en argument les
     * Service.Builder à mettre à jour, contenus dans une Map dont la clé de
     * chaque value Stop est son nom (instance de String).
     * 
     * @param ttb
     *            L'horaire à mettre à jour
     * @throws IOException
     *             En cas d'erreur d'entrée-sortie.
     * @throws IllegalArgumentException
     *             En cas d'erreur de format de données.
     */
    private void readCalendarDates(Map<String, Service.Builder> servicesBuilders)
            throws IOException, IllegalArgumentException {
        InputStream inStream = getClass().getResourceAsStream(
                baseResourceName + "calendar_dates.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                String name = data[0];
                Date date = extractDate(data[1]);
                boolean goToIncluded = data[2].equals("1");

                if (goToIncluded) {
                    servicesBuilders.get(name).addIncludedDate(date);
                } else {
                    servicesBuilders.get(name).addExcludedDate(date);
                }
            }
            reader.close();
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException
                | StringIndexOutOfBoundsException | IOException e) {
            throw new IllegalArgumentException(
                    "Erreur de données dans calendar_dates.csv");
        }
    }

    /**
     * Lit et retourne le graphe des horaires pour les arrêts donnés, en ne
     * considérant que les trajets dont le service fait partie de l'ensemble
     * donné. Ce graphe inclut également la totalité des trajets à pied
     * entre arrêts qui sont faisables en un temps inférieur ou égal à celui
     * donné, à la vitesse de marche donnée.
     * 
     * @param stops
     * @param services
     * @param walkingTime
     *            Le temps de marche, en secondes.
     * @param walkingSpeed
     *            La vitesse de marche, en mètres par seconde.
     * @return
     * @throws IOException
     *             En cas d'erreur d'entrée-sortie.
     * @throws IllegalArgumentException
     *             En cas d'erreur de format de données.
     */
    public Graph readGraphForServices(Set<Stop> stops, Set<Service> services,
            int walkingTime, double walkingSpeed) throws IOException,
            IllegalArgumentException {

        Graph.Builder gb = new Graph.Builder(stops);

        // On copie les Stops dans une map afin de pouvoir les retrouver plus
        // facilement à partir de leur nom
        Map<String, Stop> stopsMap = new HashMap<>();
        for (Stop s : stops) {
            stopsMap.put(s.name(), s);
        }

        Set<String> nomsDeServices = new HashSet<>();
        for (Service s : services)
            nomsDeServices.add(s.name());

        InputStream inStream = getClass().getResourceAsStream(
                baseResourceName + "stop_times.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, StandardCharsets.UTF_8));

        try {
            String line = reader.readLine();
            while (line != null) {

                String[] data = line.split(";");

                String nomService = data[0];
                String nomStopDepart = data[1];
                int departureTime = Integer.parseInt(data[2]);
                String nomStopArrivee = data[3];
                int arrivalTime = Integer.parseInt(data[4]);

                if (nomsDeServices.contains(nomService)) {
                    gb.addTripEdge(stopsMap.get(nomStopDepart),
                            stopsMap.get(nomStopArrivee), departureTime,
                            arrivalTime);

                }
                line = reader.readLine();

            }
            gb.addAllWalkEdges(walkingTime, walkingSpeed);
            reader.close();
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException
                | IOException e) {
            throw new IllegalArgumentException(
                    "Erreur de données dans stop_times.csv");
        }
        return gb.build();
    }
}