package QuickSecure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTabPane;

import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * Controller of EncryptionFormFXML
 */
public class EncryptionFormController {
	private int lastBitsCount = 2;
	private final String CLIPBOARD_TEXT = "Из буфера обмена";

	// controls

	@FXML
	private TextField imageFilenameTextField;

	@FXML
	private JFXTabPane dataTab;

	@FXML
	private JFXSlider bitsCountSlider;

	@FXML
	private JFXButton openFileButton;

	@FXML
	private Pane dropPane;

	@FXML
	private TextArea dataTextField;

	@FXML
	private AnchorPane strokePane;

	@FXML
	private Label fileInfoLabel;

	@FXML
	private Label textInfoLabel;

	@FXML
	private JFXButton copyButton;

	@FXML
	private TextField dataFilenameTextField;

	@FXML
	private AnchorPane filenamePane;

	@FXML
	private AnchorPane dropzonePane;

	@FXML
	private AnchorPane anchPane1;

	@FXML
	private JFXButton encodeButton;

	@FXML
	private AnchorPane filenamePane1;

	@FXML
	private ImageView upDownArrow;

	@FXML
	private ImageView upDownArrowBottomPart;

	@FXML
	private ImageView sourceImage;

	@FXML
	private JFXButton saveButton;

	@FXML
	private GridPane imagesGridPane;

	@FXML
	private Rectangle dropRectangle;

	@FXML
	private AnchorPane anchPane2;

	@FXML
	private ImageView resultImage;

	@FXML
	private Pane centeringPane;

	@FXML
	private Label imageInfoLabel;

	private FileChooser openTxt = new FileChooser();
	private FileChooser openImg = new FileChooser();
	private FileChooser saveImg = new FileChooser();

	/**
	 * Layout pane to be centered in bounds with specified size
	 * 
	 * @param pane
	 *            pane that must be in center
	 * @param width
	 * @param height
	 */
	void CenterPane(Pane pane, double width, double height) {
		pane.setLayoutX((width - pane.getPrefWidth()) / 2);
		pane.setLayoutY((height - pane.getPrefHeight()) / 2);
	}

