package ch.epfl.alpano.gui;

/**
 * Represents a panorama labelizer, used to name some summit of the panorama
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.PanoramaComputer.DX;
import static ch.epfl.alpano.PanoramaComputer.rayToGroundDistance;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public final class Labelizer {

    /** The vertical limit to name summit, in pixels */
    private final static int MIN_DISTANCE_FROM_TOP = 170;

    /** The horizontal distance that separes summit's name, in pixels */
    private final static int PIXELS_PER_LABEL = 20;

    /** The minimum length of the line that separate the summit of its name */
    private final static int MIN_LINE_LENGTH = 20;

    /** The space that separate a line with its summit's name */
    private final static int SPACE_LINE_TO_TEXT = 2;

    /** The rotation we apply on the name of a summit, in degree */
    private final static int TEXT_ROTATION = -60;

    /**
     * The tolerance we accept (in meters) for an area centered in S, to be
     * associated to the summit S
     */
    private final static int RAY_TOLERANCE = 200;

    /** A Continuous Elevation Model in which the summits are found */
    private final ContinuousElevationModel cem_;

    /**
     * The list of summits that corresponds to the CEM above
     */
    private final List<Summit> summits_;

    /**
     * Constructs a Labelizer
     * 
     * @param cem the continuous elevation model of the panorama we want to labelize
     * @param summits the summits contained in the cem
     */
    public Labelizer(ContinuousElevationModel cem, List<Summit> summits) {
        cem_ = cem; 
        summits_ = new ArrayList<>(unmodifiableList(summits));
    }

    /**
     * Gives the list of label attached to a panorama
     * 
     * @param paramForLabel
     *            the panorama we to labeled
     * @return the list of label attached to paramForLabel (in the form of Node
     *         of JavaFX)
     */
    public List<Node> labels(PanoramaParameters paramForLabel) {
        /*
         * On trie d'abord parmi la liste des sommets visibles horizontalement,
         * ceux que l'on peut mettre - BitSet a initialisé sans les 20 premiers
         * et 20 derniers - parcourir le BitSet : Si les 19 suivants sont à 1,
         * mettre le sommet et : Mettre les 19 suivants à 0 Dans la parcour du
         * BitSet => i + 19 directement (Dans ce cas les bits ne seront pas
         * marquer par des 1, mais juste sauter)
         */
        List<VisibleSummit> visibleSummits = getVisibleSummits(paramForLabel);
        BitSet panoramaHIndex = new BitSet(paramForLabel.width());
        panoramaHIndex.set(PIXELS_PER_LABEL,
                paramForLabel.width() - PIXELS_PER_LABEL);

        List<Node> nodes = new ArrayList<>();
        if (!(visibleSummits.isEmpty())) {
            double yForLabel = visibleSummits.get(0).getY() - MIN_LINE_LENGTH
                    - SPACE_LINE_TO_TEXT;
    
            for (VisibleSummit vS : visibleSummits) {
                if (panoramaHIndex.get(vS.getX(), vS.getX() + PIXELS_PER_LABEL)
                        .cardinality() != PIXELS_PER_LABEL) {
                    continue;
                }
                panoramaHIndex.set(vS.getX(), vS.getX() + PIXELS_PER_LABEL, false);
    
                Text t = new Text(vS.getSummit().name() + " (" + vS.getSummit().elevation() + " m)");
                t.getTransforms().addAll(new Translate(vS.getX(), yForLabel), new Rotate(TEXT_ROTATION));
                Line l = new Line(vS.getX(), yForLabel + SPACE_LINE_TO_TEXT,
                        vS.getX(), vS.getY());
                nodes.add(t);
                nodes.add(l);
            }
        }
        return nodes;
    }

    /**
     * Gives the list of summit that are visible in a given panorama
     * 
     * @param paramForLabel
     *            the panorama we see
     * @return the list of summit that are visible in paramForLabel
     */
    private List<VisibleSummit> getVisibleSummits(
            PanoramaParameters paramForLabel) {
        List<VisibleSummit> visibleSummits = new ArrayList<>();

        Optional<VisibleSummit> visibleSummit;
        for (Summit s : summits_) {
            visibleSummit = isSummitVisible(s, paramForLabel);
            if (visibleSummit.isPresent()) {
                visibleSummits.add(visibleSummit.get());
            }
        }

        sort(visibleSummits, (v1, v2) -> {
            if (v1.getY() == v2.getY()) {
                return Integer.compare(v2.getSummit().elevation(),
                        v1.getSummit().elevation());
            }
            return Integer.compare(v1.getY(), v2.getY());
        });

        return visibleSummits;
    }

    /**
     * Check if a summit is visible in a given panorama
     * 
     * @param s
     *            a summit
     * @param pFL
     *            the panorama we see
     * @return an Optionnal containing the summit if it is visible, an empty
     *         Optional instance otherwise
     */
    private Optional<VisibleSummit> isSummitVisible(Summit s,
            PanoramaParameters pFL) {

        double distanceToSummit = s.position()
                .distanceTo(pFL.observerPosition());
        double azimuthH = pFL.observerPosition().azimuthTo(s.position());

        if (!(/* Check that the distance of the Summit is valid */
              (distanceToSummit <= pFL.maxDistance()) &&
              /* Check that the azimuth of the Summit is valid */
              (Math.abs(Math2.angularDistance(pFL.centerAzimuth(),
                azimuthH)) <= (pFL.horizontalFieldOfView() / 2.0)))) {
            return Optional.empty();
        }

        ElevationProfile elevationProfileToSummit = new ElevationProfile(cem_,
                pFL.observerPosition(), azimuthH, distanceToSummit);
        DoubleUnaryOperator rTGD1 = rayToGroundDistance(
                elevationProfileToSummit, pFL.observerElevation(), 0);
        double altitude = Math.atan2(-rTGD1.applyAsDouble(distanceToSummit),
                distanceToSummit);

        /* Check that the altitude of the Summit is valid */
        if (!((Math.abs(altitude) <= pFL.verticalFieldOfView() / 2.0)
                && (pFL.yForAltitude(altitude) >= MIN_DISTANCE_FROM_TOP))) {
            return Optional.empty();
        }

        DoubleUnaryOperator rTGD2 = rayToGroundDistance(
                elevationProfileToSummit, pFL.observerElevation(), altitude);
        double lowerBoundOfRay = Math2.firstIntervalContainingRoot(rTGD2, 0,
                distanceToSummit, DX);

        if (lowerBoundOfRay >= distanceToSummit - RAY_TOLERANCE) {
            return Optional.of(new VisibleSummit(s,
                    (int) Math.round(pFL.xForAzimuth(azimuthH)),
                    (int) Math.round(pFL.yForAltitude(altitude))));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Represents a summit that should be visible according to a panorama, only
     * use with {@link Labelizer#labels(PanoramaParameters)}
     * 
     * @author Ghali Chraibi (262251)
     * @author Niels Poulsen (270494)
     */
    private static final class VisibleSummit {

        /**
         * The summit (of class Summit, with all the information it contains)
         */
        private final Summit summit_;

        /**
         * The horizontal coordinate of the summit in the panorama (an index)
         */
        private final int xCoord_;

        /** The vertical coordinate of the summit in the panorama (an index) */
        private final int yCoord_;

        /**
         * Construct a visible summit
         * 
         * @param summit
         *            the summit it is based on
         * @param xCoord
         *            the horizontal coordinate of the summit in the panorama
         *            (from left to right)
         * @param yCoord
         *            the vertical coordinate of the summit in the panorama
         *            (from top to bottom)
         * @throws NullPointerException
         *             if the summit is null
         */
        public VisibleSummit(Summit summit, int xCoord, int yCoord) {
            summit_ = requireNonNull(summit);
            xCoord_ = xCoord;
            yCoord_ = yCoord;
        }

        /**
         * Gives the horizontal coordinate of the summit in the panorama (in
         * pixels)
         *
         * @return the horizontal coordinate of the summit in the panorama
         */
        public int getX() {
            return xCoord_;
        }

        /**
         * Gives the vertical coordinate of the summit in the panorama (in
         * pixels)
         *
         * @return the vertical coordinate of the summit in the panorama
         */
        public int getY() {
            return yCoord_;
        }

        /**
         * Gives the summit associated to this
         *
         * @return the summit associated to this
         */
        public Summit getSummit() {
            return summit_;
        }
    }

}
