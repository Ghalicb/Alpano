package ch.epfl.alpano;

/**
 * Represents a point on the surface of the earth, with a certain latitude and
 * longitude
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.fromMath;
import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.haversin;
import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Locale;

public final class GeoPoint {

    /** The latitude of the GeoPoint (in radians) */
    private final double latitude_;

    /** The longitude of the GeoPoint (in radians) */
    private final double longitude_;

    /**
     * Constructs a point on the surface of the earth by attributing it a
     * longitude and a latitude
     * 
     * @param longitude
     *            (in radians)
     * @param latitude
     *            (in radians)
     * @throws IllegalArgumentException
     *             if the longitude isn't in the interval [-π;π]
     * @throws IllegalArgumentException
     *             if the latitude isn't in the interval [-π/2/;π/2]
     */
    public GeoPoint(double longitude, double latitude) {
        checkArgument((longitude >= -PI) && (longitude <= PI),
                "Invalid longitude");
        checkArgument((latitude >= -PI / 2) && (latitude <= PI / 2),
                "Invalid latitude");

        latitude_ = latitude;
        longitude_ = longitude;
    }

    /**
     * Gives the longitude of this GeoPoint
     * 
     * @return the longitude of this (in radians)
     */
    public double longitude() {
        return longitude_;
    }

    /**
     * Gives the latitude of this GeoPoint
     * 
     * @return the latitude of this GeoPoint (in radians)
     */
    public double latitude() {
        return latitude_;
    }

    /**
     * Gives the distance between this GeoPoint and another one
     * 
     * @param that
     *            a second GeoPoint
     * @return the distance between this and that (in meters)
     */
    public double distanceTo(GeoPoint that) {
        double a = 2 * asin(sqrt(haversin(latitude() - that.latitude())
                + cos(latitude()) * cos(that.latitude())
                        * haversin(longitude() - that.longitude())));
        return toMeters(a);
    }

    /**
     * Gives the azimuth of a destination GeoPoint in the referential of this
     * 
     * @param that
     *            the destination GeoPoint
     * @return the azimuth of that in the referential of this (in radians)
     */
    public double azimuthTo(GeoPoint that) {
        double x = sin(longitude() - that.longitude()) * cos(that.latitude());
        double y = cos(latitude()) * sin(that.latitude()) - sin(latitude())
                * cos(that.latitude()) * cos(longitude() - that.longitude());
        return fromMath(canonicalize(atan2(x, y)));
    }

    @Override
    public String toString() {
        Locale l = null;
        String s = String.format(l, "(%.4f,%.4f)", toDegrees(longitude()),
                toDegrees(latitude()));
        return s;
    }

}
