package ch.epfl.alpano.dem;

/**
 * Represents a continuous Digital Elevation Model (obtained by interpolation of a discrete one)
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.bilerp;
import static ch.epfl.alpano.Math2.sq;
import static ch.epfl.alpano.dem.DiscreteElevationModel.SAMPLES_PER_RADIAN;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;
import static java.lang.Math.acos;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import ch.epfl.alpano.GeoPoint;

public final class ContinuousElevationModel {

    /** The distance between two discrete samples (chosen arbitrarily) */
    private final static double DISTANCE_PER_SAMPLES = toMeters(
            1 / SAMPLES_PER_RADIAN);

    /** The discrete DEM that will be use to create a continuous one */
    private final DiscreteElevationModel dem_;

    /**
     * Constructs a continuous DEM
     * 
     * @param dem
     *            a discrete Digital Elevation Model
     * @throws NullPointerException
     *             if the discrete DEM passed in argument is null
     */
    public ContinuousElevationModel(DiscreteElevationModel dem) {
        dem_ = requireNonNull(dem, "The DEM given is null");
    }

    /**
     * Gives the altitude of a (Geo)point contained in this
     * 
     * @param p
     *            a GeoPoint
     * @return the altitude of the GeoPoint in meters
     */
    public double elevationAt(GeoPoint p) {
        double pIndexLong = sampleIndex(p.longitude());
        double pIndexLat = sampleIndex(p.latitude());

        double flooredLong = floor(pIndexLong);
        double flooredLat = floor(pIndexLat);

        // We create the closest four points that are surrounding our GeoPoint
        double z00 = elevationDiscreteDEM((int) flooredLong, (int) flooredLat);
        double z10 = elevationDiscreteDEM((int) (flooredLong + 1),
                (int) flooredLat);
        double z01 = elevationDiscreteDEM((int) flooredLong,
                (int) (flooredLat + 1));
        double z11 = elevationDiscreteDEM((int) (flooredLong + 1),
                (int) (flooredLat + 1));

        return bilerp(z00, z10, z01, z11, pIndexLong - flooredLong,
                pIndexLat - flooredLat);
    }

    /**
     * Give the altitude of a (Geo)point contained in the discrete DEM
     * associated with this
     * 
     * @param indexSampleX
     *            the index of the longitude of a chosen point
     * @param indexSampleY
     *            the index of the latitude of a chosen point
     * @return the altitude of the point of coordinates (indexSampleX,
     *         indexSampleY) in meters
     */
    private double elevationDiscreteDEM(int indexSampleX, int indexSampleY) {
        if (dem_.extent().contains(indexSampleX, indexSampleY)) {
            return dem_.elevationSample(indexSampleX, indexSampleY);
        } else {
            return 0;
        }
    }

    /**
     * Give the slope of a (Geo)point contained in this
     * 
     * @param p
     *            a GeoPoint
     * @return the slope of p in radians
     */
    public double slopeAt(GeoPoint p) {
        int x = (int) floor(sampleIndex(p.longitude()));
        int y = (int) floor(sampleIndex(p.latitude()));

        double slope00 = slopeDiscreteDEM(x, y);
        double slope10 = slopeDiscreteDEM(x + 1, y);
        double slope01 = slopeDiscreteDEM(x, y + 1);
        double slope11 = slopeDiscreteDEM(x + 1, y + 1);

        return bilerp(slope00, slope10, slope01, slope11,
                sampleIndex(p.longitude()) - x, sampleIndex(p.latitude()) - y);
    }

    /**
     * Give the slope of a (Geo)point contained in the discrete DEM associated
     * with this
     * 
     * @param indexSampleX
     *            the index of the longitude of a chosen point
     * @param indexSampleY
     *            the index of the latitude of a chosen point
     * @return the altitude of the point of coordinates (indexSampleX,
     *         indexSampleY) in radians
     */
    private double slopeDiscreteDEM(int indexSampleX, int indexSampleY) {
        double z00 = elevationDiscreteDEM(indexSampleX, indexSampleY);
        double z10 = elevationDiscreteDEM(indexSampleX + 1, indexSampleY);
        double z01 = elevationDiscreteDEM(indexSampleX, indexSampleY + 1);
        double root = sqrt(
                sq(z01 - z00) + sq(z10 - z00) + sq(DISTANCE_PER_SAMPLES));
        return acos(DISTANCE_PER_SAMPLES / root);
    }
}
