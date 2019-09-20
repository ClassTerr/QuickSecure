package QuickSecure;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Controller to MainForm.fxml
 */
public class MainFormController implements Initializable {

	/**
	 * Represents encryption form pane
	 */
	public static AnchorPane EncryptionForm = null;

	/**
	 * Represents decryption form pane
	 */
	public static AnchorPane DecryptionForm = null;

	/**
	 * Represents about form pane
	 */
	public static AnchorPane AboutForm = null;

	/**
	 * Represents help form pane
	 */
	public static AnchorPane HelpForm = null;

	/**
	 * Represents left-side pane which render menu
	 */
	public static JFXDrawer Drawer = null;

	/**
	 * Represents animation for three-line menu
	 */
	public static HamburgerBackArrowBasicTransition HamburgerTransition = null;

	@FXML
	private JFXDrawer drawer;

	@FXML
	private JFXHamburger hamburger;

	@FXML
	private AnchorPane root;

	@FXML
	private AnchorPane aboutPane;

	@FXML
	private AnchorPane decryptionPane;

	@FXML
	private AnchorPane encryptionPane;

	@FXML
	private AnchorPane helpPane;

	@FXML
	private AnchorPane settingsPane;

	/**
	 * Initialize method that called after fxml loaded
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		EncryptionForm = encryptionPane;
		DecryptionForm = decryptionPane;
		HelpForm = helpPane;
		AboutForm = aboutPane;
		Drawer = drawer;

		try {
			Node menu = FXMLLoader.load(getClass().getResource("Menu.fxml"));

			drawer.setSidePane(menu);
		} catch (IOException ex) {
			Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
		}

		HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
		HamburgerTransition = transition;
		transition.setRate(-1);
		hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
			transition.setRate(drawer.isShown() ? -1 : 1);
			transition.play();

			if (drawer.isShown()) {
				drawer.close();
			} else {
				drawer.open();
			}
		});
	}
}