	/**
	 * Copy result image with encoded info to clipboard
	 */
	@FXML
	void copyToClipboard() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putImage(resultImage.getImage());
		clipboard.setContent(content);
	}

	/**
	 * Handles file drop to dataFilenameTextField
	 * 
	 * @param event
	 */
	@FXML
	void dataFilenameDragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasFiles())
			dataFilenameTextField.setText((db.getFiles().get(0).getAbsolutePath()));
	}

	/**
	 * Accepts drag if it have files
	 * 
	 * @param event
	 */
	@FXML
	void dataFilenameDragOver(DragEvent event) {
		if (event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
		event.consume();
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
			sourceImage.setImage(db.getImage());
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
		ftr.setToValue(Color.rgb(32, 192, 32, 0.3));
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
		if (sourceImage.getImage() != null)
			Fun.CreateTimeline(strokePane.opacityProperty(), 0, 250);

		FillTransition ftr = new FillTransition(Duration.millis(250), dropRectangle);
		ftr.setFromValue(Color.rgb(32, 192, 32, 0.3));
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
	 * Handles encode button clicked Inject information to writeableImage
	 */
	@FXML
	void encode() {
		if (sourceImage.getImage() != null) {
			try {
				DataEncryptor encryptor = new DataEncryptor(sourceImage.getImage());
				encryptor.setLastBitsPerChannel(lastBitsCount);
				byte[] data = null;

				if (dataTab.getSelectionModel().getSelectedIndex() == 0) {
					data = dataTextField.getText().getBytes("UTF-8");
				} else {
					File f = new File(dataFilenameTextField.getText());
					if (f.exists() && !f.isDirectory())
						data = Files.readAllBytes(Paths.get(dataFilenameTextField.getText()));
					else {
						Fun.Alert("Пожалуйста, укажите существующий файл с данными!", "Нет данных для зашифровки!");
						return;
					}
				}

				int w = (int) sourceImage.getImage().getWidth();
				int h = (int) sourceImage.getImage().getHeight();
				int informationAmount = lastBitsCount * (w * h - 1) * 3 / 8;
				if (informationAmount < 5) {
					Fun.Alert("В это изображение ничего нельзя зашифровать!", "Ошибка", AlertType.ERROR);
					return;
				}

				if (data.length > informationAmount - 4) {
					Optional<ButtonType> res = Fun.Alert(
							"Обьём данных слишком велик для данного изображения!\n"
									+ "Хотите ли вы обрезать данные и всё равно закодировать?",
							"Предупреждение", AlertType.CONFIRMATION);
					if (res.get() == ButtonType.CANCEL)
						return;
					data = Arrays.copyOfRange(data, 0, informationAmount);
				}

				resultImage.setImage(encryptor.encode(data));
				copyButton.setDisable(false);
				saveButton.setDisable(false);
			} catch (IOException e) {
				Fun.Alert("Не удалось зашифровать указанный текст!\nОшибка ввода-вывода!", "Ошибка!");
			} catch (Exception e) {
				Fun.Alert("Не удалось зашифровать указанный текст!", "Ошибка!");
			}
		} else {
			Fun.Alert("Пожалуйста, укажите исходное изображение!", "Ошибка", AlertType.INFORMATION);
		}
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
			if (sourceImage.getImage() != null)
				Fun.CreateTimeline(strokePane.opacityProperty(), 0, 250);
		});
		rip.setOnMousePressed((e) -> {
			Fun.CreateTimeline(strokePane.opacityProperty(), 1, 250);
		});
		dropzonePane.getChildren().add(rip);
		dropRectangle.widthProperty().bind(strokePane.widthProperty());
		dropRectangle.heightProperty().bind(strokePane.heightProperty());

		bitsCountSlider.valueProperty().addListener((obs, oldval, newVal) -> {
			lastBitsCount = newVal.intValue();
			updateImageLabel();
		});
		bitsCountSlider.setOnMouseClicked((e) -> {
			lastBitsCount = (int) bitsCountSlider.getValue();
			updateImageLabel();
		});

		openTxt.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		openImg.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		saveImg.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));

		openTxt.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Любой тип", "*.*"));

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
						sourceImage.setImage(cl.getImage());
						imageFilenameTextField.setStyle("");
					} else
						imageFilenameTextField.setStyle("-fx-background-color:eeee22");
					return;
				}

				fis = new FileInputStream(imageFilenameTextField.getText());
				Image img = new Image(fis);
				if (img.isError())
					throw new Exception();

				sourceImage.setImage(img);
				imageFilenameTextField.setStyle("");

			} catch (Exception e) {
				if (newV.isEmpty() || newV.equals(CLIPBOARD_TEXT))
					imageFilenameTextField.setStyle("");
				else
					imageFilenameTextField.setStyle("-fx-background-color:dd3333ff");
				sourceImage.setImage(null);
				resultImage.setImage(null);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e1) {
					}
				}
			}
		});

		dataFilenameTextField.textProperty().addListener((obs, oldV, newV) -> {
			openDataFile(newV);
		});

		dataTextField.textProperty().addListener((obs, oldV, newV) -> {
			updateTextLabel();
		});

		dropzonePane.widthProperty().addListener((obs, oldV, newV) -> {
			CenterPane(centeringPane, (double) newV, dropzonePane.getHeight());
		});

		dropzonePane.heightProperty().addListener((obs, oldV, newV) -> {
			CenterPane(centeringPane, dropzonePane.getWidth(), (double) newV);
		});

		anchPane1.widthProperty().addListener((observable, oldValue, newValue) -> {
			Fun.LayoutImage(sourceImage, (Region) anchPane1);
		});

		anchPane1.heightProperty().addListener((observable, oldValue, newValue) -> {
			Fun.LayoutImage(sourceImage, (Region) anchPane1);
		});

		anchPane2.widthProperty().addListener((observable, oldValue, newValue) -> {
			Fun.LayoutImage(resultImage, (Region) anchPane2);
		});

		anchPane2.heightProperty().addListener((observable, oldValue, newValue) -> {
			Fun.LayoutImage(resultImage, (Region) anchPane2);
		});

		sourceImage.imageProperty().addListener((observable, oldValue, newValue) -> {
			Fun.LayoutImage(sourceImage, (Region) anchPane1, newValue);
			resultImage.setImage(null);

			if (sourceImage.getImage() == null) {
				sourceImage.setOpacity(0);

				Fun.CreateTimeline(strokePane.opacityProperty(), 1, 500);
				imageInfoLabel.setText("");

			} else {
				sourceImage.setOpacity(0);
				Fun.CreateTimeline(sourceImage.opacityProperty(), 1, 1000);
				Fun.CreateTimeline(strokePane.opacityProperty(), 0, 500);
				updateImageLabel();
			}
		});

		resultImage.imageProperty().addListener((observable, oldValue, newValue) -> {
			Fun.LayoutImage(resultImage, (Region) anchPane2, newValue);

			ColumnConstraints col = imagesGridPane.getColumnConstraints().get(0);

			if (resultImage.getImage() == null) {
				copyButton.setDisable(true);
				saveButton.setDisable(true);
				Fun.CreateTimeline(col.percentWidthProperty(), 100, 500, new FastAnimationInterpolator());
				Fun.CreateTimeline(resultImage.opacityProperty(), 0, 500);
			} else {
				copyButton.setDisable(false);
				saveButton.setDisable(false);
				Fun.CreateTimeline(col.percentWidthProperty(), 50, 500, new FastAnimationInterpolator());
				Fun.CreateTimeline(resultImage.opacityProperty(), 1, 500);
			}

			// resultImage.setVisible(resultImage.getImage() != null);
		});

		// CenterPane(centeringPane, anchPane1.getPrefWidth(),
		// anchPane1.getPrefHeight());

		Fun.CreateTimeline(upDownArrow.yProperty(), upDownArrow.getY() - 10, 1500, Timeline.INDEFINITE,
				new SoftAnimationInterpolator());
	}

	/**
	 * Show open dialog and if ok clicked opens data file to encode as byte
	 * array
	 */
	@FXML
	void openDataFile() {
		File file = openTxt.showOpenDialog(sourceImage.getScene().getWindow());
		if (file != null)
			openDataFile(file.getAbsolutePath());
	}

	/**
	 * Open file as raw data (byte array)
	 * 
	 * @param filename
	 *            Name of file
	 */
	void openDataFile(String filename) {
		try {
			File f = new File(filename);
			if (!f.exists() || f.isDirectory())
				fileInfoLabel.setText("Пожалуйста, укажите существующий файл");
			else {
				fileInfoLabel.setText("Размер файла составляет " + Files.size(Paths.get(filename)) + " байт");
				dataFilenameTextField.setText(filename);
			}
		} catch (IOException e) {
			fileInfoLabel.setText("Ошибка считывания данных!");
		}
	}

	/**
	 * Opens image by changing text of imageFilenameTextField. - If user clicked
	 * the right mouse button above drag pane it try to insert data from
	 * clipboard by setting up text from CLIPBOARD_TEXT constant - if user
	 * selected a file text of imageFilenameTextField changing to path of
	 * selected file
	 * 
	 * @param ev
	 */
	@FXML
	void openImage(MouseEvent ev) {
		if (ev.getButton() == MouseButton.SECONDARY) {
			if (Clipboard.getSystemClipboard().hasImage() || sourceImage.getImage() == null) {
				imageFilenameTextField.setText("");
				imageFilenameTextField.setText(CLIPBOARD_TEXT);
			}
		}
		if (ev.getButton() == MouseButton.PRIMARY) {
			try {
				File file = openImg.showOpenDialog(sourceImage.getScene().getWindow());
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
	 * Shows dialog to open file
	 */
	@FXML
	void openTextFile() {
		File file = openTxt.showOpenDialog(sourceImage.getScene().getWindow());
		if (file != null)
			openTextFile(file.getAbsolutePath());
	}

	/**
	 * Read data from file
	 * 
	 * @param filename
	 *            Name of file
	 */
	void openTextFile(String filename) {
		byte[] data = null;
		String s = "";
		try {
			data = Files.readAllBytes(Paths.get(filename));
		} catch (IOException e) {
			Fun.Alert("Ошибка считывания данных!", "Ошибка", AlertType.ERROR);
			return;
		}
		try {
			s = new String(data, Fun.DetectEncoding(data));
		} catch (UnsupportedEncodingException e) {
			try {
				s = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
			}
		}

		if (s.length() > Fun.textAreaMaxLength)
			s = s.substring(0, Fun.textAreaMaxLength - 3) + "...";
		dataTextField.setText(s);
	}

	/**
	 * Save result image with encoded info to file
	 */
	@FXML
	void save() {
		try {
			String p = "";
			File file = null;

			do {
				file = saveImg.showSaveDialog(saveButton.getScene().getWindow());
				if (file == null)
					return;

				p = Fun.GetFileExtension(file.getAbsolutePath());

				if (!p.equals("png") && !p.equals("bmp"))
					Fun.Alert(
							"Внимание, Вы выбрали тип изображения, который не поддерживается.\n"
									+ "Пожалуйста, выберите файл с расширением \".png\" или \".bmp\"!",
							"Неподдерживаемый формат", AlertType.ERROR);
			} while (!p.equals("png") && !p.equals("bmp") && file != null);

			ImageIO.write(SwingFXUtils.fromFXImage(resultImage.getImage(), null), p, file);
		} catch (IOException e) {
			Fun.Alert("Не удалось сохранить изображение!", "Ошибка записи", AlertType.ERROR);
		}
	}

	/**
	 * Handles drag drop to dataTextField
	 * 
	 * @param event
	 */
	@FXML
	void txtDragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasString())
			dataTextField.setText(db.getString());
		else if (db.hasFiles())
			openTextFile(db.getFiles().get(0).getAbsolutePath());
	}

	/**
	 * Hanles drag above dataTextFieled
	 * 
	 * @param event
	 */
	@FXML
	void txtDragOver(DragEvent event) {
		if (event.getDragboard().hasFiles() || event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
		event.consume();
	}

	/**
	 * Refresh label when image changed
	 */
	private void updateImageLabel() {
		if (sourceImage.getImage() != null) {
			int maxInfoAmount = (int) sourceImage.getImage().getWidth() * (int) sourceImage.getImage().getHeight()
					* lastBitsCount * 3 / 8 - 4;
			if (maxInfoAmount <= 0)
				imageInfoLabel.setText("В это изображение невозможно внедрить информацию!");
			else
				imageInfoLabel.setText("В это изображение можно внедрить " + maxInfoAmount + " байт");
		} else
			imageInfoLabel.setText("");
	}

	/**
	 * Refresh data text label when text in dataTextField changed
	 */
	private void updateTextLabel() {
		try {
			textInfoLabel
					.setText("Размер текста составляет " + dataTextField.getText().getBytes("UTF-8").length + " байт");
		} catch (UnsupportedEncodingException e) {
			// never called
		}
	}
}
