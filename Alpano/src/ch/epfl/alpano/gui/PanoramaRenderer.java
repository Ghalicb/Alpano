package ch.epfl.alpano.gui;

/**
 * Contains méthods to create the image of a panorama based on an image painter.
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import ch.epfl.alpano.Panorama;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public interface PanoramaRenderer {

	/**
	 * Creates an image based on a panorama and a painter
	 * 
	 * @param panorama the panorama this is based on
	 * @param painter the painter this uses
	 * @return an image based on the panorama and the painter
	 */
	static Image renderPanorama(Panorama panorama, ImagePainter painter){
		int width = panorama.parameters().width();
		int height = panorama.parameters().height();
		WritableImage wI = new WritableImage(width,height);
		PixelWriter pW = wI.getPixelWriter();
		
		for (int w=0; w<width;++w){
			for (int h=0;h<height;++h){
				pW.setColor(w, h, painter.colorAt(w, h));
			}
		}
		return wI;
	}
	
}
