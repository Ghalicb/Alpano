package ch.epfl.alpano;

/**
 * Represents a panorama calculator. Used to calculate the different points in a
 * panorama, their distance from the observer, their slope, their elevation,
 * etc.
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Distance.EARTH_RADIUS;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.improveRoot;
import static ch.epfl.alpano.Math2.sq;
import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.util.Objects.requireNonNull;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;

public final class PanoramaComputer {

    /** The size of the interval in which to find the first root of our ray */
    public final static int DX = 64; 

    /** The degree of precision we want for our root of rayToGroundDistance */
    private final static int EPSILON = 4;

    /** The refraction coefficient used */
    private final static double K = 0.13;

    /**
     * A constant used to compensate for the curvature of the earth when
     * creating a ray
     */
    private final static double TRUE_RADIUS = ((1 - K) / (2 * EARTH_RADIUS));

    /** The continuous elevation representing the earth's surface */
    private final ContinuousElevationModel dem_;

    /**
     * Creates a new panorama calculator
     * 
     * @param dem
     */
    public PanoramaComputer(ContinuousElevationModel dem) {
        dem_ = requireNonNull(dem, "The CEM given is null");
    }

    /**
     * Creates a new panorama based on it's parameters
     * 
     * @param parameters
     *            the parameters used to create the panorama
     * @return a new panorama
     */
    public Panorama computePanorama(PanoramaParameters parameters) {
        
        Panorama.Builder builder = new Panorama.Builder(parameters);

        int maxDistance = parameters.maxDistance();
        GeoPoint observerPosition = parameters.observerPosition();
        double observerElevation = parameters.observerElevation();

        for (int x = 0; x < parameters.width(); ++x) {

            // Creates an elevation profile based on the azimuth of the x value
            double azimuth = parameters.azimuthForX(x);
            ElevationProfile profile = new ElevationProfile(dem_,
                    observerPosition, azimuth, maxDistance);

            // The distance from the observer to the previous Altitudes distance
            double previousPosition = 0;

            // Computes all of the rays for the already computed x
            for (int y = parameters.height() - 1; y >= 0
                    && previousPosition <= maxDistance; --y) {
                double altitude = parameters.altitudeForY(y);
                double raySlope = tan(altitude);
                DoubleUnaryOperator ray = rayToGroundDistance(profile,
                        observerElevation, raySlope);
                double intervalStart = firstIntervalContainingRoot(ray,
                        previousPosition, maxDistance, DX);
                double horizontalDistance = Double.POSITIVE_INFINITY;

                // Checks that a zero of the function is in the view
                if ((intervalStart <= maxDistance)
                        && (ray.applyAsDouble(intervalStart)
                                * ray.applyAsDouble(Math.min(intervalStart + DX,
                                        maxDistance)) <= 0)) {
                    horizontalDistance = improveRoot(ray, intervalStart,
                            Math.min(intervalStart + DX, maxDistance), EPSILON);
                    
                    double trueDistance = horizontalDistance / cos(altitude);
                    GeoPoint position = profile.positionAt(horizontalDistance);
                    
                    builder.setDistanceAt(x, y, (float) trueDistance)
                           .setLongitudeAt(x, y, (float) position.longitude())
                           .setLatitudeAt(x, y, (float) position.latitude())
                           .setSlopeAt(x, y, (float) profile.slopeAt(horizontalDistance))
                           .setElevationAt(x, y, (float) profile.elevationAt(horizontalDistance));
                }
                previousPosition = horizontalDistance;
            }
        }
        return builder.build();
    }

    /**
     * Creates a new function that represents the distance in between the ray's
     * elevation and the ground
     * 
     * @param profile
     *            the elevation profile representing the ground's elevation
     * @param ray0
     *            the elevation of the ray at the observer's position (in
     *            meters)
     * @param raySlope
     *            the ray's slope
     * @return the function representing the distance (in meters) between the
     *         ray's elevation and the ground
     */
    public static DoubleUnaryOperator rayToGroundDistance(
            ElevationProfile profile, double ray0, double raySlope) {
        return (x) -> (ray0 + raySlope * x - profile.elevationAt(x)
                + TRUE_RADIUS * sq(x));
    }

}
