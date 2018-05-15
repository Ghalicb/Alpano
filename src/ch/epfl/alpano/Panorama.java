package ch.epfl.alpano;

/**
 * Represents a panorama
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Arrays.fill;
import static java.util.Objects.requireNonNull;

public final class Panorama {

    /** The parameters this is based on */
    private final PanoramaParameters parameters_;

    /**
     * The real-world distances in between the observer and the points in the
     * panorama
     */
    private final float[] distances_;

    /** The real-world latitudes of the different points in the panorama */
    private final float[] latitudes_;

    /** The real-world longitudes of the different points in the panorama */
    private final float[] longitudes_;

    /** The real-world elevation of the different points in the panorama */
    private final float[] elevations_;

    /** The slopes of the different points in the panorama */
    private final float[] slopes_;

    /**
     * Fills the informations that describe a panorama
     * 
     * @param parameters
     *            the parameters of this panorama
     * @param distances
     *            the distances of all the points in this panorama
     * @param latitudes
     *            the latitudes of all the points in this panorama
     * @param longitudes
     *            the longitudes of all the points in this panorama
     * @param elevations
     *            the elevations of all the points in this panorama
     * @param slopes
     *            the slopes of all the points in this panorama
     * @throws NullPointerException
     *             if parameters is null
     */
    private Panorama(PanoramaParameters parameters, float[] distances,
            float[] latitudes, float[] longitudes, float[] elevations,
            float[] slopes) {
        parameters_ = parameters;
        distances_ = distances;
        latitudes_ = latitudes;
        longitudes_ = longitudes;
        elevations_ = elevations;
        slopes_ = slopes;
    }

    /**
     * Gives the parameters of this panorama
     * 
     * @return the parameters of this panorama
     */
    public PanoramaParameters parameters() {
        return parameters_;
    }

    /**
     * Gives the distance in between a point of position (x,y) and the observer
     * in the panorama
     * 
     * @param x
     *            the abscissa of a chosen point
     * @param y
     *            the ordinate of the chosen point
     * @return the real-world distance between the point and the observer
     * @throws IndexOutOfBoundsException
     *             if the point is not part of the panorama
     */
    public float distanceAt(int x, int y) {
        checkIndex(x, y, parameters_);
        return distances_[parameters_.linearSampleIndex(x, y)];
    }

    /**
     * Gives the distance in between a point of position (x,y) and the observer
     * in the panorama
     * 
     * @param x
     *            the abscissa of a chosen point
     * @param y
     *            the ordinate of the chosen point
     * @param d
     *            a default value
     * @return the real-world distance between the point and the observer if
     *         (x,y) is in this panorama, d otherwise
     */
    public float distanceAt(int x, int y, float d) {
        if (parameters_.isValidSampleIndex(x, y)) {
            return distanceAt(x, y);
        } else {
            return d;
        }
    }

    /**
     * Gives the latitude of a point in the panorama
     * 
     * @param x
     *            the abscissa of a chosen point
     * @param y
     *            the ordinate of the chosen point
     * @return the latitude of the point in the panorama
     * @throws IndexOutOfBoundsException
     *             if the point is not part of the panorama
     */
    public float latitudeAt(int x, int y) {
        checkIndex(x, y, parameters_);
        return latitudes_[parameters_.linearSampleIndex(x, y)];
    }

    /**
     * Gives the longitude of a point in the panorama
     * 
     * @param x
     *            the abscissa of a chosen point
     * @param y
     *            the ordinate of the chosen point
     * @return the longitude of the point in the panorama
     * @throws IndexOutOfBoundsException
     *             if the point is not part of the panorama
     */
    public float longitudeAt(int x, int y) {
        checkIndex(x, y, parameters_);
        return longitudes_[parameters_.linearSampleIndex(x, y)];
    }

    /**
     * Gives the elevation of a point in the panorama
     * 
     * @param x
     *            the abscissa of a chosen point
     * @param y
     *            the ordinate of the chosen point
     * @return the elevation of the point in the panorama
     * @throws IndexOutOfBoundsException
     *             if the point is not part of the panorama
     */
    public float elevationAt(int x, int y) {
        checkIndex(x, y, parameters_);
        return elevations_[parameters_.linearSampleIndex(x, y)];
    }

    /**
     * Gives the slope of a point in the panorama
     * 
     * @param x
     *            the abscissa of a chosen point
     * @param y
     *            the ordinate of the chosen point
     * @return the slope of the point in the panorama
     * @throws IndexOutOfBoundsException
     *             if the point is not part of the panorama
     */
    public float slopeAt(int x, int y) {
        checkIndex(x, y, parameters_);
        return slopes_[parameters_.linearSampleIndex(x, y)];
    }

    /**
     * Check if a point of index (x,y) belongs to this panorama
     * 
     * @param x
     *            the abscissa of the point
     * @param y
     *            the ordinate of the point
     * @throws IndexOutOfBoundsException
     *             if the point is not in this panorama
     */
    private static void checkIndex(int x, int y,
            PanoramaParameters parameters) {
        if (!parameters.isValidSampleIndex(x, y)) {
            throw new IndexOutOfBoundsException(
                    "The index given are out of bounds of the panorama !");
        }
    }

    /**
     * Build a panorama
     * 
     * @author Ghali Chraibi (262251)
     * @author Niels Poulsen (270494)
     */
    public static final class Builder {

        /** The parameters this is based on */
        private final PanoramaParameters parameters_;

        /**
         * The real-world distances in between the observer and the points in
         * the panorama
         */
        private float[] distances_;

        /** The real-world latitudes of the different points in the panorama */
        private float[] latitudes_;

        /** The real-world longitudes of the different points in the panorama */
        private float[] longitudes_;

        /** The real-world elevation of the different points in the panorama */
        private float[] elevations_;

        /** The slopes of the different points in the panorama */
        private float[] slopes_;

        /** Whether the panorama has already been built or not */
        private boolean built_;

        /**
         * Construct an empty panorama
         * 
         * @param parameters
         *            the parameters of this panorama
         * @throws NullPointerException
         *             if parameters is null
         */
        public Builder(PanoramaParameters parameters) {
            parameters_ = requireNonNull(parameters);
            int size = parameters_.width() * parameters_.height();

            distances_ = new float[size];
            fill(distances_, Float.POSITIVE_INFINITY);
            latitudes_ = new float[size];
            longitudes_ = new float[size];
            elevations_ = new float[size];
            slopes_ = new float[size];

            built_ = false;
        }

        /**
         * Set the distance of a point at a certain index
         * 
         * @param x
         *            the horizontal coordinate of the sample index
         * @param y
         *            the vertical coordinate of the sample index
         * @param distance
         *            the distance of the point at position (x,y)
         * @return this
         * @throws IllegalStateException
         *             if this has already been built
         */
        public Builder setDistanceAt(int x, int y, float distance) {
            checkIndex(x, y, parameters_);
            alreadyBuilded();
            distances_[parameters_.linearSampleIndex(x, y)] = distance;
            return this;
        }

        /**
         * Set the longitude of a point at a certain index
         * 
         * @param x
         *            the horizontal coordinate of the sample index
         * @param y
         *            the vertical coordinate of the sample index
         * @param longitude
         *            the longitude of the point at position (x,y)
         * @return this
         * @throws IllegalStateException
         *             if this has already been built
         */
        public Builder setLongitudeAt(int x, int y, float longitude) {
            checkIndex(x, y, parameters_);
            alreadyBuilded();
            longitudes_[parameters_.linearSampleIndex(x, y)] = longitude;
            return this;
        }

        /**
         * Set the latitude of a point at a certain index
         * 
         * @param x
         *            the horizontal coordinate of the sample index
         * @param y
         *            the vertical coordinate of the sample index
         * @param latitude
         *            the latitude of the point at position (x,y)
         * @return this
         * @throws IllegalStateException
         *             if this has already been built
         */
        public Builder setLatitudeAt(int x, int y, float latitude) {
            checkIndex(x, y, parameters_);
            alreadyBuilded();
            latitudes_[parameters_.linearSampleIndex(x, y)] = latitude;
            return this;
        }

        /**
         * Set the elevation of a point at a certain index
         * 
         * @param x
         *            the horizontal coordinate of the sample index
         * @param y
         *            the vertical coordinate of the sample index
         * @param elevation
         *            the elevation of the point at position (x,y)
         * @return this
         * @throws IllegalStateException
         *             if this has already been built
         */
        public Builder setElevationAt(int x, int y, float elevation) {
            checkIndex(x, y, parameters_);
            alreadyBuilded();
            elevations_[parameters_.linearSampleIndex(x, y)] = elevation;
            return this;
        }

        /**
         * Set the slope of a point at a certain index
         * 
         * @param x
         *            the horizontal coordinate of the sample index
         * @param y
         *            the vertical coordinate of the sample index
         * @param slope
         *            the slope of the point at position (x,y)
         * @return this
         * @throws IllegalStateException
         *             if this has already been built
         */
        public Builder setSlopeAt(int x, int y, float slope) {
            checkIndex(x, y, parameters_);
            alreadyBuilded();
            slopes_[parameters_.linearSampleIndex(x, y)] = slope;
            return this;
        }

        /**
         * Build a final panorama based on the information accumulated in this
         * 
         * @return a unique panorama based on this
         */
        public Panorama build() {
            alreadyBuilded();
            built_ = true;
            
            Panorama newP = new Panorama(parameters_, distances_, latitudes_,
                    longitudes_, elevations_, slopes_);
            
            // On libère la mémoire des tableaux
            distances_ = latitudes_ = longitudes_ = elevations_ = slopes_ = null;
            
            return newP;
        }

        /**
         * Check if this has already been built
         * 
         * @throws IllegalStateException
         *             if it has already been built
         */
        private void alreadyBuilded() {
            if (built_) {
                throw new IllegalStateException(
                        "The panorama has already been built !");
            }
        }
    }
}
