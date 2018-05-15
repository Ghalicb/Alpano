package ch.epfl.alpano.gui;

/**
 * A functionnal interface that represents a channel painter 
 * (a function that attributes for each coordinates a value)
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.Panorama;

@FunctionalInterface
public interface ChannelPainter {

    /**
     * Gives the value of the channel at a point
     * 
     * @param x
     *            the abscissa of the point
     * @param y
     *            the ordinate of the point
     * @return the value of the channel at the point
     */
    float valueAt(int x, int y);

    /**
     * Gives a channel painter that attributes to each point a value which
     * corresponds to the difference of distance between the furthest neighbour
     * (in real-distance) and a choosen point.
     *
     * @param panorama
     *            the panorama whom points belong
     * @return the channel painter
     */
    static ChannelPainter maxDistanceToNeighbors(Panorama panorama) {
        return (x,y) -> max(
                        max(panorama.distanceAt(x, y + 1, 0),
                                panorama.distanceAt(x + 1, y, 0)),
                        max(panorama.distanceAt(x - 1, y, 0),
                                panorama.distanceAt(x, y - 1, 0)))
                        - panorama.distanceAt(x, y);
    }

    /**
     * Creates a channel painter composed of this function plus a given value
     * 
     * @param d
     *            a constant
     * 
     * @return a new channel painter
     */
    default ChannelPainter add(float d) {
        return (x, y) -> valueAt(x, y) + d;
    }

    /**
     * Creates a channel painter composed of this function minus a given value
     * 
     * @param d
     *            a constant
     * 
     * @return a new channel painter
     */
    default ChannelPainter sub(float d) {
        return (x, y) -> valueAt(x, y) - d;
    }

    /**
     * Creates a channel painter composed of this function multiplied by a given
     * value
     * 
     * @param d
     *            a constant
     * 
     * @return a new channel painter
     */
    default ChannelPainter mul(float d) {
        return (x, y) -> valueAt(x, y) * d;
    }

    /**
     * Creates a channel painter composed of this function divided by a given
     * value
     * 
     * @param d
     *            a constant
     * 
     * @return a new channel painter
     */
    default ChannelPainter div(float d) {
        return (x, y) -> valueAt(x, y) / d;
    }

    /**
     * Creates a channel painter which is the composition of this and a given
     * function
     * 
     * @param f
     *            a function (R => R)
     * 
     * @return a new channel painter
     */
    default ChannelPainter map(DoubleUnaryOperator f) {
        return (x, y) -> (float) f.applyAsDouble(valueAt(x, y));
    }

    /**
     * Creates a channel painter which clamps the value produced by this between
     * 0 and 1
     * 
     * @return a new channel painter (R => [0,1])
     */
    default ChannelPainter clamped() {
        return (x, y) -> max(0, min(valueAt(x, y), 1));
    }

    /**
     * Creates a channel painter which applies congruence modulo 1 to the value
     * produced by this
     * 
     * @return a new channel painter (R => [0,1])
     */
    default ChannelPainter cycling() {
        return (x, y) -> (float) Math2.floorMod(valueAt(x, y), 1);
    }

    /**
     * Creates a channel painter which gives the complementary of the value
     * produced by this (according that the value was clamped or cycled before)
     * 
     * @return a new channel painter ([0,1] => [0,1])
     */
    default ChannelPainter inverted() {
        return (x, y) -> 1 - valueAt(x, y);
    }

}
