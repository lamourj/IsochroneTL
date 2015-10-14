/**
 * Transformateur de fournisseur de tuiles abstrait. <p>
 * Transforme l'mage des tuiles de son fournisseur sous-jacent, pixel par pixel. 
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

public abstract class FilteringTileProvider implements TileProvider {
    protected TileProvider t;

    /**
     * Constructeur de transformateur de tuiles.
     * 
     * @param t Le fournisseur sous-jacent.
     */
    public FilteringTileProvider(TileProvider t) {
        this.t = t;
    }

    /**
     * Méthode qui reçoit en argument la couleur d'un pixel au format ARGB et
     * retourne la couleur transformée de ce pixel.
     * 
     * @param argb la couleur du pixel à transformer au format ARGB
     * @return La couleur transformée de ce pixel
     */
    public abstract int transformARGB(int argb);
}