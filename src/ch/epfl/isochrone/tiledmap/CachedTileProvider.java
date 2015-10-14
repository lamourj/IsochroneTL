/**
 * Transformateur de fournisseur de tuiles.
 * Garde en mémoire un certain nombre de tuiles afin d'accélérer leur obtention.
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

public class CachedTileProvider implements TileProvider {
    private TileCache tileCache;
    private TileProvider tp;

    /**
     * Constructeur de transformateur de fournisseur de tuiles.
     * 
     * @param tp Le fournisseur de tuiles à transformer.
     */
    public CachedTileProvider(TileProvider tp) {
        this.tileCache = new TileCache();
        this.tp = tp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.isochrone.tiledmap.TileProvider#tileAt(int, int, int)
     */
    public Tile tileAt(int zoom, int x, int y) {
        Tile t = null;
        if (tileCache.get(zoom, x, y) != null)
            t = tileCache.get(zoom, x, y);
        else
            t = tp.tileAt(zoom, x, y);
        tileCache.put(zoom, x, y, t);
        return t;
    }
}