/** 
 * 17.02.14
 * Formules mathematiques utiles, classe non instanciable
 * 
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */

package ch.epfl.isochrone.math;

import static java.lang.Math.*;

public final class Math {

    /**
     * Constructeur privé et vide, pour empêcher l'instanciation
     */
    private Math() {
    }

    /**
     * Sinus hyperbolique inverse
     * 
     * @param x
     *            l'argument
     * @return Le sinus hyperbolique inverse du paramètre x.
     * 
     */
    public static double asinh(double x) {
        return log(x + sqrt(1 + pow(x, 2)));
    }

    /**
     * Haversin
     * 
     * @param x
     * l'argument
     * @return Haversin du paramètre x.
     */
    public static double haversin(double x) {
        return pow(sin(x / 2), 2);
    }

    /**
     * Methode privée utile pour les deux methodes suivantes, à savoir divF et modF
     * 
     * @param n
     * @param d
     * @return ?
     */
    private static int utilI(int n, int d) {
        if (signum(n % d) == -signum(d)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Retourne le quotient de la division entière par défaut
     * 
     * @param n
     * @param d
     * @return quotient de la division par défaut de n par d
     */
    public static int divF(int n, int d) {
        return n / d - utilI(n, d);
    }

    /**
     * Retourne le reste de la division entière par défaut
     * 
     * @param n
     * @param d
     * @return reste de la division par défaut de n par d
     */
    public static int modF(int n, int d) {
        return (n % d) + utilI(n, d) * d;
    }
}