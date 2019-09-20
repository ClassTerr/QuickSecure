package QuickSecure;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Class with static methods that implement most of common needed methods
 */
public class Fun {

	/**
	 * Maximum text size in symbols which may be placed in textArea. Text area
	 * have very slow behavior when it contains a lot of text
	 */
	public final static int textAreaMaxLength = 1000;

	/**
	 * Shows informational message to user
	 * 
	 * @param text
	 *            Text of message
	 * @param caption
	 *            Caption of new window
	 * @return Button that was pressed by user
	 */
	public static Optional<ButtonType> Alert(String text, String caption) {
		return Alert(text, caption, AlertType.INFORMATION);
	}

	/**
	 * Shows informational message to user
	 * 
	 * @param text
	 *            Text of message
	 * @param caption
	 *            Caption of new window
	 * @param type
	 *            Type of informational window
	 * @return Button that was pressed by user
	 */
	public static Optional<ButtonType> Alert(String text, String caption, AlertType type) {
		Alert a = new Alert(type);
		a.setTitle(caption);
		a.setHeaderText(null);
		a.setContentText(text);
		return a.showAndWait();
	}

	/**
	 * Makes it easey to creating animation.
	 * 
	 * @param <T>
	 *            Type of value needed to change by timeline
	 * @param prop
	 *            The property to which animation should be applied
	 * @param toVal
	 *            Destination value in the end of animation
	 * @param millis
	 *            Duration in milliseconds
	 */
	public static <T> void CreateTimeline(WritableValue<T> prop, T toVal, int millis) {
		CreateTimeline(prop, toVal, millis, Interpolator.EASE_BOTH);
	}

	/**
	 * Makes it easey to creating animation.
	 * 
	 * @param <T>
	 *            Type of value needed to change by timeline
	 * @param prop
	 *            The property to which animation should be applied
	 * @param toVal
	 *            Destination value in the end of animation
	 * @param millis
	 *            Duration of animation in milliseconds
	 * @param cycleCount
	 *            Animation cycles count
	 * @param ip
	 *            The interpolator of animation
	 */
	public static <T> void CreateTimeline(WritableValue<T> prop, T toVal, int millis, int cycleCount, Interpolator ip) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(cycleCount);
		KeyValue kv = new KeyValue(prop, toVal, ip);
		KeyFrame kf = new KeyFrame(Duration.millis(millis), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
	}

	/**
	 * Makes it easey to creating animation.
	 * 
	 * @param <T>
	 *            Type of value needed to change by timeline
	 * @param prop
	 *            The property to which animation should be applied
	 * @param toVal
	 *            Destination value in the end of animation
	 * @param millis
	 *            Duration of animation in milliseconds
	 * @param ip
	 *            The interpolator of animation
	 */
	public static <T> void CreateTimeline(WritableValue<T> prop, T toVal, int millis, Interpolator ip) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(1);
		KeyValue kv = new KeyValue(prop, toVal, ip);
		KeyFrame kf = new KeyFrame(Duration.millis(millis), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
	}

	/**
	 * Convert byte array to readable view string by representation each byte
	 * with two HEX letters
	 * 
	 * @param data
	 *            Raw data
	 * @return String representation
	 */
	public static String DataToString(byte[] data) {
		char[] chars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder sb = new StringBuilder(data.length * 3);
		for (int i = 0; i < data.length; i++) {
			sb.append(chars[Fun.NormailzeByte(data[i]) / 16 % 16]);
			sb.append(chars[Fun.NormailzeByte(data[i]) % 16]);
			if (i != data.length - 1)
				sb.append(' ');
		}

		return sb.toString();
	}

	/**
	 * Detects encoding of text thet represented as byte array
	 * 
	 * @param in
	 *            Data with encoded text
	 * @return Encoding in string representation
	 */
	public static String DetectEncoding(byte[] in) {
		CharsetDetector detector = new CharsetDetector().setText(in);
		CharsetMatch charsetMatch = detector.detect();
		if (charsetMatch == null) {
			return "UTF-8";
		}
		return charsetMatch.getName();
	}

