package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.toMath;
import static ch.epfl.alpano.Distance.toRadians;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.Math2.lerp;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.scalb;
import static java.lang.Math.sin;
import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Represents the Elevation profile of a Continuous elevation model from an
 * origin to an end location given through its azimuth with the origin and its
 * distance (in meters) from it.
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public final class ElevationProfile {

    /** The distance between the points used for the interpolation of the elevation profile */
    private final static int SPACE_BETWEEN_POINTS = 4096;
    
    /** The origin of the elevation profile */
    private final GeoPoint origin_;

    /** The continuous elevation model containing the elevation at each point */
    private final ContinuousElevationModel elevationModel_;

    /**
     * The great-circle distance(in meters) in between the origin and the
     * end-point of this
     */
    private final double length_;

    /** The azimuth from the origin in which the elevation profile is made */
    private final double azimuth_;

    /**
     * An array containing GeoPoints seperated by a great-circle distance of
     * 4096 meters along the elevation profile
     */
    private GeoPoint[] positions_;

    /**
     * Creates an elevation profile from an origin in a certain direction and of
     * a certain length. Calculates GeoPoints separated by 4096 meters along the
     * great-circle and stores them in the positions_ array.
     * 
     * @param elevationModel
     *            the Continuous elevation model containing the elevation of
     *            each point of the elevation model
     * @param origin
     *            the GeoPoint where the elevation profile starts
     * @param azimuth
     *            the direction in which the elevation profile follows the
     *            great-circle
     * @param length
     *            the great-circle distance in between the origin and the last
     *            GeoPoint of the elevation profile
     * @throws IllegalArgumentException
     *             if the length is smaller or equal to Zero or if the azimuth
     *             is not canonical
     * @throws NullPointerException
     *             if the continuous elevation model or the origin is null
     */
    public ElevationProfile(ContinuousElevationModel elevationModel,
            GeoPoint origin, double azimuth, double length) {
        checkArgument(isCanonical(azimuth), "The azimuth is not canonical");
        checkArgument(length > 0,
                "The length of elevation profile smaller or equal to 0");
        origin_ = requireNonNull(origin, "The origin GeoPoint is null");
        elevationModel_ = requireNonNull(elevationModel,
                "The elevation model is null");
        length_ = length;
        azimuth_ = azimuth;
        positions();
    }

    /**
     * Calculates the GeoPoints separated by 4096 meters along the great-circle
     * and stores them in the positions_ array. Called by the constructor during
     * the initialization of the Elevation profile
     */
    private void positions() {
        int points = (int) floor((length_ - 1) / SPACE_BETWEEN_POINTS) + 2;
        positions_ = new GeoPoint[points];
        for (int i = 0; i < points; ++i) {
            positions_[i] = calculateExactPosition(i * SPACE_BETWEEN_POINTS);
        }
    }

    /**
     * Calculates the GeoPoint separated by a certain distance from the origin
     * along the great-circle
     * 
     * @param distance
     *            the distance in meters between the origin and the GeoPoint
     * @return the GeoPoint situated "distance" away from the origin
     */
    private GeoPoint calculateExactPosition(double distance) {
        double latitudeFrom = origin_.latitude(); 
        double longitudeFrom = origin_.longitude(); 
        double radiansDistance = toRadians(distance);
        double latitudeAt = asin(
                (sin(latitudeFrom) * cos(radiansDistance)) + (cos(latitudeFrom)
                        * sin(radiansDistance) * cos(toMath(azimuth_))));
        double arcSin = asin((sin(toMath(azimuth_)) * sin(radiansDistance))
                / cos(latitudeAt));
        double longitudeAt = floorMod((longitudeFrom - arcSin + PI), PI2) - PI;
        return new GeoPoint(longitudeAt, latitudeAt);
    }

    /**
     * Determines the elevation of a point in the elevation profile, a certain
     * distance away from the origin
     * 
     * @param x
     *            the great-circle distance in between the origin and the point
     * @return the elevation of the point
     * @throws IllegalArgumentException
     *             if the point is not in the elevation profile
     */
    public double elevationAt(double x) {
        checkArgument(x <= length_ && x >= 0,
                "The point is not in the elevation profile");
        return elevationModel_.elevationAt(positionAt(x));
    }

    /**
     * Gives the GeoPoint of a point in the elevation profile situated a certain
     * distance from the origin
     * 
     * @param x
     *            the great-circle distance between the origin and the point
     * @return the GeoPoint of the point
     * @throws IllegalArgumentException
     *             if the point is not in the elevation profile
     */
    public GeoPoint positionAt(double x) {
        checkArgument(x <= length_ && x >= 0,
                "The point is not in the elevation profile");
        double pos = scalb(x, -12);
        int flooredPos = (int) floor(pos);
        if (flooredPos == positions_.length-1){
            flooredPos -= 1;
        }
        double longitude = lerp(positions_[flooredPos].longitude(),
                positions_[flooredPos + 1].longitude(), pos - flooredPos);
        double latitude = lerp(positions_[flooredPos].latitude(),
                positions_[flooredPos + 1].latitude(), pos - flooredPos);
        return new GeoPoint(longitude, latitude);
    }

    /**
     * Determines the slope of a point in the elevation profile a certain
     * distance away from the origin
     * 
     * @param x
     *            the great-circle distance between the origin and the point
     * @return the slope of the point
     * @throws IllegalArgumentException
     *             if the point is not in the elevation profile
     */
    public double slopeAt(double x) {
        checkArgument(x <= length_ && x >= 0,
                "The point is not in the elevation profile");
        return elevationModel_.slopeAt(positionAt(x));
    }

}
