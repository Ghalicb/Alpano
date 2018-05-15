package ch.epfl.debug;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import static java.lang.Math.*;
import static ch.epfl.debug.DrawDEM.*;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.gui.ChannelPainter;
import ch.epfl.alpano.gui.ImagePainter;
import ch.epfl.alpano.gui.PanoramaRenderer;
import ch.epfl.alpano.gui.PredefinedPanoramas;
import javafx.embed.swing.SwingFXUtils;

/**
 * 
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

final class DrawPanorama {
    final static File HGT_FILE1 = new File("N46E007.hgt"); // 006

    final static int IMAGE_WIDTH = 500;
    final static int IMAGE_HEIGHT = 200;

    final static double ORIGIN_LON = toRadians(7.65); // 7.65 Portalban 6.9560
    final static double ORIGIN_LAT = toRadians(46.73); // 46.73 46.9179
    final static int ELEVATION = 600; // 600 500
    final static double CENTER_AZIMUTH = toRadians(180); // 180 300
    final static double HORIZONTAL_FOV = toRadians(60); // 60
    final static int MAX_DISTANCE = 100_000;

    final static PanoramaParameters PARAMS = new PanoramaParameters(
            new GeoPoint(ORIGIN_LON, ORIGIN_LAT), ELEVATION, CENTER_AZIMUTH,
            HORIZONTAL_FOV, MAX_DISTANCE, IMAGE_WIDTH, IMAGE_HEIGHT);

    public static void main(String[] as) throws Exception {
        try (DiscreteElevationModel dDEM = new HgtDiscreteElevationModel(
                HGT_FILE1)) {
            ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
            
            //Panorama p = new PanoramaComputer(cDEM) .computePanorama(PARAMS);
             
            Panorama p = new PanoramaComputer(cDEM).computePanorama(
                    PredefinedPanoramas.Niesen.panoramaDisplayParameters());

            // Grey Image
            ChannelPainter gray = ChannelPainter.maxDistanceToNeighbors(p)
                    .sub(500).div(4500).clamped().inverted();

            ChannelPainter distance = p::distanceAt;
            ChannelPainter opacity = distance
                    .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

            ImagePainter l = ImagePainter.gray(gray, opacity);

            javafx.scene.image.Image j = PanoramaRenderer.renderPanorama(p, l);
            ImageIO.write(SwingFXUtils.fromFXImage(j, null), "png",
                    new File("niesen-profile.png"));

            // ColorImage
            ChannelPainter dis = p::distanceAt;
            ChannelPainter hue = dis.div(100000).cycling().mul(360);
            ChannelPainter sat = dis.div(200000).clamped().inverted();
            ChannelPainter s = p::slopeAt;
            ChannelPainter lum = s.mul((float) (2 / Math.PI)).inverted()
                    .mul((float) 0.7).add((float) 0.3);
            ChannelPainter opa = (x,
                    y) -> (dis.valueAt(x, y) == Double.POSITIVE_INFINITY ? 0
                            : 1);

            ImagePainter colors = ImagePainter.hsb(hue, sat, lum, opa);

            javafx.scene.image.Image k = PanoramaRenderer.renderPanorama(p,
                    colors);
            ImageIO.write(SwingFXUtils.fromFXImage(k, null), "png",
                    new File("niesen-colors.png"));

            // Teachers image
            BufferedImage i = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
                    TYPE_INT_RGB);

            for (int x = 0; x < IMAGE_WIDTH; ++x) {
                for (int y = 0; y < IMAGE_HEIGHT; ++y) {
                    float d = p.distanceAt(x, y);
                    int c = (d == Float.POSITIVE_INFINITY) ? 0x87_CE_EB
                            : gray((d - 2_000) / 15_000);
                    i.setRGB(x, y, c);
                }
            }

            ImageIO.write(i, "png", new File("niesen.png"));
        }
    }
}
