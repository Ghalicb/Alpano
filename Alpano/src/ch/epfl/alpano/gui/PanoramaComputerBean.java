package ch.epfl.alpano.gui;

/**
 * Contains all the computable elements needed to display a panorama
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;

import java.util.List;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.summit.Summit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;

public final class PanoramaComputerBean {

    /** A view of the {@link PanoramaUserParameters} */
    private final ObjectProperty<PanoramaUserParameters> parameters_;

    /**
     * A view of the {@link Panorama} based on the PanoramaUserParameters in
     * this
     */
    private final ObjectProperty<Panorama> panorama_;

    /**
     * A view of the {@link Image} based on the PanoramaUserParameters in this
     */
    private final ObjectProperty<Image> image_;

    /**
     * A view of the observable list of {@link Node} based on the
     * PanoramaUserParameters in this
     */
    private final ObjectProperty<ObservableList<Node>> labelsProp_;

    /**
     * The observable list of {@link Node} based on the PanoramaUserParameters
     * in this
     */
    private final ObservableList<Node> labels_;

    /**
     * Constructs a computer bean based on a continuous elevation model and a
     * list of summits
     * 
     * @param summits
     *            the list of summits used to create this
     * @param cem
     *            the CEM used to create this
     */
    public PanoramaComputerBean(List<Summit> summits,
            ContinuousElevationModel cem) {
        PanoramaComputer pComputer = new PanoramaComputer(cem);
        Labelizer labelizer = new Labelizer(cem, summits);

        parameters_ = new SimpleObjectProperty<>();
        panorama_ = new SimpleObjectProperty<>();
        image_ = new SimpleObjectProperty<>();
        labels_ = FXCollections.observableArrayList();
        labelsProp_ = new SimpleObjectProperty<>(
                FXCollections.unmodifiableObservableList(labels_));

        parameters_.addListener((observable, oldV, newV) -> {
            panorama_.set(pComputer.computePanorama(newV.panoramaParameters()));

            labels_.clear();
            labels_.setAll(labelizer.labels(newV.panoramaDisplayParameters()));

            ImagePainter painterForImage = getDefaultImagePainter();
            image_.set(renderPanorama(panorama_.get(), painterForImage));
        });

    }

    /**
     * Gives a view of the PanoramaUserParameters associated to this
     * 
     * @return a view of the PanoramaUserParameters
     */
    public ObjectProperty<PanoramaUserParameters> parametersProperty() {
        return parameters_;
    }

    /**
     * Gives the PanoramaUserParameters associated to this
     * 
     * @return the PanoramaUserParameters
     */
    public PanoramaUserParameters getParameters() {
        return parameters_.get();
    }
 
    /**
     * Sets the PanoramaUserParameters for this
     * 
     * @param newParameters the PanoramaUserParameters we want to affect to this 
     */
    public void setParameters(PanoramaUserParameters newParameters) {
        parameters_.set(newParameters);
    }

    /**
     * Gives an unmodifiable view of the Panorama associated to this
     * 
     * @return an unmodifiable view of the Panorama
     */
    public ReadOnlyObjectProperty<Panorama> panoramaProperty() {
        return panorama_;
    }

    /**
     * Gives the Panorama associated to this
     * 
     * @return the Panorama
     */
    public Panorama getPanorama() {
        return panorama_.get();
    }

    /**
     * Gives an unmodifiable view of the Image associated to this
     * 
     * @return an unmodifiable view of the Image
     */
    public ReadOnlyObjectProperty<Image> imageProperty() {
        return image_;
    }

    /**
     * Gives the Image associated to this
     * 
     * @return the Image 
     */
    public Image getImage() {
        return image_.get();
    }

    /**
     * Gives an unmodifiable view of the observable list of Nodes associated to this
     * 
     * @return an unmodifiable view of the observable list of Nodes
     */
    public ReadOnlyObjectProperty<ObservableList<Node>> labelsProperty() {
        return labelsProp_;
    }

    /**
     * Gives the observable list of Nodes associated to this
     * 
     * @return the observable list of the Nodes
     */
    public ObservableList<Node> getLabels() {
        return labelsProp_.get();
    }

    /**
     * Generates a default ImagePainter to create the image for this
     * 
     * @return the default ImagePainter
     */
    private ImagePainter getDefaultImagePainter() {
        ChannelPainter distance = getPanorama()::distanceAt;
        ChannelPainter slope = getPanorama()::slopeAt;
        ChannelPainter hue = distance.div(100000).cycling().mul(360);
        ChannelPainter saturation = distance.div(200000).clamped().inverted();
        ChannelPainter brightness = slope.mul(2).div((float) Math.PI).inverted()
                .mul(0.7f).add(0.3f);
        ChannelPainter opacity = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return ImagePainter.hsb(hue, saturation, brightness, opacity);
    }
}
