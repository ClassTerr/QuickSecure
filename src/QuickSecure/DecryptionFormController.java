package QuickSecure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTabPane;

import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * Controller of DecryptionFormFXML
 */
public class DecryptionFormController {
	private final String CLIPBOARD_TEXT = "Из буфера обмена";
	private byte[] rawData = new byte[0];

	// controls
	@FXML
	private ResourceBundle resources;

	@FXML
	private TextField imageFilenameTextField;

	@FXML
	private JFXTabPane dataTab;

	@FXML
	private Label loadImageFailedLabel;

	@FXML
	private Pane dropPane;

	@FXML
	private Pane dataAsImagePane;

	@FXML
	private TextArea dataTextField;

	@FXML
	private AnchorPane strokePane;

	@FXML
	private JFXButton copyButton;

	@FXML
	private ImageView imgView;

	@FXML
	private AnchorPane filenamePane;

	@FXML
	private AnchorPane anchPane;

	@FXML
	private JFXButton decodeButton;

	@FXML
	private ImageView upDownArrow;

	@FXML
	private ImageView upDownArrowBottomPart;

	@FXML
	private ImageView imageFromData;

	@FXML
	private JFXButton saveButton;

	@FXML
	private Rectangle dropRectangle;

	@FXML
	private Pane centeringPane;

	private FileChooser openTxt = new FileChooser();
	private FileChooser openImg = new FileChooser();
	private FileChooser saveImg = new FileChooser();

	void CenterPane(Pane pane, double width, double height) {
		pane.setLayoutX((width - pane.getPrefWidth()) / 2);
		pane.setLayoutY((height - pane.getPrefHeight()) / 2);
	}

	/**
	 * Copy result data to clipboard
	 */
	@FXML
	void copyToClipboard() {

		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		if (dataTab.getSelectionModel().getSelectedIndex() == 0)
			content.putString(dataTextField.getText());
		else if (dataTab.getSelectionModel().getSelectedIndex() == 1)
			if (imageFromData.getImage() == null) {
				if (rawData != null)
					content.put(DataFormat.PLAIN_TEXT, Fun.DataToString(rawData));
			} else
				content.putImage(imageFromData.getImage());

		clipboard.setContent(content);
	}

	/**
	 * Handles decode button
	 */
	@FXML
	void decode() {
		if (imgView.getImage() != null) {
			DataDecryptor dataDecryptor = new DataDecryptor(imgView.getImage());
			byte[] raw = dataDecryptor.decode();
			try {

				String s = new String(raw, "UTF-8");
				// textArea is so bad control that slowly render a lot of text
				if (s.length() > Fun.textAreaMaxLength)
					s = s.substring(0, Fun.textAreaMaxLength - 3) + "...";

				if (Fun.isValidString(s))
					dataTextField.setText(s);
			} catch (UnsupportedEncodingException e) {

			}

			Image img = Fun.GetImageFromByteArray(raw);
			if (img != null && !img.isError()) {
				imageFromData.setImage(img);
				loadImageFailedLabel.setVisible(false);
			} else {
				imageFromData.setImage(null);
				loadImageFailedLabel.setVisible(true);
			}
			rawData = raw;
		} else {
			Fun.Alert("Пожалуйста, укажите исходное изображение!", "Ошибка", AlertType.INFORMATION);
		}
	}

