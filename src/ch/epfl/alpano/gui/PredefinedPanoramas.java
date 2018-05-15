package ch.epfl.alpano.gui;

/**
 * Represents a collection of predefined instances of user panorama parameters
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public interface PredefinedPanoramas {

    /** Standards constants used for predefined panorama */
    public final static int STANDARD_MAX_DISTANCE = 300, STANDARD_WIDTH = 2500,
            STANDARD_HEIGHT = 800, STANDARD_SAMPLING_EXPONENT = 0;

    /**
     * Represents the user panorama parameters based on the view of the Niesen
     * mountain from the Thun lake
     */
    PanoramaUserParameters Niesen = new PanoramaUserParameters(76500, 467300,
            600, 180, 110, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);

    /**
     * Represents the user panorama parameters based on the view of the Alps
     * from the Jura mountains
     */
    PanoramaUserParameters AlpesDuJura = new PanoramaUserParameters(68087,
            470085, 1380, 162, 27, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);

    /**
     * Represents the user panorama parameters based on the view of the Mont
     * Racine
     */
    PanoramaUserParameters MontRacine = new PanoramaUserParameters(68200,
            470200, 1500, 135, 45, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);

    /**
     * Represents the user panorama parameters based on the view of the
     * Finsteraarhorn
     */
    PanoramaUserParameters Finsteraarhorn = new PanoramaUserParameters(81260,
            465374, 4300, 205, 20, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);

    /**
     * Represents the user panorama parameters based on the view from the
     * Sauvanelin Tower
     */
    PanoramaUserParameters TourDeSauvabelin = new PanoramaUserParameters(66385,
            465353, 700, 135, 100, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);

    /**
     * Represents the user panorama parameters based on the view from the
     * Pelican Beach
     */
    PanoramaUserParameters PlageDuPelican = new PanoramaUserParameters(65728,
            465132, 380, 135, 60, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);

    PanoramaUserParameters GhaliLausanne = new PanoramaUserParameters(66348,
            465176, 500, 180, 75, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);
    
    PanoramaUserParameters NielsNeuchatel = new PanoramaUserParameters(69654,
            470133, 770, 150, 60, STANDARD_MAX_DISTANCE, STANDARD_WIDTH,
            STANDARD_HEIGHT, STANDARD_SAMPLING_EXPONENT);
}
