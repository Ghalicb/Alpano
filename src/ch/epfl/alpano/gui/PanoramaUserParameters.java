package ch.epfl.alpano.gui;

/**
 * Represents the parameters of a panorama based on the view of the user of the application
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaParameters;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;


public final class PanoramaUserParameters {

    protected static final double INTEGER_TO_LONGITUDE = 10000d;
    
    protected static final double INTEGER_TO_LATITUDE = 10000d;

    protected static final int KILOMETERS_TO_METERS = 1000;
    
    /** An associative table that links every user parameter to its value */
    private final Map<UserParameter, Integer> panorama_;

    /**
     * Creates a new set of user parameters needed to create a panorama
     * 
     * @param panorama
     *            the user parameters linked to their values
     * @throws IllegalArgumentException
     *             if the given panorama doesn't contain a value for every user
     *             parameter
     * @throws NullPointerException
     *             if the given panorama is null
     */
    public PanoramaUserParameters(Map<UserParameter, Integer> panorama) {
        requireNonNull(panorama,
                "The informations given for the panorama are null");
        panorama = new EnumMap<>(panorama);
        checkArgument(panorama.size() == UserParameter.values().length,
                "The map does not contain the necessary informations for a panorama");

        sanitizePanoramaParameters(panorama);

        panorama_ = Collections.unmodifiableMap(new EnumMap<>(panorama));
    }

    /**
     * Creates a new set of user parameters needed to create a panorama
     * 
     * @param observerLongitude
     *            the longitude of the observer
     * @param observerLatitude
     *            the latitude of the observer
     * @param observerElevation
     *            the observers elevation, in meters
     * @param centerAzimuth
     *            the azimuth in the middle of the observers view, in radians
     * @param horizontalFieldOfView
     *            the angle representing how wide the observers view is, in
     *            radians
     * @param maxDistance
     *            the maximum distance the observer can see, in kilometers
     * @param width
     *            the width of the panorama that will be drawn, in pixels
     * @param height
     *            the height of the panorama that will be drawn, in pixels
     * @param superSamplingExponent
     *            the supersampling exponents value
     */
    public PanoramaUserParameters(int observerLongitude, int observerLatitude,
            int observerElevation, int centerAzimuth, int horizontalFieldOfView,
            int maxDistance, int width, int height, int superSamplingExponent) {
        this(parametersToMap(Arrays.asList(observerLongitude, observerLatitude,
                observerElevation, centerAzimuth, horizontalFieldOfView,
                maxDistance, width, height, superSamplingExponent)));
    }


    /**
     * Verifies that all panorama parameters satisfy their respective maximums
     * and minimums, and if they don't sanitizes them
     * 
     * @param panorama
     *            the panorama parameters that need to be checked
     */
    private static void sanitizePanoramaParameters(
            Map<UserParameter, Integer> panorama) {
        for (Map.Entry<UserParameter, Integer> pair : panorama.entrySet()) {
            panorama.put(pair.getKey(),
                    pair.getKey().sanitize(pair.getValue()));
        }

        int w = panorama.get(UserParameter.WIDTH);
        int vH = panorama.get(UserParameter.HORIZONTAL_FIELD_OF_VIEW);
        int hPotentiel = panorama.get(UserParameter.HEIGHT);

        final int maxHeight = 170 * (w - 1 / vH) + 1;
                
        if (hPotentiel > maxHeight) {
            panorama.put(UserParameter.HEIGHT, maxHeight);
        }
    }

    /**
     * Creates a map between user parameters and their values
     * 
     * @param parameterInArray
     *            the ordered list of values that needs to be assigned to their
     *            respective values
     * @return a map between user parameters and their values
     */
    private static Map<UserParameter, Integer> parametersToMap(
            List<Integer> parameterInArray) {

        Map<UserParameter, Integer> parameters = new EnumMap<>(
                UserParameter.class);

        for (UserParameter i : UserParameter.values()) {
            parameters.put(i, parameterInArray.get(i.ordinal()));
        }

        return parameters;
    }

    /**
     * Gives the value associated to a user parameter
     * 
     * @param uP
     *            the user parameters of which we want the value in this set of
     *            user panorama parameters
     * @return the value associated to uP
     */
    public int get(UserParameter uP) {
        return panorama_.get(uP);
    }

    /**
     * Gives the observers longitude in this
     * 
     * @return the observers longitude
     */
    public int observerLongitude() {
        return get(UserParameter.OBSERVER_LONGITUDE);
    }

    /**
     * Gives the observers latitude in this
     * 
     * @return the observers latitude
     */
    public int observerLatitude() {
        return get(UserParameter.OBSERVER_LATITUDE);
    }

    /**
     * Gives the observers elevation in this
     * 
     * @return the observers elevation
     */
    public int observerElevation() {
        return get(UserParameter.OBSERVER_ELEVATION);
    }

    /**
     * Gives the center azimuth in this
     * 
     * @return the center azimuth
     */
    public int centerAzimuth() {
        return get(UserParameter.CENTER_AZIMUTH);
    }

    /**
     * Gives the horizontal field of view in this
     * 
     * @return the horizontal field of view
     */
    public int horizontalFieldOfView() {
        return get(UserParameter.HORIZONTAL_FIELD_OF_VIEW);
    }

    /**
     * Gives the maximum distance the observer can see in this
     * 
     * @return the maximum distance the observer can see
     */
    public int maxDistance() {
        return get(UserParameter.MAX_DISTANCE);
    }

    /**
     * Gives the width of the panorama in this
     * 
     * @return the width of the panorama
     */
    public int width() {
        return get(UserParameter.WIDTH);
    }

    /**
     * Gives the height of the panorama in this
     * 
     * @return the height of the panorama
     */
    public int height() {
        return get(UserParameter.HEIGHT);
    }

    /**
     * Gives the supersampling exponent in this
     * 
     * @return the supersampling exponent
     */
    public int superSamplingExponent() {
        return get(UserParameter.SUPER_SAMPLING_EXPONENT);
    }

    /**
     * Gives a new set of panorama parameters (representing how the panorama
     * will be calculated) based on this set of user panorama parameters
     * 
     * @return a new set of panorama parameters based on this
     */
    public PanoramaParameters panoramaParameters() {
        return new PanoramaParameters(
                new GeoPoint(toRadians(observerLongitude() / INTEGER_TO_LONGITUDE),
                        toRadians(observerLatitude() / INTEGER_TO_LATITUDE)),
                observerElevation(), toRadians(centerAzimuth()),
                toRadians(horizontalFieldOfView()), maxDistance() * KILOMETERS_TO_METERS,
                (int) pow(2, superSamplingExponent()) * width(),
                (int) pow(2, superSamplingExponent()) * height());
    }

    /**
     * Gives a new set of panorama parameters (representing how the panorama
     * will be displayed) based on this set of user panorama parameters
     * 
     * @return a new set of panorama parameters based on this
     */
    public PanoramaParameters panoramaDisplayParameters() {
        return new PanoramaParameters(
                new GeoPoint(toRadians(observerLongitude() / INTEGER_TO_LONGITUDE),
                        toRadians(observerLatitude() / INTEGER_TO_LATITUDE)),
                observerElevation(), toRadians(centerAzimuth()),
                toRadians(horizontalFieldOfView()), maxDistance() * KILOMETERS_TO_METERS,
                width(), height());
    }

    @Override
    public boolean equals(Object other) {
        return ((other instanceof PanoramaUserParameters) && (panorama_
                .equals(((PanoramaUserParameters) other).panorama_)));
    }

    @Override
    public int hashCode() {
        return panorama_.hashCode();
    }

}
