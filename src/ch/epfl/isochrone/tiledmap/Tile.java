/**
 * Représente une tuile de carte.
 *
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;


public final class Tile {
    @SuppressWarnings("unused")
    private int zoomLevel;
    @SuppressWarnings("unused")
    private double x, y;
    private BufferedImage image;

    /**
     * Constructeur de tuile.<p>
     * Construit la tuile aux coordonnées et image données.
     * 
     * @param zoomLevel
     * @param x
     * @param y
     * @param image
     */
    public Tile(int zoomLevel, double x, double y, BufferedImage image) {
        this.zoomLevel = zoomLevel;
        this.x = x;
        this.y = y;
        // clone pour classe immuable
        this.image = image.getSubimage(0, 0, image.getWidth(),
                image.getHeight());
    }

    /**
     * Retourne l'image de la tuile
     * @return L'image de la tuile.
     */
    public BufferedImage image(){
        return image;
    }
}