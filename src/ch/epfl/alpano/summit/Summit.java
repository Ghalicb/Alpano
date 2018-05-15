package ch.epfl.alpano.summit;

/**
 * Represents a summit
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

public final class Summit {

    /** The name of the summit */
    private final String name_;

    /** The position of the summit (represented with a GeoPoint) */
    private final GeoPoint position_;

    /** The altitude of the summit (in meters) */
    private final int elevation_;

    /**
     * Create a summit
     * 
     * @param name
     *            the name of the summit
     * @param position
     *            the position of the summit
     * @param elevation
     *            the altitude of the summit
     * @throws NullPointerException
     *             if the GeoPoint that represents the position of this is null
     */
    public Summit(String name, GeoPoint position, int elevation) {
        name_ = requireNonNull(name, "The name of this summit is null");
        position_ = requireNonNull(position, "The position of this summit is null");
        elevation_ = elevation;
    }

    /**
     * Give the name of this summit
     * 
     * @return the name of this
     */
    public String name() {
        return name_;
    }

    /**
     * Give the position of this (a GeoPoint)
     * 
     * @return the position of this
     */
    public GeoPoint position() {
        return position_;
    }

    /**
     * Give the altitude of this (in meters)
     * 
     * @return the altitude of this
     */
    public int elevation() {
        return elevation_;
    }

    @Override
    public String toString() {
        return name_ + " " + position_ + " " + elevation_;
    }
}
