package ch.epfl.alpano.gui;

/**
 * Represents the user parameters of a panorama
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.runLater;

import java.beans.Beans;
import java.util.EnumMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class PanoramaParametersBean extends Beans {

    /** An observable {@link PanoramaUserParameters} */
    private final ObjectProperty<PanoramaUserParameters> parameters_;

    /**
     * A map that links a value to every parameter represented in UserParameter
     */
    private final Map<UserParameter, ObjectProperty<Integer>> parametersToValue_;

    /**
     * Construct a bean for the representation of paramenters for a panorama
     * 
     * @param parameters
     *            an initial set of PanoramaUserParameters
     * @throws NullPointerException
     *             if the parameters given are null
     */
    public PanoramaParametersBean(PanoramaUserParameters parameters) {
        parameters_ = new SimpleObjectProperty<>(requireNonNull(parameters,
                "The parameters given for the panorama bean are null"));
        parametersToValue_ = new EnumMap<>(UserParameter.class);
        for (UserParameter uP : UserParameter.values()) {
            parametersToValue_.put(uP,
                    new SimpleObjectProperty<>(parameters.get(uP)));
            parametersToValue_.get(uP).addListener((observable, oldV,
                    newV) -> runLater(this::synchronizeParameters));
        }
    }

    /**
     * Gives an unmodifiable set of panoramaUserParameters associated to this
     * 
     * @return an unmodifiable view of the panoramaUserParameters
     */
    public ReadOnlyObjectProperty<PanoramaUserParameters> parametersProperty() {
        return parameters_;
    }

    /**
     * Get the value associated to a given UserParameter
     * 
     * @param uP
     *            the UserParameter we want the value from
     * @return the value associated to uP
     */
    public ObjectProperty<Integer> get(UserParameter uP) {
        return parametersToValue_.get(uP);
    }

    /**
     * Gives a view of the value associated to the longitude in this
     * 
     * @return a view of the longitudes value
     */
    public ObjectProperty<Integer> observerLongitudeProperty() {
        return get(UserParameter.OBSERVER_LONGITUDE);
    }

    /**
     * Gives a view of the value associated to the latitude in this
     * 
     * @return a view of the latitudes value
     */
    public ObjectProperty<Integer> observerLatitudeProperty() {
        return get(UserParameter.OBSERVER_LATITUDE);
    }

    /**
     * Gives a view of the value associated to the observer elevation in this
     * 
     * @return a view of the observers elevation value
     */
    public ObjectProperty<Integer> observerElevationProperty() {
        return get(UserParameter.OBSERVER_ELEVATION);
    }

    /**
     * Gives a view of the value associated to the center azimuth in this
     * 
     * @return a view of the center azimuths value
     */
    public ObjectProperty<Integer> centerAzimuthProperty() {
        return get(UserParameter.CENTER_AZIMUTH);
    }

    /**
     * Gives a view of the value associated to the horizontal field of view in
     * this
     * 
     * @return a view of the horizontal field of views value
     */
    public ObjectProperty<Integer> horizontalFieldOfViewProperty() {
        return get(UserParameter.HORIZONTAL_FIELD_OF_VIEW);
    }

    /**
     * Gives a view of the value associated to the maximum distance in this
     * 
     * @return a view of the maximum distances value
     */
    public ObjectProperty<Integer> maxDistanceProperty() {
        return get(UserParameter.MAX_DISTANCE);
    }

    /**
     * Gives a view of the value associated to the width in this
     * 
     * @return a view of the widths value
     */
    public ObjectProperty<Integer> widthProperty() {
        return get(UserParameter.WIDTH);
    }

    /**
     * Gives a view of the value associated to the height in this
     * 
     * @return a view of the heights value
     */
    public ObjectProperty<Integer> heightProperty() {
        return get(UserParameter.HEIGHT);
    }

    /**
     * Gives a view of the value associated to the super sampling exponent in
     * this
     * 
     * @return a view of the super sampling exponents value
     */
    public ObjectProperty<Integer> superSamplingExponentProperty() {
        return get(UserParameter.SUPER_SAMPLING_EXPONENT);
    }

    /**
     * Synchronizes and {@link UserParameter#sanitize} the values for all
     * parameters of UserParameters
     */
    private void synchronizeParameters() {
        Map<UserParameter, Integer> newValues = new EnumMap<>(
                UserParameter.class);
        for (UserParameter uP : UserParameter.values()) {
            newValues.put(uP, get(uP).getValue());
        }
        PanoramaUserParameters newParameters = new PanoramaUserParameters(
                newValues);
        parameters_.set(newParameters);
        for (UserParameter uP : UserParameter.values()) {
            parametersToValue_.get(uP).set(newParameters.get(uP));
        }
    }
}
