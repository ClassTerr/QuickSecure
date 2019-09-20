package QuickSecure;

import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * The DataDecryptor class is used to extract encoded data from image
 */
public class DataDecryptor {
	private int lastBitsPerChannel = -1;

	private int divisor = 4;

	private int blockWidth = lastBitsPerChannel * 3;
	public Image image;

	public DataDecryptor(Image image) {
		this.image = image;
	}

	/**
	 * Method that retreive encoded data from image
	 * 
	 * @return Data from image
	 */
	public byte[] decode() {
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		Color c = image.getPixelReader().getColor(0, 0);
		int r = (int) (c.getRed() * 255);
		int g = (int) (c.getGreen() * 255);
		int b = (int) (c.getBlue() * 255);
		lastBitsPerChannel = (b & 1) + ((g & 1) << 1) + ((r & 1) << 2) + 1;
		divisor = (1 << lastBitsPerChannel);
		blockWidth = lastBitsPerChannel * 3;

		// in bits
		int informationAmount = (w * h - 1) * lastBitsPerChannel * 3 / 8 - 4;

		int pointer = 0; // position in stream
		int bitsFree = 8; // count of free bits in right-side of currentValue
		int currentValue = 0; // one byte
		byte[] result = new byte[informationAmount + 4];

		for (int i = 1; i < w * h; i++) {

			c = image.getPixelReader().getColor(i % w, i / w);
			int dataUse = 0;
			int data = getDataFromPixel(c);

			while (dataUse < blockWidth) {
				int shift = Math.min(bitsFree, blockWidth - dataUse);
				currentValue <<= shift;
				currentValue += getFirstNBits(data, shift, blockWidth - dataUse);
				data = removeFirstNBits(data, shift, blockWidth - dataUse);

				dataUse += shift;
				bitsFree -= shift;

				if (bitsFree == 0) {
					result[pointer++] = (byte) currentValue;
					currentValue = 0;
					bitsFree = 8;
				}
			}
		}

		int size = 0;

		for (int t = 0; t < 4; t++)
			size += Fun.NormailzeByte(result[3 - t]) << (t * 8);

		if (size >= 0 && size <= informationAmount)
			result = Arrays.copyOfRange(result, 4, size + 4);

		return result;
	}

	/**
	 * Extracts data from last bits of given color.
	 * 
	 * @param c
	 *            Color
	 * @return
	 */
	private int getDataFromPixel(Color c) {
		int r = (int) (c.getRed() * 255);
		int g = (int) (c.getGreen() * 255);
		int b = (int) (c.getBlue() * 255);
		int data = 0;
		data = data * divisor + r % divisor;
		data = data * divisor + g % divisor;
		data = data * divisor + b % divisor;

		return data;
	}

	/**
	 * Get first n bits from given data buffer and return it as integer number
	 * 
	 * @param data
	 *            Source buffer
	 * @param n
	 *            How many bits is needed to remove
	 * @param size
	 *            Amount of bits in buffer
	 * @return Data from first bits
	 */
	private int getFirstNBits(int data, int n, int size) {
		if (size < n)
			size = n;
		return data >> (size - n);
	}

	/**
	 * Get image with encoded data
	 * 
	 * @return Image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Identify how much last bits of each (R, G, B) channel used to encode
	 * information. Identify only after method decode was called
	 * 
	 * @return last bits per channel
	 */
	public int getLastBitsPerChannel() {
		return lastBitsPerChannel;
	}

	/**
	 * Remove first n bits from given data buffer and return cutted buffer
	 * 
	 * @param data
	 *            Source buffer
	 * @param n
	 *            How many bits is needed to remove
	 * @param size
	 *            Amount of bits in buffer
	 * @return Data with deleted first n bits
	 */
	private int removeFirstNBits(int data, int n, int size) {
		if (size < n)
			size = n;
		return data % (1 << (size - n));
	}

	/**
	 * Set image with encoded data
	 * 
	 * @param image
	 *            Image
	 */
	public void setImage(Image image) {
		this.image = image;
	}
}
