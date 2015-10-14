/**
 * Fournisseur de tuiles
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;


public interface TileProvider {
    
    
    /**
     * Fournit la tuile aux coordonnées données
     * 
     * @param zoom Le niveau de zoom de la tuile souhaitée
     * @param x Coordonnée x de la tuile souhaitée 
     * @param y Coordonnée y de la tuile souhaitée 
     * @return la <code>Tile</code> de coordonnées données
     */
    Tile tileAt(int zoom, int x, int y) ;
}