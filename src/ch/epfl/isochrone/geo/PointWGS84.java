/** 
 * 17.02.14
 * Un point dans le système WGS84
 * 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
 
package ch.epfl.isochrone.geo;
 
import static java.lang.Math.*;
import static ch.epfl.isochrone.math.Math.*;
 
public final class PointWGS84 {
    private final double latitude;
    private final double longitude;
    // latitude et longitude sont en radians
    private final int R = 6378137;
 
    /**
     * Constructeur de PointWGS84
     * 
     * @param longitude
     *            La longitude du point en radians
     * @param latitude
     *            La latitude du point en radians
     * @throws IllegalArgumentException
     *             si la longitude est invalide (c-à-d hors de l'intervalle
     *             [−π;π]) ou si la latitude est invalide (c-à-d hors de
     *             l'intervalle [−π2;π2])
     */
    public PointWGS84(double longitude, double latitude)
            throws IllegalArgumentException {
        // longitude et latitude sont en radians
        if (longitude < -Math.PI || longitude > Math.PI) {
            throw new IllegalArgumentException("Longitude invalide");
        }
        if (latitude < -PI / 2 || latitude > PI / 2) {
            throw new IllegalArgumentException("Latitude invalide");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }
 
    /**
     * Getter
     * 
     * @return la longitude du point en radians
     */
    public double longitude() {
        return longitude;
    }
 
    /**
     * Getter
     * 
     * @return la latitude du point en radians
     */
    public double latitude() {
        return latitude;
    }
 
    /**
     * Retourne la distance entre deux points
     * 
     * @param that
     *            Le second point duquel on calcule la distance
     * @return La distance, en mètres, entre le point auquel on l'applique du
     *         point passé en argument.
     */
    public double distanceTo(PointWGS84 that) {
        return 2
                * R
                * asin(sqrt(haversin(this.latitude - that.latitude)
                        + cos(this.latitude) * cos(that.latitude)
                        * haversin(this.longitude - that.longitude)));
    }
 
    /**
     * Retourne ce point mais dans un système de coordonnées OSM.
     * 
     * @param zoom
     *            Le niveau de zoom du système de coordonnées OSM
     * @return Le point dans le système de coordonnées OSM au niveau de zoom
     *         donné
     * @throws IllegalArgumentException
     *             Si le zoom est négatif
     */
    public PointOSM toOSM(int zoom) throws IllegalArgumentException {
        if (zoom < 0) {
            throw new IllegalArgumentException("Zoom négatif");
        }
        return new PointOSM(zoom, (pow(2, zoom + 8)) * (longitude + PI)
                / (2 * PI),
                (pow(2, zoom + 8) * (PI - asinh(tan(latitude))) / (2 * PI)));
    }
 
    /**
     * Retourne une représentation textuelle du point
     * 
     * @return Représentation textuelle du point (longitude,latitude) en degrés
     * 
     *         (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder('(');
        s.append(toDegrees(longitude));
        s.append(',');
        s.append(toDegrees(latitude));
        s.append(')');
        return s.toString();
    }
}