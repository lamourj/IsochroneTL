/**
 * Composant Swing capable d'afficher une carte en tuiles,
 * ces dernières étant fournies par un ou plusieurs 
 * fournisseurs de tuiles.
 * 01.05.2014
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.isochrone.tiledmap.TileProvider;

public final class TiledMapComponent extends JComponent {
    private static final long serialVersionUID = -1443562536371323695L;
    private int zoomLevel;
    private List<TileProvider> tileList;

    /**
     * Constructeur de TiledMapComponent
     * 
     * @param zoomLevel
     *            Le <code>int</code> correspondant niveau de zoom souhaité.
     */
    public TiledMapComponent(int zoomLevel) {
        if (zoomLevel < 10 || zoomLevel > 19) {
            throw new IllegalArgumentException("Illegal zoom level");
        }
        this.tileList = new LinkedList<>();
        this.zoomLevel = zoomLevel;
    }

    /**
     * Retire le TileProvider passé en argument de la liste de TileProvider du
     * TiledMapComponent.
     * 
     * @param tp
     *            Le TileProvider à supprimer.
     */
    public void removeTileProvider(TileProvider tp) {
        tileList.remove(tp);
        this.repaint();
    }

    /**
     * Setter du niveau de zoom.
     * 
     * @param newZoomLevel
     *            Le nouveau niveau de zoom souhaité.
     * @throws IllegalArgumentException
     *             si le niveau de zoom n'est pas compris entre 10 et 19 inclus.
     */
    public void setZoomLevel(int newZoomLevel) {
        if (newZoomLevel < 10 || newZoomLevel > 19) {
            throw new IllegalArgumentException("Illegal zoom level");
        }
        this.zoomLevel = newZoomLevel;
        repaint();
    }

    /**
     * Getter de zoomLevel
     * 
     * @return zoomLevel, le niveau de zoom actuel.
     */
    public int getZoom() {
        return zoomLevel;
    }

    /**
     * Ajoute le fournisseur de tuiles passé en argument à la liste des
     * fournisseurs de tuiles.
     * 
     * @param t le fournisseur de tuiles à ajouter.
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(TileProvider t) {
        boolean b = tileList.add(t);
        repaint();
        return b;
    }

    /*
     * Méthode appelée par Swing chaque fois que le composant doit être
     * redessiné.
     * 
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Rectangle r = getVisibleRect();
        int xBegin = (int) (r.getMinX() / 256);
        int yBegin = (int) (r.getMinY() / 256);
        int xEnd = (int) (r.getMaxX() / 256);
        int yEnd = (int) (r.getMaxY() / 256);

        for (int x = xBegin; x <= xEnd; x++) {
            for (int y = yBegin; y <= yEnd; y++) {
                
                // Copie de la liste pour éviter qu'elle soit modifiée pendant son utilisation
                List<TileProvider> copyTileList = new LinkedList<>(tileList);
                
                for (TileProvider t : copyTileList) {
                    g.drawImage(t.tileAt(zoomLevel, x, y).image(), null,
                            x * 256, y * 256);
                }
            }
        }
    }

    /*
     * Retourne la taille idéale du composant.
     * 
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        int width, height;
        width = height = (int) Math.pow(2, zoomLevel + 8);
        return new Dimension(width, height);
    }
}