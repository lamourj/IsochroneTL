/** 
 * 17.02.14
 * Un point dans le système OSM
 * 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.geo;

import static java.lang.Math.*;

public final class PointOSM {
    private final int zoom;
    private final double x;
    private final double y;

    /**
     * Construit un point de coordonnées x et y, au niveau de zoom donné
     * 
     * @param zoom
     * @param x
     *            Distance en pixels sur l'axe x
     * @param y
     *            Distance en pixels sur l'axe y
     * @throws IllegalArgumentException
     *             si le niveau de zoom est négatif, ou si l'une des deux
     *             coordonnées n'est pas dans l'intervalle admissible allant de
     *             0 à la valeur maximale.
     */
    public PointOSM(int zoom, double x, double y)
            throws IllegalArgumentException {
        if (x < 0 || x > maxXY(zoom) || y < 0 || y > maxXY(zoom)) {
            throw new IllegalArgumentException("Coordonnées illégales");
        }
        this.zoom = zoom;
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne la taille de l'image de la carte du monde au niveau de zoom
     * donné
     * 
     * @param zoom
     *            Le niveau de zoom du système de coordonnées OSM
     * @return la taille en pixels
     * @throws IllegalArgumentException
     *             Si le zoom est négatif
     */
    public static int maxXY(int zoom) throws IllegalArgumentException {
        if (zoom < 0) {
            throw new IllegalArgumentException("Negative zoom");
        }
        return (int) pow(2, zoom + 8);
    }

    /**
     * Getter
     * 
     * @return la coordonnée x du point.
     */
    public double x() {
        return x;
    }

    /**
     * Getter
     * 
     * @return la coordonnnée y du point.
     */
    public double y() {
        // Getter
        return y;
    }

    /**
     * Méthode d'arrondi
     * 
     * @return l'entier le plus proche de la coordonnée x du point.
     */
    public int roundedX() {
        return (int) round(x);
    }

    /**
     * Méthode d'arrondi
     * 
     * @return l'entier le plus proche de la coordonnée y du point.
     */
    public int roundedY() {
        return (int) round(y);
    }

    /**
     * Getter
     * 
     * @return le niveau de zoom du système de coordonnées du point.
     */
    public int zoom() {
        return zoom;
    }

    /**
     * Retourne le point même au niveau de zoom passé en argument
     * 
     * @param newZoom
     *            Le nouveau niveau de zoom souhaité dans le système de
     *            coordonnées OSM
     * @return le point OSM au niveau de zoom passé en argument
     * @throws IllegalArgumentException
     *             Si le zoom est négatif
     */
    public PointOSM atZoom(int newZoom) throws IllegalArgumentException {
        if (newZoom < 0) {
            throw new IllegalArgumentException("Zoom négatif");
        }
        return new PointOSM(newZoom, x * pow(2, newZoom - zoom), y
                * pow(2, newZoom - zoom));
    }

    /**
     * Retourne le point dans le système de coordonnées WGS 84.
     * 
     * @return Le point dans le système WGS84
     */
    public PointWGS84 toWGS84() {
        double p = pow(2, zoom + 8);
        return new PointWGS84(2 * x * PI / p - PI, atan(sinh(PI
                - 2 * y * PI / p)));
    }

    /**
     * Retourne une représentation textuelle du point
     * 
     * @return une représentation textuelle du point (zoom,x,y) en pixels
     */
    public String toString() {
        StringBuilder s = new StringBuilder('(');
        s.append(zoom);
        s.append(',');
        s.append(x);
        s.append(',');
        s.append(y);
        s.append(')');
        return s.toString();
    }
}