/**
 * Test des prodiders de tuiles OSM. 
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class TiledMapComponentTest {

    /**
     * Crée une fichier imageTestOSM.png, dont la valeur de transparence est 0.5
     */
    @Test
    public void testImageOSM() {
        TileProvider bgTileProvider = new CachedTileProvider(
                new OSMTileProvider("http://a.tile.openstreetmap.org", "png"));

        TransparentTileProvider ttp = new TransparentTileProvider(0.5,
                bgTileProvider);
        Tile t = ttp.tileAt(17,67927, 46357);
        try {
            ImageIO.write(t.image(), "png", new File("imageTestOSM.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée une fichier imageTestErreur.png, devant afficher l'image erreur
     */
    @Test
    public void testImageErreur() {
        TileProvider bgTileProvider = new CachedTileProvider(
                new OSMTileProvider("Vivelaprogrammation", "png"));

        TransparentTileProvider ttp = new TransparentTileProvider(0.5,
                bgTileProvider);
        Tile t = ttp.tileAt(17, 67927 * 256, 46357 * 256);
        try {
            ImageIO.write(t.image(), "png", new File("imageTestErreur.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
