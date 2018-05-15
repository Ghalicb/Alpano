package ch.epfl.alpano.gui;

/**
 * The simulation of Swiss alpine panoramas
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.gui.PanoramaUserParameters.INTEGER_TO_LATITUDE;
import static ch.epfl.alpano.gui.PanoramaUserParameters.INTEGER_TO_LONGITUDE;
import static ch.epfl.alpano.gui.PanoramaUserParameters.KILOMETERS_TO_METERS;
import static ch.epfl.alpano.gui.UserParameter.CENTER_AZIMUTH;
import static ch.epfl.alpano.gui.UserParameter.HEIGHT;
import static ch.epfl.alpano.gui.UserParameter.HORIZONTAL_FIELD_OF_VIEW;
import static ch.epfl.alpano.gui.UserParameter.MAX_DISTANCE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_ELEVATION;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LATITUDE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LONGITUDE;
import static ch.epfl.alpano.gui.UserParameter.WIDTH;
import static ch.epfl.alpano.summit.GazetteerParser.readSummitsFrom;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public final class Alpano extends Application {

    private static final int PREF_ROW_NUMBER_TEXT_AREA = 2;

    private static final int PREF_COLUMN_SIZE_SMALL = 3;

    private static final int PREF_COLUMN_SIZE_MEDIUM = 4;

    private static final int PREF_COLUMN_SIZE_BIG = 7;
    
    private static final int VERTICAL_GAP_BETWEEN_GRID_ELEMENT = 3;

    private static final int HORIZONTAL_GAP_BETWEEN_GRID_ELEMENT = 10;
    
    private static final int[] GRIDPANE_INSETS = {7,5,5,5};

    private static final Color BACKGROUND_COLOR_OF_NOTICE = 
            new Color(1f, 1f, 1f, 0.9f);

    private static final int FONT_SIZE_UPDATE_NOTICE = 40;
    
    // Extension
    private static final int ALTITUDE_MARGIN_FROM_GROUND = 20;

    /** The Continuous Elevation Model used in this application*/
    private final ContinuousElevationModel cem;
    
    /** The list of summits that can be displayed in this application*/
    private final List<Summit> summits;
    
    /** The PanoramaParametersBean used to update the parameters*/
    private final PanoramaParametersBean parameters;
    
    /** The PanoramaComputerBean used to compute the image*/
    private final PanoramaComputerBean computer;

    /**
     * Sets the initial values for the application
     * 
     * @throws IOException  
     *          if there is a I/O problem while reading the file 
     *          or if the file does not respect the right format
     */
    public Alpano() throws IOException {
        cem = getCEM();
        summits = readSummitsFrom(new File("res/alps.txt"));
        parameters = new PanoramaParametersBean(
                PredefinedPanoramas.AlpesDuJura);
        computer = new PanoramaComputerBean(summits, cem);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Main StackPane creation (containing the image of the panorama)

        ImageView panoView = setPanoramaView();
        panoView.setOnMouseClicked(value -> pointToWeb(value));

        Pane labelsPane = setPanoramaLabel();

        StackPane panoPane = centerStackPane(panoView, labelsPane);

        // Main GridPane creation (containing the parameters and the information)

        TextArea parametersArea = setTextArea();
        panoView.setOnMouseMoved(value -> pointToInfo(value, parametersArea));

        GridPane paramsGrid = bottomParametersGrid(parametersArea);

        /*
         * Extension : Menu Bar creation (containing file options, tools,
         * predefined panoramas and help)
         */

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = fileMenu(primaryStage);
        Menu toolMenu = toolMenu();
        Menu predefinedPanoramaMenu = predefinedPanoramaMenu();
        Menu helpMenu = helpMenu(primaryStage);

        menuBar.getMenus().setAll(fileMenu, toolMenu, predefinedPanoramaMenu,
                helpMenu);

        // Assembling all the parts

        BorderPane root = new BorderPane(panoPane, menuBar, null, paramsGrid,
                null);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Alpano");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Constructs a ContinuousElevationModel based on HGT Files that covers the Swiss alps
     * 
     * @return the constructed CEM
     */
    @SuppressWarnings("resource")
    private ContinuousElevationModel getCEM() {
        HgtDiscreteElevationModel hgt1 = new HgtDiscreteElevationModel(
                new File("res/N45E006.hgt"));
        HgtDiscreteElevationModel hgt2 = new HgtDiscreteElevationModel(
                new File("res/N45E007.hgt"));
        HgtDiscreteElevationModel hgt3 = new HgtDiscreteElevationModel(
                new File("res/N45E008.hgt"));
        HgtDiscreteElevationModel hgt4 = new HgtDiscreteElevationModel(
                new File("res/N45E009.hgt"));
        HgtDiscreteElevationModel hgt5 = new HgtDiscreteElevationModel(
                new File("res/N46E006.hgt"));
        HgtDiscreteElevationModel hgt6 = new HgtDiscreteElevationModel(
                new File("res/N46E007.hgt"));
        HgtDiscreteElevationModel hgt7 = new HgtDiscreteElevationModel(
                new File("res/N46E008.hgt"));
        HgtDiscreteElevationModel hgt8 = new HgtDiscreteElevationModel(
                new File("res/N46E009.hgt"));

        DiscreteElevationModel dem = hgt1.union(hgt2).union(hgt3).union(hgt4)
                .union(hgt5.union(hgt6).union(hgt7).union(hgt8));

        return new ContinuousElevationModel(dem);
    }

    /**
     * Gives the ponderated (by the superSamplingIndex) value of one of the mouses index
     * 
     * @return the ponderated index
     */
    private int getDisplayedIndex(double originalValue) {
        return (int) (originalValue*Math.pow(2,computer.getParameters().superSamplingExponent()));
    }

    /**
     * Assembles the central panes for our application
     * 
     * @param panoView 
     *              the image representing our panorama
     * @param labelsPane 
     *              the pane containing the nodes to label the summits
     * @return the assembled central pane
     */
    private StackPane centerStackPane(ImageView panoView, Pane labelsPane) {

        StackPane panoGroup = new StackPane(panoView, labelsPane);
        ScrollPane panoScrollPane = new ScrollPane(panoGroup);

        Text updateText = new Text("Les paramètres du panorama ont changé. \n "
                + "Cliquez ici pour mettre le dessin à jour.");
        updateText.setFont(new Font(FONT_SIZE_UPDATE_NOTICE));
        updateText.setTextAlignment(TextAlignment.CENTER);

        StackPane updateNotice = new StackPane(updateText);
        setUpdateNotice(updateNotice);

        return new StackPane(panoScrollPane, updateNotice);
    }

    /**
     * Formats and links the content of an Imageview to the displayed image
     * 
     * @return a new ImageView linked to the displayed image
     */
    private ImageView setPanoramaView() {
        ImageView panoView = new ImageView();
        panoView.fitWidthProperty().bind(parameters.widthProperty());
        panoView.imageProperty().bind(computer.imageProperty());
        panoView.preserveRatioProperty().set(true);
        panoView.smoothProperty().set(true);

        return panoView;
    }

    /**
     * Constructs a pane containing all the labels to be displayed
     * 
     * @return a new Pane linked to the labels that need to be displayed
     */
    private Pane setPanoramaLabel() {
        Pane labelsPane = new Pane();
        labelsPane.prefHeightProperty().bind(parameters.heightProperty());
        labelsPane.prefWidthProperty().bind(parameters.widthProperty());
        Bindings.bindContent(labelsPane.getChildren(), computer.getLabels());
        labelsPane.setMouseTransparent(true);

        return labelsPane;
    }

    /**
     * Sets a notice to appear when the panorama needs to be updated. 
     * The panorama is recalculated when one clicks on the notice.
     * 
     * @param updateNotice 
     *                  the Pane that needs to appear 
     */
    private void setUpdateNotice(StackPane updateNotice) {

        Background backG = new Background(new BackgroundFill(
                BACKGROUND_COLOR_OF_NOTICE, CornerRadii.EMPTY, Insets.EMPTY));
        updateNotice.setBackground(backG);

        updateNotice.visibleProperty().bind(parameters.parametersProperty()
                .isNotEqualTo(computer.parametersProperty()));
        updateNotice.setOnMouseClicked(value -> {
            computer.setParameters(parameters.parametersProperty().get());
        });

    }

    /**
     *  Assembles the bottom panes for our application
     * 
     * @param parametersArea
     *                  the box containing all the parameters information at the mouses location
     * @return the settled bottom pane
     */
    private GridPane bottomParametersGrid(TextArea parametersArea) {

        LabeledListStringConverter LLSConverter = new LabeledListStringConverter(
                "non", "2x", "4x");
        FixedPointStringConverter FPSConverter = new FixedPointStringConverter(
                4);
        IntegerStringConverter ISConverter = new IntegerStringConverter();

        TextFormatter<Integer> formatterLat = new TextFormatter<>(FPSConverter);
        TextFormatter<Integer> formatterLong = new TextFormatter<>(FPSConverter);
        TextFormatter<Integer> formatterAlt = new TextFormatter<>(ISConverter);
        TextFormatter<Integer> formatterAzi = new TextFormatter<>(ISConverter);
        TextFormatter<Integer> formatterHFView = new TextFormatter<>(ISConverter);
        TextFormatter<Integer> formatterMDist = new TextFormatter<>(ISConverter);
        TextFormatter<Integer> formatterW = new TextFormatter<>(ISConverter);
        TextFormatter<Integer> formatterH = new TextFormatter<>(ISConverter);

        Label latitudeArea = new Label("Latitude (°) : ");
        TextField latitudeText = setTextField(formatterLat,
                PREF_COLUMN_SIZE_BIG, OBSERVER_LATITUDE);

        Label longitudeArea = new Label("Longitude (°) : ");
        TextField longitudeText = setTextField(formatterLong,
                PREF_COLUMN_SIZE_BIG, OBSERVER_LONGITUDE);

        Label altitudeArea = new Label("Altitude (m) : ");
        TextField altitudeText = setTextField(formatterAlt,
                PREF_COLUMN_SIZE_MEDIUM, OBSERVER_ELEVATION);

        Label azimuthArea = new Label("Azimut (°) : ");
        TextField azimuthText = setTextField(formatterAzi,
                PREF_COLUMN_SIZE_SMALL, CENTER_AZIMUTH);

        Label hFViewArea = new Label("Angle de vue (°) : ");
        TextField hFViewText = setTextField(formatterHFView,
                PREF_COLUMN_SIZE_SMALL, HORIZONTAL_FIELD_OF_VIEW);

        Label maxDistanceArea = new Label("Visibilité (km) : ");
        TextField maxDistanceText = setTextField(formatterMDist,
                PREF_COLUMN_SIZE_SMALL, MAX_DISTANCE);

        Label widthArea = new Label("Largeur (px) : ");
        TextField widthText = setTextField(formatterW, 
                PREF_COLUMN_SIZE_MEDIUM, WIDTH);

        Label heightArea = new Label("Hauteur (px) : ");
        TextField heightText = setTextField(formatterH, 
                PREF_COLUMN_SIZE_MEDIUM, HEIGHT);

        Label superSamplingArea = new Label("Suréchantillonage (px) : ");

        ChoiceBox<Integer> superSamplingChoice = new ChoiceBox<>();
        superSamplingChoice.getItems().addAll(0, 1, 2);
        superSamplingChoice.setConverter(LLSConverter);
        superSamplingChoice.valueProperty()
                .bindBidirectional(parameters.superSamplingExponentProperty());

        GridPane paramsGrid = new GridPane();

        paramsGrid.setAlignment(Pos.CENTER);
        paramsGrid.setHgap(HORIZONTAL_GAP_BETWEEN_GRID_ELEMENT);
        paramsGrid.setVgap(VERTICAL_GAP_BETWEEN_GRID_ELEMENT);
        paramsGrid.setPadding(new Insets(GRIDPANE_INSETS[0], GRIDPANE_INSETS[1],
                GRIDPANE_INSETS[2], GRIDPANE_INSETS[3]));
        paramsGrid.addRow(0, latitudeArea, latitudeText, longitudeArea,
                longitudeText, altitudeArea, altitudeText);
        paramsGrid.addRow(1, azimuthArea, azimuthText, hFViewArea, hFViewText,
                maxDistanceArea, maxDistanceText);
        paramsGrid.addRow(2, widthArea, widthText, heightArea, heightText,
                superSamplingArea, superSamplingChoice);
        paramsGrid.add(parametersArea, 7, 0, 1, 3);

        for (Node n : paramsGrid.getChildren()) {
            GridPane.setHalignment(n, HPos.RIGHT);
        }

        return paramsGrid;
    }

    /**
     * Constructs an uneditable TextArea with two rows
     * 
     * @return an uneditable TextArea with two rows
     */
    private TextArea setTextArea() {
        TextArea parametersArea = new TextArea();
        parametersArea.editableProperty().set(false);
        parametersArea.prefRowCountProperty().set(PREF_ROW_NUMBER_TEXT_AREA);
        return parametersArea;
    }
    
    /**
     * Constructs a template of textfield containing a UserParameter value
     * 
     * @param textFmt 
     *          used to format the UserParameter information
     * @param prefCol 
     *          the preffered number of columns
     * @param up 
     *          the UserParameter
     * @return a template of textfield
     */

    private TextField setTextField(TextFormatter<Integer> textFmt, int prefCol,
            UserParameter up) {
        TextField pupText = new TextField();
        pupText.setTextFormatter(textFmt);
        pupText.setAlignment(Pos.CENTER_RIGHT);
        pupText.setPrefColumnCount(prefCol);
        textFmt.valueProperty().bindBidirectional(parameters.get(up));

        return pupText;
    }

    
    /**
     * Prepares the access to OpenStreetMap at the real-life position of the index of the mouse.
     * 
     * @param value
     *              the MouseEvent for which this happens
     */
    private void pointToWeb(MouseEvent value) {

        int x = getDisplayedIndex(value.getX());
        int y = getDisplayedIndex(value.getY());
        float longitude = (float) Math
                .toDegrees(computer.getPanorama().longitudeAt(x, y));
        float latitude = (float) Math
                .toDegrees(computer.getPanorama().latitudeAt(x, y));
        String query = String.format((Locale) null, "mlat=%.4f&mlon=%.4f",
                latitude, longitude);
        String fragment = String.format((Locale) null, "map=15/%.4f/%.4f",
                latitude, longitude);

        try {
            URI osmURI = new URI("http", "www.openstreetmap.org", "/", query,
                    fragment);
            java.awt.Desktop.getDesktop().browse(osmURI);
        } catch (IOException e) {
            System.out.println("Error while opening the URL");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.out.println("The URI is invalid");
            e.printStackTrace();
        }
    }

    /**
     * Prepares the display of information contained in value to a TextArea
     * 
     * @param value
     *          the MouseEvent for which this happens
     * @param parametersArea
     *          the TextArea where the information will be displayed
     */
    private void pointToInfo(MouseEvent value, TextArea parametersArea) {

        int x =  getDisplayedIndex(value.getX());
        int y =  getDisplayedIndex(value.getY());
        float latitude = (float) Math
                .toDegrees(computer.getPanorama().latitudeAt(x, y));
        float longitude = (float) Math
                .toDegrees(computer.getPanorama().longitudeAt(x, y));
        float distance = computer.getPanorama().distanceAt(x, y) / KILOMETERS_TO_METERS;
        int altitude = (int) computer.getPanorama().elevationAt(x, y);
        double azimuthRad = computer.getPanorama().parameters().azimuthForX(x);
        double azimuth = Math.toDegrees(azimuthRad);
        String octantAz = Azimuth.toOctantString(azimuthRad, "N", "E", "S",
                "W");
        double elevation = (float) Math
                .toDegrees(computer.getPanorama().parameters().altitudeForY(y));

        String latString = String.format("%.4f", Math.abs(latitude));
        String longString = String.format("%.4f", Math.abs(longitude));
        String distString = String.format("%.1f", distance);
        String aziString = String.format("%.1f", azimuth);
        String elevString = String.format("%.1f", elevation);

        StringBuilder info = new StringBuilder();
        info.append("Position : ").append(latString).append("°")
                .append(latitude > 0 ? "N  " : "S  ").append(longString)
                .append("°").append(longitude > 0 ? "E \n" : "W \n")
                .append("Distance : ").append(distString).append(" km \n")
                .append("Altitude : ").append(altitude).append(" m \n")
                .append("Azimut : ").append(aziString).append("° ").append("(")
                .append(octantAz).append(") \t").append("Elevation : ")
                .append(elevString).append("°");

        parametersArea.setText(info.toString());
    }

    // ================================= EXTENSION ==================================//

    private Menu fileMenu(Stage primaryStage) {
        Menu fileMenu = new Menu("File");

        MenuItem saveViewItem = new MenuItem("Save Panorama Image");
        saveViewItem.setOnAction(value -> saveView(primaryStage));

        MenuItem saveFileItem = new MenuItem("Save Panorama Parameters");
        saveFileItem.setOnAction(value -> saveParametersData(primaryStage));

        MenuItem loadFileItem = new MenuItem("Load Panorama Parameters");
        loadFileItem.setOnAction(value -> loadParametersData(primaryStage));

        fileMenu.getItems().setAll(saveViewItem, new SeparatorMenuItem(),
                saveFileItem, loadFileItem);

        return fileMenu;
    }

    private void saveView(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters()
                .add(new ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try {
                ImageIO.write(
                        SwingFXUtils.fromFXImage(computer.getImage(), null),
                        "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void saveParametersData(Stage primaryStage) {

        Map<String, Integer> parametersToSave = new TreeMap<>();

        parametersToSave.put("latitude", parameters.observerLatitudeProperty().get());
        parametersToSave.put("longitude", parameters.observerLongitudeProperty().get());
        parametersToSave.put("altitude", parameters.observerElevationProperty().get());
        parametersToSave.put("azimuth", parameters.centerAzimuthProperty().get());
        parametersToSave.put("hfView", parameters.horizontalFieldOfViewProperty().get());
        parametersToSave.put("maxDistance", parameters.maxDistanceProperty().get());
        parametersToSave.put("height", parameters.heightProperty().get());
        parametersToSave.put("width", parameters.widthProperty().get());
        parametersToSave.put("samplingExponant", parameters.superSamplingExponentProperty().get());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Parameters");
        fileChooser.getExtensionFilters()
                .add(new ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File fileLocation = fileChooser.showSaveDialog(primaryStage);

        try (PrintStream file = new PrintStream(fileLocation)) {
            for (Map.Entry<String, Integer> parameter : parametersToSave
                    .entrySet()) {
                file.println(parameter.getKey() + ":" + parameter.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadParametersData(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Parameters");
        File fileToRead = fileChooser.showOpenDialog(primaryStage);

        if (fileToRead != null) {
            Map<String, Integer> parametersLoaded = new TreeMap<>();
            try (BufferedReader i = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileToRead), StandardCharsets.UTF_8))) {
                String param;
                while ((param = i.readLine()) != null) {
                    Map.Entry<String, Integer> e = getParametersValueFromFile(param);
                    parametersLoaded.put(e.getKey(), e.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (IllegalFormatException e) {
                System.err.println(
                        "The file used to load parameters has not the right format"
                                + "please only load files that have been saved using Alpano !");
                return;    
            }

            setParamFromPredefined(
                    new PanoramaUserParameters(parametersLoaded.get("longitude"),
                            parametersLoaded.get("latitude"), parametersLoaded.get("altitude"),
                            parametersLoaded.get("azimuth"), parametersLoaded.get("hfView"),
                            parametersLoaded.get("maxDistance"), parametersLoaded.get("width"),
                            parametersLoaded.get("height"), parametersLoaded.get("samplingExponant")));
        }

    }

    private Map.Entry<String, Integer> getParametersValueFromFile(String param) {

        String[] paramSplitted = param.split(":");
        String name = paramSplitted[0];
        Integer parameter = Integer.parseInt(paramSplitted[1]);

        return new AbstractMap.SimpleEntry<String, Integer>(name, parameter);
    }

    private Menu toolMenu() {
        Menu toolMenu = new Menu("Outils");

        MenuItem stepOnTheGround = new MenuItem("Get me out of the ground");
        stepOnTheGround.setOnAction(value -> stepOnTheGround());

        toolMenu.getItems().setAll(stepOnTheGround);

        return toolMenu;
    }

    private void stepOnTheGround() {

        double longitude = Math.toRadians(
                parameters.observerLongitudeProperty().get() / INTEGER_TO_LONGITUDE);
        double latitude = Math.toRadians(
                parameters.observerLatitudeProperty().get() / INTEGER_TO_LATITUDE);
        int altitudeToGround = (int) cem
                .elevationAt(new GeoPoint(longitude, latitude));

        parameters.observerElevationProperty().setValue(altitudeToGround + ALTITUDE_MARGIN_FROM_GROUND);
    }

    private Menu predefinedPanoramaMenu() {
        Menu predefinedPanoramaMenu = new Menu("Predefined Panoramas");

        MenuItem niesenItem = new MenuItem("Niesen");
        niesenItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.Niesen);
        });

        MenuItem alpsJuraItem = new MenuItem("Alpes Du Jura");
        alpsJuraItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.AlpesDuJura);
        });

        MenuItem montRacineItem = new MenuItem("Mont Racine");
        montRacineItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.MontRacine);
        });

        MenuItem finsterItem = new MenuItem("Finsteraarhorn");
        finsterItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.Finsteraarhorn);
        });

        MenuItem tourSauvabelinItem = new MenuItem("Tour De Sauvabelin");
        tourSauvabelinItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.TourDeSauvabelin);
        });

        MenuItem plagePelicanItem = new MenuItem("Plage Du Pelican");
        plagePelicanItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.PlageDuPelican);
        });

        Menu devViewItem = new Menu("Developers' home View");
        MenuItem nielsNeuchatelItem = new MenuItem("Niels' home (Neuchâtel)");
        nielsNeuchatelItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.NielsNeuchatel);
        });
        MenuItem ghaliLausanneItem = new MenuItem("Ghali's home (Lausanne)");
        ghaliLausanneItem.setOnAction(value -> {
            setParamFromPredefined(PredefinedPanoramas.GhaliLausanne);
        });
        devViewItem.getItems().setAll(nielsNeuchatelItem, ghaliLausanneItem);

        predefinedPanoramaMenu.getItems().setAll(niesenItem, alpsJuraItem,
                montRacineItem, finsterItem, tourSauvabelinItem,
                plagePelicanItem, new SeparatorMenuItem(), devViewItem);

        return predefinedPanoramaMenu;
    }

    private void setParamFromPredefined(PanoramaUserParameters predef) {
        parameters.observerLatitudeProperty().setValue(predef.observerLatitude());
        parameters.observerLongitudeProperty().setValue(predef.observerLongitude());
        parameters.observerElevationProperty().setValue(predef.observerElevation());
        parameters.centerAzimuthProperty().setValue(predef.centerAzimuth());
        parameters.horizontalFieldOfViewProperty().setValue(predef.horizontalFieldOfView());
        parameters.maxDistanceProperty().setValue(predef.maxDistance());
        parameters.heightProperty().setValue(predef.height());
        parameters.widthProperty().setValue(predef.width());
        parameters.superSamplingExponentProperty().setValue(predef.superSamplingExponent());
    }

    private Menu helpMenu(Stage primaryStage) {
        Menu helpMenu = new Menu("Help");

        MenuItem infoItem = new MenuItem("How to use Alpano ?"); 
                                                                
        infoItem.setOnAction(value -> {
            File howTo = new File("README.txt");
            openFile(howTo);
        });

        MenuItem sourceItem = new MenuItem("Source");
        sourceItem.setOnAction(value -> {
            File source = new File("Sources.txt");
            openFile(source);
        });

        helpMenu.getItems().setAll(infoItem, sourceItem);

        return helpMenu;
    }

    private void openFile(File file) {
        file.setReadOnly();
        try {
            Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
