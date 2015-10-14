/**
 * Représente un cache de tuiles (table 
 * associative associant des tuiles à leurs coordonnées).
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

public class TileCache {
    private final int MAX_SIZE = 100;
    private LinkedHashMap<Long, Tile> cache = new LinkedHashMap<Long, Tile>() {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Tile> e) {
            return size() > MAX_SIZE;
        }
    };

    /**
     * Insère dans la table associative les coordonnées d'une tuile et les lient
     * avec la tuile elle meme. Les coordonnées sont encodées dans un long
     * puisque leurs valeurs le permettent. Si la table associative est déjà
     * pleine, la plus ancienne valeur sera supprimée.
     * 
     * @param zoom
     * @param x
     * @param y
     * @param tile
     */
    public void put(int zoom, int x, int y, Tile tile) {
        cache.put(encodeCoordinates(zoom, x, y), tile);
    }

    /**
     * Retourne la tuile associée aux coordonnées passées en paramètre
     *
     * @param zoom
     * @param x
     * @param y
     * @return
     * La tuile correspondante si elle figure dans la table associative. Sinon, retourne <code>null</code>.
     */
    public Tile get(int zoom, int x, int y) {
        return cache.get(encodeCoordinates(zoom, x, y));
    }

    private long encodeCoordinates(int zoom, int x, int y) {
        return (long) (x + y * Math.pow(2, 20) + zoom * Math.pow(2, 40));
    }
}