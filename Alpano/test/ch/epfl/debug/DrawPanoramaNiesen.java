package ch.epfl.debug;

/**
 * 
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.debug.DrawDEM.gray;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.gui.PanoramaUserParameters;
import ch.epfl.alpano.gui.PredefinedPanoramas;
import ch.epfl.alpano.gui.Labelizer;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;

import static ch.epfl.alpano.summit.GazetteerParser.*;

final class DrawPanoramaNiesen {

	final static File HGT_FILE1 = new File("N46E007.hgt");

	final static PanoramaUserParameters pano = PredefinedPanoramas.Niesen;

	final static PanoramaParameters PARAMS = pano.panoramaDisplayParameters();

	public static void main(String[] as) throws Exception {
		try (DiscreteElevationModel dDEM = new HgtDiscreteElevationModel(HGT_FILE1);) {
			
			ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);

//			Panorama p = new PanoramaComputer(cDEM).computePanorama(PARAMS);
//			
//			BufferedImage i =
//			        new BufferedImage(pano.width(),
//			                          pano.height(),
//			                          TYPE_INT_RGB);
//
//			      for (int x = 0; x < pano.width(); ++x) {
//			        for (int y = 0; y < pano.height(); ++y) {
//			          float d = p.distanceAt(x, y);
//			          int c = (d == Float.POSITIVE_INFINITY)
//			            ? 0x87_CE_EB
//			            : gray((d - 2_000) / 15_000);
//			          i.setRGB(x, y, c);
//			        }
//			      }
//
//			      ImageIO.write(i, "png", new File("niesenWithUserParams.png"));
			      int count = 0;
			      List<Summit> list = GazetteerParser.readSummitsFrom(new File("alps.txt")); 
			      Labelizer l = new Labelizer(cDEM, list);
			      List<Node> nodes = l.labels(PARAMS);
			      for(Node n : nodes) {
			          System.out.println(n.toString());
			          ++count;
			      }
			      System.out.println(count/2);
		}
	}
}
