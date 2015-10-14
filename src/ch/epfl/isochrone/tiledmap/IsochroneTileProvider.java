/**
 * Fournisseur de tuiles isochrones
 * 
 * 28.04.2014
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Stop;

public final class IsochroneTileProvider implements TileProvider {
    private final FastestPathTree fastestPathTree;
    private final ColorTable colorTable;
    private final double walkingSpeed;

    public IsochroneTileProvider(FastestPathTree fpt, ColorTable ct,
            double walkingSpeed) {
        this.fastestPathTree = fpt;
        this.colorTable = ct;
        this.walkingSpeed = walkingSpeed;
    }

    /**
     * Fournit la tuile de la carte Isochrone à la position et au zomm donné
     * 
     * @return la tuile
     * 
     *         (non-Javadoc)
     * 
     * @see ch.epfl.isochrone.tiledmap.TileProvider#tileAt(int, int, int)
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {

        BufferedImage bI = new BufferedImage(256, 256,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2D = bI.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcul de la taille d'un pixel au niveau de zoom pour déterminer le
        // taille du cercle
        double largeurX = (new PointOSM(zoom, x * 256, y * 256).toWGS84())
                .distanceTo(new PointOSM(zoom, (x * 256) + 1, y * 256)
                        .toWGS84());

        double largeurY = (new PointOSM(zoom, x * 256, y * 256).toWGS84())
                .distanceTo(new PointOSM(zoom, x * 256, (y * 256) + 1)
                        .toWGS84());

        for (int i = colorTable.nombreDeTranches() - 1; i > 0; i--) {

            int tempsTotal = i * colorTable.getDuree();

            for (Stop stop : fastestPathTree.stops()) {

                int dureeRestante = tempsTotal
                        - (fastestPathTree.arrivalTime(stop) - fastestPathTree
                                .startingTime());

                if (dureeRestante > 0) {

                    PointOSM pOSM = stop.position().toOSM(zoom);

                    double rayonX = walkingSpeed * dureeRestante / largeurX;
                    double rayonY = walkingSpeed * dureeRestante / largeurY;

                    g2D.setColor(colorTable.couleurPourTranche(i - 1));
                    g2D.fill(new Ellipse2D.Double(pOSM.x() - rayonX
                            - new PointOSM(zoom, x * 256, y * 256).roundedX(),
                            pOSM.y()
                                    - rayonY
                                    - new PointOSM(zoom, x * 256, y * 256)
                                            .roundedY(), rayonX * 2, rayonY * 2));

                }

            }

        }
        return new Tile(zoom, x, y, bI);
    }

}