package ch.epfl.alpano.dem;

/**
 * Represents a union of two DEMs
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.Preconditions.checkArgument;

import ch.epfl.alpano.Interval2D;

final class CompositeDiscreteElevationModel implements DiscreteElevationModel {

    /** The first DEM of this union */
    private final DiscreteElevationModel dem1_;

    /** The second DEM of this union */
    private final DiscreteElevationModel dem2_;

    /** The extent of this Composite DEM */
    private final Interval2D extent_;

    /**
     * Builds a new Composite DEM from two other DEMs
     * 
     * @param dem1
     *            the first DEM in this union
     * @param dem2
     *            the second DEM in this union
     * @throws NullPointerException
     *             if one of the DEMs is empty
     */
    public CompositeDiscreteElevationModel(DiscreteElevationModel dem1,
            DiscreteElevationModel dem2) {
        dem1_ = requireNonNull(dem1, "The first DEM is null");
        dem2_ = requireNonNull(dem2, "The second DEM is null");
        extent_ = dem1_.extent().union(dem2_.extent());
    }

    @Override
    public void close() throws Exception {
        dem1_.close();
        dem2_.close();
    }

    @Override
    public Interval2D extent() {
        return extent_;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(
                dem1_.extent().contains(x, y) || dem2_.extent().contains(x, y),
                "The index is not in this Composite DEM");
        if (dem1_.extent().contains(x, y)) {
            return dem1_.elevationSample(x, y);
        } else {
            return dem2_.elevationSample(x, y);
        }
    }

}
