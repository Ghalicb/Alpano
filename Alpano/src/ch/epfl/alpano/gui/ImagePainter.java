package ch.epfl.alpano.gui;

/**
 * A functional interface representing an image painter
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import javafx.scene.paint.Color;

@FunctionalInterface
public interface ImagePainter {

	/**
	 * Gives the color of a certain point in an image
	 * 
	 * @param x
	 *            the x-axis coordinate of the point
	 * @param y
	 *            the y-axis coordinate of the point
	 * @return the color of the image at (x,y)
	 */
	abstract Color colorAt(int x, int y);

	/**
	 * Creates an image painter
	 * 
	 * @param hue
	 *            the hue of the image painter
	 * @param saturation
	 *            the saturation of the image painter
	 * @param brightness
	 *            the brightness of the image painter
	 * @param opacity
	 *            the opacity of the image painter
	 * @return an image painter that colors an image based on its hue,
	 *         saturation, brightness and opacity
	 */
	static ImagePainter hsb(ChannelPainter hue, ChannelPainter saturation, ChannelPainter brightness,
			ChannelPainter opacity) {
		return (x, y) -> Color.hsb(hue.valueAt(x, y), saturation.valueAt(x, y), brightness.valueAt(x, y),
				opacity.valueAt(x, y));
	}

	/**
	 * Creates a image painter
	 * 
	 * @param grayHue the grey hue of the image painter
	 * @param opacity the opacity of the image painter
	 * @return an image painter that colors an image based on its grey hue and opacity
	 */
	static ImagePainter gray(ChannelPainter grayHue, ChannelPainter opacity) {
		return (x, y) -> Color.gray(grayHue.valueAt(x, y), opacity.valueAt(x, y));
	}

}
