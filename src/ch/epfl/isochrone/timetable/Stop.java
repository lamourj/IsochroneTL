/**
 *03.03.14
 *Représentation d'un arrêt de transport en commun
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

public final class Stop implements Comparable<Stop> {

    private final String name;
    private final PointWGS84 position;

    /**
     * Constructeur de Stop, un arrêt de transport en commun
     * 
     * @param name
     *            Nom de l'arrêt
     * @param position
     *            Position spaciale de l'arrêt (PointWGS84)
     */
    public Stop(String name, PointWGS84 position) {
        this.name = name;
        this.position = position;
    }

    /**
     * Retourne le nom de l'arrêt
     * 
     * @return Le nom de l'arrêt
     */
    public String name() {
        return name;
    }

    /**
     * Retourne la position de l'arrêt
     * 
     * @return La position de l'arret (PointWGS84)
     */
    public PointWGS84 position() {
        return position;
    }

    /*
     * Retourne une représentation textuelle de l'arrêt
     * 
     * @Override (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     * 
     * @return le nom de l'arrêt
     */

    public String toString() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Stop o) {
        return this.toString().compareTo(o.toString());
    }
}