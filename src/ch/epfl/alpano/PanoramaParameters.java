package ch.epfl.alpano;

/**
 * Represents the parameters needed to draw a panorama
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;

public final class PanoramaParameters {

    /** The position of the observer who is seeing the panorama */
    private final GeoPoint observerPosition_;

    /** The elevation of the observer who is seeing the panorama, in meters */
    private final int observerElevation_;

    /** The azimuth of the center of the panorama (from the north) in radians */
    private final double centerAzimuth_;

    /** The angle representing how wide the observers view is, in radians */
    private final double horizontalFieldOfView_;

    /** The furthest the observer can see, in meters */
    private final int maxDistance_;

    /** The width of the panorama that will be drawn, in pixels */
    private final int width_;

    /** The height of the panorama that will be drawn, in pixels */
    private final int height_;

    /** The angle in between two consecutive pixels */
    private final double delta_;
    
    /** The angle reprenting how large is the peripheral view of the observers, in radians */
    private final double verticalFieldOfView_;

    /**
     * Constructs a new set of parameters needed to draw a panorama
     * 
     * @param observerPosition
     *            the position of the person that sees the panorama (the
     *            observer)
     * @param observerElevation
     *            the observers elevation, in meters
     * @param centerAzimuth
     *            the azimuth in the middle of the observers view, in radians
     * @param horizontalFieldOfView
     *            the angle representing how wide the observers view is, in
     *            radians
     * @param maxDistance
     *            the maximum distance the observer can see, in meters
     * @param width
     *            the width of the panorama that will be drawn, in pixels
     * @param height
     *            the height of the panorama that will be drawn, in pixels
     * @throws NullpointerException
     *             if the observers position is null
     * @throws IllegalArgumentException
     *             if the center azimuth is not canonical, the horizontal field
     *             of view is smaller than 0 or bigger or equal to PI2, or if
     *             the maxDistance is not strictly positive, or the width or
     *             height are not strictly bigger than 1
     */
    public PanoramaParameters(GeoPoint observerPosition, int observerElevation,
            double centerAzimuth, double horizontalFieldOfView, int maxDistance,
            int width, int height) {
        observerPosition_ = requireNonNull(observerPosition,
                "The GeoPoint representing the observers position is null");
        checkArgument(isCanonical(centerAzimuth),
                "The azimuth is not canonical");
        checkArgument(horizontalFieldOfView > 0 && horizontalFieldOfView <= PI2,
                "The horizontal field of view is invalid");
        checkArgument(maxDistance > 0 && width > 1 && height > 1,
                "The components of the panorama are not strictly positive");
        centerAzimuth_ = centerAzimuth;
        observerElevation_ = observerElevation;
        horizontalFieldOfView_ = horizontalFieldOfView;
        maxDistance_ = maxDistance;
        width_ = width;
        height_ = height;
        delta_ = horizontalFieldOfView_ / (width_ - 1);
        verticalFieldOfView_ = delta_ * (height_ - 1);
    }

    /**
     * Calculates the azimuth of a pixel when given its horizontal index
     * 
     * @param x
     *            the pixels horizontal index
     * @return the azimuth corresponding to the pixels position
     * @throws IllegalArgumentException
     *             if the index of the pixel is not included in the panoramas
     *             indexes
     */
    public double azimuthForX(double x) {
        checkArgument(x >= 0 && x <= width() - 1,
                "The index is not in the field of view");
        double westAzimuth = centerAzimuth() - (0.5 * horizontalFieldOfView());
        return canonicalize(westAzimuth + delta_ * x);
    }

    /**
     * Calculates the horizontal index of a pixel when given its azimuth
     * 
     * @param a
     *            the pixels azimuth
     * @return the horizontal index corresponding to the pixels position
     * @throws IllegalArgumentException
     *             if the azimuth of the pixel is not included in the horizontal
     *             field of view or if the azimuth is not canonical
     */
    public double xForAzimuth(double a) {
        double angularDistance = angularDistance(centerAzimuth(), a);
        checkArgument(isCanonical(a), "The angle is not canonical");
        checkArgument(abs(angularDistance) <= 0.5 * horizontalFieldOfView(),
                "The angle is not in the field of view");
        return (width() - 1) / 2.0 + (angularDistance / delta_);
    }

    /**
     * Calculates the altitude of a pixel when given its vertical index
     * 
     * @param y
     *            the pixels vertical index
     * @return the altitude corresponding to the pixels position
     * @throws IllegalArgumentException
     *             if the index of the pixel is not included in the panoramas
     *             indexes
     */
    public double altitudeForY(double y) {
        checkArgument(y >= 0 && y <= height() - 1,
                "The index is not in the field of view");
        return (0.5 * verticalFieldOfView()) - delta_ * y;
    }

    /**
     * Calculates the vertical index of a pixel when given its altitude
     * 
     * @param a
     *            the pixels altitude
     * @return the vertical index corresponding to the pixels position
     * @throws IllegalArgumentException
     *             if the altitude of the pixel is not included in the vertical
     *             field of view
     */
    public double yForAltitude(double a) {
        checkArgument(abs(a) <= 0.5 * verticalFieldOfView(),
                "The altitude is not in the field of view");
        return (0.5 * verticalFieldOfView() - a) / delta_;
    }

    /**
     * Checks if a sample index is contained in the panorama
     * 
     * @param x
     *            the horizontal coordinate of the sample index
     * @param y
     *            the vertical coordinate of the sample index
     * @return true if the sample index is in the panorama, false if it is not.
     */
    boolean isValidSampleIndex(int x, int y) {
        return (x >= 0) && (x < width()) && (y >= 0) && (y < height());
    }

    /**
     * Transforms a pixels sample index (in coordinates) into a linear sample
     * index
     * 
     * @param x
     *            the horizontal coordinate of the sample index
     * @param y
     *            the vertical coordinate of the sample index
     * @return the pixels linear sample index
     */
    int linearSampleIndex(int x, int y) {
        return x + (width() * y);
    }

    /**
     * Gives the position of the observer who is seeing the panorama
     * 
     * @return the position of the observer
     */
    public GeoPoint observerPosition() {
        return observerPosition_;
    }

    /**
     * Gives the elevation of the observer who is seeing the panorama
     * 
     * @return the elevation of the observer who is seeing the panorama (in
     *         meters)
     */
    public int observerElevation() {
        return observerElevation_;
    }

    /**
     * Gives the azimuth of the center of the panorama (from the north)
     * 
     * @return the azimuth in the middle of the observers view (in radians)
     */
    public double centerAzimuth() {
        return centerAzimuth_;
    }

    /**
     * Gives the angle representing how wide the observers view is
     * 
     * @return the angle representing how wide the observers view is (in
     *         radians)
     */
    public double horizontalFieldOfView() {
        return horizontalFieldOfView_;
    }

    /**
     * Gives the maximum distance the observer can see
     * 
     * @return the maximum distance the observer can see (in meters)
     */
    public int maxDistance() {
        return maxDistance_;
    }

    /**
     * Gives the width of the image
     * 
     * @return the width of the image, in pixels
     */
    public int width() {
        return width_;
    }

    /**
     * Gives the height of the image
     * 
     * @return the height of the image, in pixels
     */
    public int height() {
        return height_;
    }

    /**
     * Calculates the vertical field of view
     * 
     * @return the vertical field of view, in radians
     */
    public double verticalFieldOfView() {
        return verticalFieldOfView_;
    }

}
