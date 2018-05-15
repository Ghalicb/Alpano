package ch.epfl.alpano;

/**
 * Regroups methods for manipulating azimuths (in radians)
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.lang.Math.PI;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.Preconditions.checkArgument;

public interface Azimuth {

    /**
     * Checks if an azimuth given is canonical
     * 
     * @param azimuth
     *            an angle
     * @return true when the azimuth belongs to [0,2π[ and false otherwise
     */
    static boolean isCanonical(double azimuth) {
        return (azimuth < PI2 && azimuth >= 0);
    }

    /**
     * Computes the canonical form of an azimuth
     * 
     * @param azimuth
     *            an angle
     * @return the canonicalized form of the angle
     */
    static double canonicalize(double azimuth) {
        return floorMod(azimuth, PI2);
    }

    /**
     * Transforms an azimuth (clockwise) into a mathematical angle
     * (counterclockwise)
     * 
     * @param azimuth
     *            an angle (clockwise)
     * @return the angle expressed under its mathematical sense
     * @throws IllegalArgumentException
     *             if the azimuth isn't canonical
     */
    static double toMath(double azimuth) {
        checkArgument(isCanonical(azimuth));
        if (azimuth == 0) {
            return 0;
        } else {
            return PI2 - azimuth;
        }
    }

    /**
     * Transforms a mathematical angle (counterclockwise) into an azimuth
     * (clockwise)
     * 
     * @param azimuth
     *            an angle (counterclockwise)
     * @return the angle expressed under its azimuthal sense
     */
    static double fromMath(double azimuth) {
        return toMath(azimuth);
    }

    /**
     * Converts an azimuth into the octant it contains (on the boarder of two
     * octants, the one-charactered octants are prioritized)
     * 
     * @param azimuth
     *            an angle
     * @param n
     *            the string associated to the north
     * @param e
     *            the string associated to the east
     * @param s
     *            the string associated to the south
     * @param w
     *            the string associated to the west
     * @throws IllegalArgumentException
     *             if the azimuth isn't canonical
     * @return the octant associated to the azimuth
     */
    static String toOctantString(double azimuth, String n, String e, String s,
            String w) {
        checkArgument(isCanonical(azimuth));
        StringBuilder octant = new StringBuilder("");

        // Here it is first test whether it is on the north or south to
        // eventualy assign 'bi-octants' (ne,se, ...)
        if ((azimuth <= 3 * PI / 8) || (azimuth >= 13 * PI / 8)) {
            octant.append(n);
        } else if ((azimuth >= 5 * PI / 8) && (azimuth <= 11 * PI / 8)) {
            octant.append(s);
        }
        if ((azimuth > PI / 8) && (azimuth < 7 * PI / 8)) {
            octant.append(e);
        } else if ((azimuth > 9 * PI / 8) && (azimuth < 15 * PI / 8)) {
            octant.append(w);
        }

        return octant.toString();
    }
}
