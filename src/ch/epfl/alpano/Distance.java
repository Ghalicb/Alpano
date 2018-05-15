package ch.epfl.alpano;

/**
 * Contains methods that converts distances on the earth's surface (meters <=>
 * radians) and attributes characterizing the earth
 * 
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public interface Distance {

    /** The radius of the earth in meters */
    static double EARTH_RADIUS = 6371000;

    /**
     * Converts a distance on the earth's surface into an angle
     * 
     * @param distanceInMeters
     *            a distance on the earth's surface (in meters)
     * @return the angle corresponding (in radians)
     */
    static double toRadians(double distanceInMeters) {
        return distanceInMeters / EARTH_RADIUS;
    }

    /**
     * Converts an angle into its corresponding distance on the earth's surface
     * 
     * @param distanceInRadians
     *            an angle (in radians)
     * @return the corresponding distance on the earth's surface (in meters)
     */
    static double toMeters(double distanceInRadians) {
        return distanceInRadians * EARTH_RADIUS;
    }
}
