package QuickSecure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class of application
 */
public class MainClass extends Application {

	/**
	 * Main method of the application
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Method called after launch was invoked that is used to initialize main
	 * form
	 */
	@Override
	public void start(Stage stage) throws Exception {
		AnchorPane root = new AnchorPane();
		Pane menu = FXMLLoader.load(getClass().getResource("MainForm.fxml"));

		AnchorPane.setTopAnchor(menu, (double) 0);
		AnchorPane.setBottomAnchor(menu, (double) 0);
		AnchorPane.setLeftAnchor(menu, (double) 0);
		AnchorPane.setRightAnchor(menu, (double) 0);

		root.getChildren().addAll(menu);
		root.setMinWidth(menu.getMinWidth());
		root.setMinHeight(menu.getMinHeight());
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("QuickSecure");
		stage.getIcons().addAll(

				new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("WindowIcons/app16.png")),
				new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("WindowIcons/app20.png")),
				new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("WindowIcons/app24.png")),
				new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("WindowIcons/app48.png")),
				new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("WindowIcons/app64.png")),
				new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("WindowIcons/app256.png")));
		stage.setMinWidth(menu.getMinWidth());
		stage.setMinHeight(menu.getMinHeight());
		stage.show();

		// testAlgo();

	}

	/**
	 * Unused method that used for testing algo/ Don`t use.
	 * 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unused")
	private void testAlgo() throws FileNotFoundException {
		Random r = new Random();
		r.setSeed(0);
		boolean error = false;

		DataEncryptor e = new DataEncryptor(
				new Image(new FileInputStream("C:\\Users\\ClassTerr\\Desktop\\Новый точечный рисунок.bmp")));
		for (int i = 0; i < 1000; i++) {

			int size = r.nextInt(1000);

			byte[] data = new byte[size];
			r.nextBytes(data);

			e.setLastBitsPerChannel(r.nextInt(7) + 1);
			Image im = e.encode(data);

			DataDecryptor d = new DataDecryptor(im);
			byte[] result = d.decode();

			if (!Arrays.equals(data, result)) {
				System.out.println("Error in " + i + " iteration!");
				error = true;
			}
		}

		if (!error)
			System.out.println("Success! You are not govnocoder!");
		else
			System.out.println("OMG! You are govnocoder!");
	}

}
