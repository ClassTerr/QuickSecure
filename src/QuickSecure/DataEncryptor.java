package QuickSecure;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * The DataEncryptor class is used to inject data into image
 */
public class DataEncryptor {

	public Image image;
	int lastBitsPerChannel = 2;
	int blockWidth = lastBitsPerChannel * 3;

	public DataEncryptor(Image image) {
		this.image = image;
	}

	/**
	 * Initializes DataEncryptor with parameter lastBitsPerChannel
	 * 
	 * @param lastBitsPerChannel
	 *            The number of last bits per channel into which information
	 *            will be encrypted
	 */
	public DataEncryptor(int lastBitsPerChannel) {
		this.lastBitsPerChannel = lastBitsPerChannel;
	}

	/**
	 * Method that inject data to image
	 * 
	 * @param data
	 *            Data from image
	 * @return Image with encoded data
	 */
	public WritableImage encode(byte[] data) {
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		int pointer = 0; // position in data array
		int bitsAmount = 0; // bits of information availiable in buffer
		long buffer = 0;

		WritableImage result = new WritableImage(w, h);
		PixelWriter pixelWriter = result.getPixelWriter();
		PixelReader pixelReader = image.getPixelReader();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pixelWriter.setArgb(i, j, pixelReader.getArgb(i, j));
			}
		}

		// add 4 bits to array
		byte[] dat1 = new byte[data.length + 4];
		System.arraycopy(data, 0, dat1, 4, data.length);
		data = dat1;

		// set data length to the top of array
		data[3] = (byte) ((data.length - 4) & 0xFF);
		data[2] = (byte) (((data.length - 4) & 0xFF00) >> 8);
		data[1] = (byte) (((data.length - 4) & 0xFF0000) >> 16);
		data[0] = (byte) (((data.length - 4) & 0xFF000000) >> 24);

		// write data about count of last bits with encrypted information to
		// first pixel
		int sz = lastBitsPerChannel - 1;
		Color c = pixelReader.getColor(0, 0);
		int[] res = new int[3];
		res[0] = (((int) (c.getRed() * 255)) & 0xFE) + sz / 4;
		res[1] = (((int) (c.getGreen() * 255)) & 0xFE) + sz / 2 % 2;
		res[2] = (((int) (c.getBlue() * 255)) & 0xFE) + sz % 2;
		pixelWriter.setColor(0, 0,
				new Color(res[0] / 255.0, res[1] / 255.0, res[2] / 255.0, Math.max(1.0 / 255, c.getOpacity())));

		// encode information
		for (int i = 1; i < w * h && (pointer < data.length || bitsAmount > 0); i++) {

			c = pixelReader.getColor(i % w, i / w);
			res[0] = (int) (c.getRed() * 255);
			res[1] = (int) (c.getGreen() * 255);
			res[2] = (int) (c.getBlue() * 255);

			// clear last bits
			res[0] = (res[0] >> lastBitsPerChannel) << lastBitsPerChannel;
			res[1] = (res[1] >> lastBitsPerChannel) << lastBitsPerChannel;
			res[2] = (res[2] >> lastBitsPerChannel) << lastBitsPerChannel;

			// get next bits until information buffer not enough to write one
			// pixel
			while (bitsAmount < lastBitsPerChannel * 3) {
				bitsAmount += 8;
				buffer <<= 8;
				if (pointer < data.length)
					buffer += Fun.NormailzeByte(data[pointer++]);
			}

			// get information from buffer and fill r, g, b channels with
			// information
			for (int j = 0; j < 3; j++) {
				res[j] += getFirstNBits(buffer, lastBitsPerChannel, bitsAmount);
				buffer = removeFirstNBits(buffer, lastBitsPerChannel, bitsAmount);
				bitsAmount = Math.max(0, bitsAmount - lastBitsPerChannel);
			}

			pixelWriter.setColor(i % w, i / w, new Color(res[0] / 255.0, res[1] / 255.0, res[2] / 255.0, 1));
		}
		return result;
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
	private long getFirstNBits(long buffer, int n, int bitsAmount) {
		if (bitsAmount < n)
			bitsAmount = n;
		return buffer >> (bitsAmount - n);
	}

	/**
	 * Gets image that used as container for data
	 * 
	 * @return Image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Get number of last bits per channel into which information will be
	 * encrypted
	 * 
	 * @return Last bits per channel count
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
	private long removeFirstNBits(long buffer, int n, int bitsAmount) {
		if (bitsAmount < n)
			bitsAmount = n;
		return buffer % (1 << (bitsAmount - n));
	}

	/**
	 * Set image that used as container for data
	 * 
	 * @param image
	 *            Image
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Set number of last bits per channel into which information will be
	 * encrypted
	 * 
	 * @param lastBitsPerChannel
	 *            Last bits per channel count
	 */
	public void setLastBitsPerChannel(int lastBitsPerChannel) {
		this.lastBitsPerChannel = lastBitsPerChannel;
		blockWidth = lastBitsPerChannel * 3;
	}
}
