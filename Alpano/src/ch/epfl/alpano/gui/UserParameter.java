package ch.epfl.alpano.gui;

/**
 * Represents a collection of user parameters, with their minimum and maximum
 * values attached
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public enum UserParameter {

    /** The observers longitude */
    OBSERVER_LONGITUDE(60000, 120000),

    /** The observers latitude */
    OBSERVER_LATITUDE(450000, 480000),

    /** The observers elevation, in meters */
    OBSERVER_ELEVATION(300, 10000),

    /** The azimuth of the center of the observers view, in degrees */
    CENTER_AZIMUTH(0, 359),

    /** The angle representing how wide the observers view is, in degrees */
    HORIZONTAL_FIELD_OF_VIEW(1, 360),

    /** The maximum distance the observer can see, in kilometers */
    MAX_DISTANCE(10, 600),

    /** The width of the panorama, in pixels */
    WIDTH(30, 16000),

    /** The height of the panorama, in pixels */
    HEIGHT(10, 4000),

    /** The supersampling exponent of the panorama */
    SUPER_SAMPLING_EXPONENT(0, 2);

    /** The minimum value the parameter can have */
    private final int min_;

    /** The maximum value the parameter can have */
    private final int max_;

    /**
     * Constructs a user parameter based on its bounds
     * 
     * @param min
     *            the lower bound of the user parameter
     * @param max
     *            the upper bound of the user parameter
     */
    private UserParameter(int min, int max) {
        min_ = min;
        max_ = max;
    }

    /**
     * Gives the valid value closest to a given value for a user parameter
     * 
     * @param givenValue
     *            the value of the user parameter
     * @return the valid value closest to the given value
     */
    public int sanitize(int givenValue) {
        return Math.max(min_, Math.min(givenValue, max_));
    }
}
