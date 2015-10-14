/**
 * Transformateur de fournisseur concret. <p>
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.tiledmap;

import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;

import java.awt.image.BufferedImage;

public final class TransparentTileProvider extends FilteringTileProvider {
    private double opacite;

    /**
     * Constructeur de <code>TransparentTileProvider</code>.
     * 
     * @param opacite
     * L'opacité souhaitée après transformation (int).
     * @param t
     * Le fournisseur de tuiles sous-jacent.
     */
    public TransparentTileProvider(double opacite, TileProvider t) {
        super(t);
        if (!(0<=opacite && opacite<=1)) {
            throw new IllegalArgumentException(
                    "L'opacité doit être comprise entre 0 et 1");
        }
        this.opacite = opacite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.isochrone.tiledmap.TileProvider#tileAt(int, int, int)
     */
    public Tile tileAt(int zoom, int x, int y) {
        Tile temp = t.tileAt(zoom, x, y);
        BufferedImage image = temp.image();

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, transformARGB(image.getRGB(i, j)));
            }
        }

        return new Tile(zoom, x, y, image);

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.isochrone.tiledmap.FilteringTileProvider#transformARGB(int)
     */
    @Override
    public int transformARGB(int argb) {

        double r = modF(divF(argb, (int) Math.pow(2, 16)), (int) Math.pow(2, 8)) / 255.0;
        double g = modF(divF(argb, (int) Math.pow(2, 8)), (int) Math.pow(2, 8)) / 255.0;
        double b = modF(divF(argb, (int) Math.pow(2, 0)), (int) Math.pow(2, 8)) / 255.0;

        return ((int) Math.pow(2, 24)) * ((int) Math.round(255 * opacite))
                + ((int) Math.pow(2, 16)) * ((int) Math.round(255 * r))
                + ((int) Math.pow(2, 8)) * ((int) Math.round(255 * g))
                + ((int) Math.round(255 * b));
    }

}