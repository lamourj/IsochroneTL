/**
 * Fournisseur de tuile qui affiche le trajet jusqu'à l'arrêt 
 * 
 * @author Josselin Held (239612)
 * @author Julien Lamour (236517)
 * 
 */

package ch.epfl.isochrone.tiledmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.Stop;

public class TrajetTileProvider implements TileProvider {
    LinkedList<Stop> listeArrtes;
    PointOSM pOSM;

    public TrajetTileProvider(List<Stop> liste, PointOSM pOSM) {
        this.listeArrtes = new LinkedList<Stop>(liste);
        this.pOSM = pOSM;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.isochrone.tiledmap.TileProvider#tileAt(int, int, int)
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {

        BufferedImage bI = new BufferedImage(256, 256,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2D = bI.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(new Color(0, 0, 0));

        for (int i = 0; i < listeArrtes.size() - 1; i++) {
            // Dessin du trajet entre les différents arrêts. C'est à dire les
            // parcours en transport public (en noir).
            Line2D line = new Line2D.Float(
                    Math.round((listeArrtes.get(i).position().toOSM(zoom).x() - new PointOSM(
                            zoom, x * 256, y * 256).x())),
                    Math.round((listeArrtes.get(i).position().toOSM(zoom).y() - new PointOSM(
                            zoom, x * 256, y * 256).y())),
                    Math.round((listeArrtes.get(i + 1).position().toOSM(zoom)
                            .x() - new PointOSM(zoom, x * 256, y * 256).x())),
                    Math.round((listeArrtes.get(i + 1).position().toOSM(zoom)
                            .y() - new PointOSM(zoom, x * 256, y * 256).y())));

            g2D.draw(line);
        }

        g2D.setColor(new Color(255, 0, 0));

        // Dession du trajet a faire a pied depuis l'arrêt d'arrivée. (en rouge)
        g2D.draw(new Line2D.Float(
                (int) (listeArrtes.get(listeArrtes.size() - 1).position()
                        .toOSM(zoom).x() - new PointOSM(zoom, x * 256, y * 256)
                        .x()), (int) (listeArrtes.get(listeArrtes.size() - 1)
                        .position().toOSM(zoom).y() - new PointOSM(zoom,
                        x * 256, y * 256).y()), (int) (pOSM.x() - new PointOSM(
                        zoom, x * 256, y * 256).x()),
                (int) (pOSM.y() - new PointOSM(zoom, x * 256, y * 256).y())));

        return new Tile(zoom, x, y, bI);
    }
}
