/**
 * Fournisseur de tuiles qui obtient ces dernières depuis un serveur 
 * utilisant les conventions de nommage des tuiles du projet OpenStreetMap. 
 * Paramétrée par l'adresse de base du serveur
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class OSMTileProvider implements TileProvider {
    private String serverBase, extension;

    /**
     * Constructeur de fournisseur de tuiles, qui les obtient depuis un serveur
     * utilisant les conventions de nommage OpenStreetMap
     * 
     * @param serverBase
     *            L'adresse de base du serveur
     * @param extension
     *            L'extension
     */
    public OSMTileProvider(String serverBase, String extension) {
        this.serverBase = serverBase;
        this.extension = extension;
    }

    /**
     * Constructeur de fournisseur de tuiles, qui les obtient depuis un serveur
     * utilisant les conventions de nommage OpenStreetMap
     * 
     * @param url
     *            L'URL
     */
    public OSMTileProvider(URL url) {
        this(url.toString(), "png");
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.isochrone.tiledmap.TileProvider#tileAt(int, int, int)
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {

        BufferedImage image = null;
        URL url;
        try {
            url = new URL(serverBase + "/" + zoom + "/" + x + "/" + y + "."
                    + extension);
            BufferedImage bufImg = ImageIO.read(url);
            // L'image récupérée est de type TYPE_BYTE_INDEXED, les 3 lignes
            // suivantes sont donc nécessaires pour la transformer en une image
            // de TYPE_INT_ARGB, qu'on pourra par la suite exploiter.
            image = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            image.getGraphics().drawImage(bufImg, 0, 0, null);
        } catch (IOException e) {
            try {
                image = ImageIO.read(getClass().getResource(
                        "/images/error-tile.png"));
            } catch (IOException e2) {
                // Eventuelles erreurs ignorées car il s'agit d'une ressource
                // fournie avec le programme.
            }
        }
        return new Tile(zoom, x, y, image);
    }
}