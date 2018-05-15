package ch.epfl.alpano.dem;

/**
 * Represents a discrete Digital Elevation Model
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.toDegrees;

import ch.epfl.alpano.Interval2D;

public interface DiscreteElevationModel extends AutoCloseable {

    /** Number of samples per degree of a discret DEM */
    static int SAMPLES_PER_DEGREE = 3600;

    /** Number of samples per radian of a discret DEM */
    static double SAMPLES_PER_RADIAN = SAMPLES_PER_DEGREE * toDegrees(1);

    /**
     * Converts an angle in his index form
     * 
     * @param angle
     *            in radian
     * @return the index of the angle
     */
    static double sampleIndex(double angle) {
        return angle * SAMPLES_PER_RADIAN;
    }

    /**
     * Gives the extent of a DEM (the bi-dimensional interval regrouping all its
     * samples)
     * 
     * @return the extent of a DEM (represented here as a bi-dimendionnal
     *         interval)
     */
    Interval2D extent();

    /**
     * Gives the altitude of a sample (with a certain index)
     * 
     * @param x
     *            the index of the longitude of the given sample
     * @param y
     *            the index of the latitude of the given sample
     * @return the altitude of a sample (in meters)
     * @throws IllegalArgumentException
     *             if the index does not belong to the extent DEM
     */
    double elevationSample(int x, int y);

    /**
     * Gives a discret DEM that represents the union of 2 DEM
     * 
     * @param that
     *            another DEM
     * 
     * @return the union of this and that
     * @throws IllegalArgumentException
     *             if their extents are not unionable
     */
    default DiscreteElevationModel union(DiscreteElevationModel that) {
        checkArgument(this.extent().isUnionableWith(that.extent()),
                "The DEMs extents are not unionable");
        return new CompositeDiscreteElevationModel(this, that);
    }
}