	/**
	 * Detects encoding of text thet represented as InputStream
	 * 
	 * @param in
	 *            The InputStream with data
	 * @return Name of encoding
	 */
	public static String DetectEncoding(InputStream in) {
		CharsetDetector detector;
		CharsetMatch charsetMatch = null;
		try {
			detector = new CharsetDetector().setText(in);
			charsetMatch = detector.detect();
		} catch (IOException e) {
		}
		if (charsetMatch == null) {
			return "UTF-8";
		}
		return charsetMatch.getName();
	}

	/**
	 * Gets extension of choosen file
	 * 
	 * @param filename
	 *            Name of file
	 * @return Extension
	 */
	public static String GetFileExtension(String filename) {
		Pattern PATTERN = Pattern
				.compile("((.*\\" + File.separator + ")?(.*)(\\.(.*)))|(.*\\" + File.separator + ")?(.*)");
		Matcher m = PATTERN.matcher(filename);
		if (m.find())
			return m.group(5);
		return "";
	}

	/**
	 * Makes image from raw data
	 * 
	 * @param bytes
	 *            Raw data
	 * @return Image from data
	 */
	public static Image GetImageFromByteArray(byte[] bytes) {
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
			Image im = SwingFXUtils.toFXImage(img, null);
			if (im == null || im.isError())
				throw new Exception();
			return im;
		} catch (Exception e1) {
			return null;
		}
	}

	/**
	 * Check string is valid UTF-8 string
	 * 
	 * @param a
	 *            String that is needed to check
	 * @return If string is valid UTF-8 string - true False - otherwise
	 */
	public static boolean isValidString(String a) {
		for (int i = 0; i < a.length(); i++) {
			if (Character.isHighSurrogate(a.charAt(i))) {
				if (i < a.length() - 1 && Character.isLowSurrogate(a.charAt(i + 1))) {
					i++;
				} else {
					return false;
				}
			} else if (Character.isLowSurrogate(a.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Layout image to zoom in parent
	 * 
	 * @param imgV
	 *            ImageView to resize
	 * @param parent
	 *            Bounds to zoom in
	 */
	public static void LayoutImage(ImageView imgV, Region parent) {
		LayoutImage(imgV, parent, imgV.getImage());
	}

	/**
	 * Layout image to zoom in parent. Needed when image was updated.
	 * 
	 * @param imgV
	 *            ImageView to resize
	 * @param parent
	 *            Bounds to zoom in
	 * @param newImage
	 *            Image that will placed into imgView
	 */
	public static void LayoutImage(ImageView imgV, Region parent, Image newImage) {
		if (imgV != null && imgV.getImage() != null) {
			int padding = 10;
			double reflectionKoeff = 0.1;
			double w = 0;
			double h = 0;
			double pw = parent.getWidth() - padding * 2;
			double ph = parent.getHeight() - padding * 2;

			Image img = imgV.getImage();
			double raitoX = pw / img.getWidth();
			double raitoY = ph / img.getHeight() / (1 + reflectionKoeff);

			double reduceKoeff = Math.min(Math.min(raitoY, raitoX), 1.0);// не
																			// раст€гивать

			w = img.getWidth() * reduceKoeff;
			h = img.getHeight() * reduceKoeff;

			imgV.setFitWidth(w);
			imgV.setFitHeight(h);

			imgV.setX((pw - imgV.getFitWidth()) / 2 + padding);
			imgV.setY((ph - img.getHeight() * reduceKoeff * (1 + reflectionKoeff)) / 2 + padding);
		}
	}

	/**
	 * Becouse of java have not unsigned types in some cases is needed to
	 * restore unsigned representation of byte
	 * 
	 * @param b
	 *            Signed byte
	 * @return Byte converted to unsigned in integer variable type
	 */
	public static int NormailzeByte(byte b) {
		if (b < 0)
			return (b & 0xFF);
		else
			return b;
	}
}