	/**
	 * Handles drag drop to pane with image
	 * 
	 * @param event
	 */
	@FXML
	void dragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasImage()) {
			imageFilenameTextField.setText("");
			imgView.setImage(db.getImage());
		} else if (db.hasFiles()) {
			imageFilenameTextField.setText("");
			imageFilenameTextField.setText(db.getFiles().get(0).getAbsolutePath());
		}
	}

	/**
	 * Handles drag drop to dataFilenameTextField
	 * 
	 * @param event
	 */
	@FXML
	void dragEntered(DragEvent event) {
		Fun.CreateTimeline(strokePane.opacityProperty(), 1, 250);
		FillTransition ftr = new FillTransition(Duration.millis(250), dropRectangle);
		ftr.setFromValue(Color.TRANSPARENT);
		ftr.setToValue(Color.rgb(32, 192, 32, 0.2));
		ftr.setCycleCount(1);
		ftr.play();
	}

	/**
	 * Handles drag exit from image pane When it needed download animation and
	 * dashed line will be hidden
	 * 
	 * @param event
	 */
	@FXML
	void dragExited(DragEvent event) {
		if (imgView.getImage() != null)
			Fun.CreateTimeline(strokePane.opacityProperty(), 0, 250);

		FillTransition ftr = new FillTransition(Duration.millis(250), dropRectangle);
		ftr.setFromValue(Color.rgb(32, 192, 32, 0.2));
		ftr.setToValue(Color.TRANSPARENT);
		ftr.setCycleCount(1);
		ftr.play();
	}

	/**
	 * Handles drag over dataTextField
	 * 
	 * @param event
	 */
	@FXML
	void dragOver(DragEvent event) {
		if (event.getDragboard().hasFiles() || event.getDragboard().hasImage()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
		event.consume();
	}

	/**
	 * Initialize method that called after fxml loaded
	 */
	@FXML
	void initialize() {
		upDownArrowBottomPart
				.setImage(new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("DropContainer.png")));
		upDownArrow.setImage(new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("DropArrow.png")));
		JFXRippler rip = new JFXRippler(dropPane);
		AnchorPane.setBottomAnchor(rip, 0.0);
		AnchorPane.setTopAnchor(rip, 0.0);
		AnchorPane.setLeftAnchor(rip, 0.0);
		AnchorPane.setRightAnchor(rip, 0.0);

		rip.setOnDragDropped((e) -> {
			dragDropped(e);
		});
		rip.setOnDragEntered((e) -> {
			dragEntered(e);
		});
		rip.setOnDragOver((e) -> {
			dragOver(e);
		});
		rip.setOnDragExited((e) -> {
			dragExited(e);
		});
		rip.setOnMouseReleased((e) -> {
			if (imgView.getImage() != null)
				Fun.CreateTimeline(strokePane.opacityProperty(), 0, 250);
		});
		rip.setOnMousePressed((e) -> {
			Fun.CreateTimeline(strokePane.opacityProperty(), 1, 250);
		});
		anchPane.getChildren().add(rip);

		dropRectangle.widthProperty().bind(strokePane.widthProperty());
		dropRectangle.heightProperty().bind(strokePane.heightProperty());

		openImg.setInitialDirectory(new File(System.getProperty("user.home")));
		saveImg.setInitialDirectory(new File(System.getProperty("user.home")));

		openTxt.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Любой файл", "*.*"),
				new FileChooser.ExtensionFilter("Любой тип", "*.*"));

		openImg.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Изображения", "*.png", "*.bmp", "*.gif", "*.jpg", "*.jpeg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("BMP", "*.bmp"),
				new FileChooser.ExtensionFilter("GIF", "*.gif"),
				new FileChooser.ExtensionFilter("JPG (Не рекомендуется)", "*.jpg", "*.jpeg"),
				new FileChooser.ExtensionFilter("Любой тип", "*.*"));

		saveImg.getExtensionFilters().add(new FileChooser.ExtensionFilter("Тип без потерь", "*.png", "*.bmp"));

		imageFilenameTextField.textProperty().addListener((obs, oldV, newV) -> {
			FileInputStream fis = null;
			try {
				if (newV.equals(CLIPBOARD_TEXT)) {
					Clipboard cl = Clipboard.getSystemClipboard();
					if (cl.hasImage()) {
						imgView.setImage(cl.getImage());
						imageFilenameTextField.setStyle("");
					} else
						imageFilenameTextField.setStyle("-fx-background-color:eeee22");
					return;
				}

				fis = new FileInputStream(imageFilenameTextField.getText());
				Image img = new Image(fis);

				if (img.isError())
					throw new Exception();

				imgView.setImage(img);
				imageFilenameTextField.setStyle("");

			} catch (Exception e) {
				if (newV.isEmpty())
					imageFilenameTextField.setStyle("");
				else
					imageFilenameTextField.setStyle("-fx-background-color:dd3333ff");
				imgView.setImage(null);
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e1) {
					}
			}
		});

		anchPane.widthProperty().addListener((obs, oldV, newV) -> {
			CenterPane(centeringPane, (double) newV, anchPane.getHeight());
		});

		anchPane.heightProperty().addListener((obs, oldV, newV) -> {
			CenterPane(centeringPane, anchPane.getWidth(), (double) newV);
		});

		anchPane.widthProperty().addListener((obs, oldV, newV) -> {
			Fun.LayoutImage(imgView, (Region) anchPane);
		});

		anchPane.heightProperty().addListener((obs, oldV, newV) -> {
			Fun.LayoutImage(imgView, (Region) anchPane);
		});

		dataAsImagePane.heightProperty().addListener((obs, oldV, newV) -> {
			Fun.LayoutImage(imageFromData, (Region) dataAsImagePane);
		});

		dataAsImagePane.widthProperty().addListener((obs, oldV, newV) -> {
			Fun.LayoutImage(imageFromData, (Region) dataAsImagePane);
		});

		imageFromData.imageProperty().addListener((obs, oldV, newV) -> {
			Fun.LayoutImage(imageFromData, (Region) dataAsImagePane);
		});

		imgView.imageProperty().addListener((obs, oldV, newV) -> {
			Fun.LayoutImage(imgView, (Region) anchPane, newV);

			if (imgView.getImage() == null) {
				imgView.setOpacity(0);
				Fun.CreateTimeline(strokePane.opacityProperty(), 1, 500);

			} else {
				imgView.setOpacity(0);
				Fun.CreateTimeline(imgView.opacityProperty(), 1, 1000);
				Fun.CreateTimeline(strokePane.opacityProperty(), 0, 500);
			}
		});

		Fun.CreateTimeline(upDownArrow.yProperty(), upDownArrow.getY() - 10, 1500, Timeline.INDEFINITE,
				new SoftAnimationInterpolator());
	}

	/**
	 * Load image from file or clipboard
	 * 
	 * @param ev
	 */
	@FXML
	void openImage(MouseEvent ev) {
		if (ev.getButton() == MouseButton.SECONDARY) {
			if (Clipboard.getSystemClipboard().hasImage() || imgView.getImage() == null) {
				imageFilenameTextField.setText("");
				imageFilenameTextField.setText(CLIPBOARD_TEXT);
			}
		}
		if (ev.getButton() == MouseButton.PRIMARY) {
			try {
				File file = openImg.showOpenDialog(imgView.getScene().getWindow());
				if (file != null) {
					imageFilenameTextField.setText("");
					imageFilenameTextField.setText(file.getAbsolutePath());
				}
			} catch (Exception e) {
				Fun.Alert("Ошибка загрузки изображения!", "Ошибка", AlertType.ERROR);
			}
		}
	}

	/**
	 * Handles save button
	 */
	@FXML
	void save() {
		if (dataTab.getSelectionModel().getSelectedIndex() == 1) {
			if (imageFromData.getImage() != null) {
				File f = saveImg.showSaveDialog(saveButton.getScene().getWindow());
				if (f != null) {
					String p = Fun.GetFileExtension(f.getAbsolutePath());
					try {
						ImageIO.write(SwingFXUtils.fromFXImage(imageFromData.getImage(), null), p, f);
					} catch (IOException e) {
						Fun.Alert("Не удалось сохранить изображение!", "Ошибка записи!");
					}
				}
				return;
			}
		}

		try {
			File f = openTxt.showSaveDialog(saveButton.getScene().getWindow());
			if (f != null) {
				Files.write(Paths.get(f.getAbsolutePath()), rawData);
			}
		} catch (Exception e) {
			Fun.Alert("Не удалось сохранить файл!", "Ошибка записи", AlertType.ERROR);
		}
	}
}
