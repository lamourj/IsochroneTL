/**
2 * Table des couleurs à utiliser pour dessiner une carte isochrone.
 * 
 * 28.04.14
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.tiledmap;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public final class ColorTable {
    private int duree;
    private List<Color> colorList;

  
    /**
     * Constructeur de table de couleurs.
     * @param duree La durée de chaque couche, en secondes.
     * @param colorList La liste des couleurs à utiliser.
     */
    public ColorTable(int duree, List<Color> colorList) {
        if (colorList.size() < 1) {
            throw new IllegalArgumentException(
                    "La liste de couleurs doit contenir au moins un élément");
        }
        this.duree = duree;
        this.colorList = new LinkedList<Color>(colorList);
    }

    /**
     * Retourne la <code>Color</code> qui est utilisée pour la <i>i</i>-ème tranche.
     * @param tranche Le numéro de la tranche.
     * @return la couleur utilisée pour la <i>i</i>-ème tranche
     */
    public Color couleurPourTranche(int tranche) {
        return colorList.get(tranche);
    }

    /**
     * Retourne la durée d'une tranche
     * @return La durée d'une tranche, en secondes.
     */
    public int getDuree() {
        return duree;
    }

    /**
     * Retourne le nombre de tranches.
     * @return le nombre de tranches (un <code>int</code>).
     */
    public int nombreDeTranches() {
        return colorList.size();
    }
}