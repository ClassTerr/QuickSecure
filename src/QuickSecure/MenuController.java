package QuickSecure;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Controller of menu class
 */
public class MenuController implements Initializable {

	@FXML
	private ImageView image;

	private Pane frontElement = null;

	/**
	 * handles about menu button clicked
	 * 
	 * @param event
	 */
	@FXML
	private void about(ActionEvent event) {
		ShowElement(MainFormController.AboutForm);
		hideMenu();
	}

	/**
	 * handles decode menu button clicked
	 * 
	 * @param event
	 */
	@FXML
	private void decode(ActionEvent event) {
		ShowElement(MainFormController.DecryptionForm);
		hideMenu();
	}

	/**
	 * handles encode menu button clicked
	 * 
	 * @param event
	 */
	@FXML
	private void encode(ActionEvent event) {
		ShowElement(MainFormController.EncryptionForm);
		hideMenu();
	}

	@FXML
	private void exit(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * handles help menu button clicked
	 * 
	 * @param event
	 */
	@FXML
	private void help(ActionEvent event) {
		ShowElement(MainFormController.HelpForm);
		hideMenu();
	}

	/**
	 * Hides menu
	 */
	private void hideMenu() {
		try {
			MainFormController.HamburgerTransition.setRate(MainFormController.Drawer.isShown() ? -1 : 1);
			MainFormController.HamburgerTransition.play();
			MainFormController.Drawer.close();
		} catch (Exception e) {
		}
	}

	/**
	 * initialize method
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		frontElement = MainFormController.EncryptionForm;
		image.setImage(new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("Background.jpg")));
	}

	/**
	 * Shows pane relevant to choosen menu item. Uses for switching between
	 * panels
	 * 
	 * @param pane
	 *            Pane to show
	 */
	public void ShowElement(Pane pane) {
		if (pane == frontElement || pane == null)
			return;
		pane.setOpacity(0);
		Fun.CreateTimeline(pane.opacityProperty(), 1, 500, Interpolator.EASE_OUT);
		pane.toFront();
		frontElement = pane;
	}

}
