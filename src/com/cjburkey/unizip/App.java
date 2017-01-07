package com.cjburkey.unizip;

import java.io.File;
import java.util.List;
import com.cjburkey.unizip.archive.ArchiveFile;
import com.cjburkey.unizip.archive.ArchiveUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App {
	
	private Stage stage;
	private BorderPane root;
	private Scene scene;
	private VBox placeholder;
	private Label placeHolderText;
	private Label placeHolderEmpty;
	private Button placeHolderButton;
	private ListView<ArchiveFile> listView;
	private Label bottomText;
	private ToolBar bottom;
	
	private VBox menus;
	private ToolBar toolBar;
	private Button upDir;
	private MenuBar menuBar;
	private Menu file;
	private Menu edit;
	private Menu extract;
	private Menu window;
	private MenuItem fileNew;
	private MenuItem fileOpen;
	private MenuItem fileClose;
	private MenuItem editAddFile;
	private MenuItem editRemoveFile;
	private MenuItem extractAll;
	private MenuItem extractSelected;
	private MenuItem windowExit;
	
	private String currentDir;
	
	public void init(Stage stage) {
		this.stage = stage;
		
		build();
		
		Rectangle2D size = Screen.getPrimary().getVisualBounds();
		this.stage.setScene(this.scene);
		this.stage.setTitle("Unizip - " + Unizip.version);
		this.stage.setWidth(size.getWidth() / 2);
		this.stage.setHeight(size.getHeight() / 2);
		this.stage.setMinWidth(550);
		this.stage.setMinHeight(300);
		this.stage.centerOnScreen();
		this.stage.setResizable(true);
		this.stage.setOnCloseRequest(e -> Platform.exit());
		
		this.upDir.setDisable(true);
		this.root.requestFocus();
	}
	
	private void build() {
		this.root = new BorderPane();
		this.scene = new Scene(this.root);
		this.placeholder = new VBox();
		this.placeHolderText = new Label("No archive is open.  Use the file menu to open an archive, or click the button below.");
		this.placeHolderEmpty = new Label("This archive or directory is empty.");
		this.placeHolderButton = new Button("Open Archive");
		this.listView = new ListView<ArchiveFile>();
		this.bottomText = new Label();
		this.bottom = new ToolBar();
		
		setupMenus();
		
		this.bottomText.setMaxWidth(Double.MAX_VALUE);
		this.bottom.getItems().addAll(this.bottomText);
		this.bottomText.setAlignment(Pos.CENTER_RIGHT);
		this.bottomText.setTextAlignment(TextAlignment.RIGHT);
		this.bottomText.setWrapText(false);
		this.listView.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				ArchiveFile selected = this.listView.getSelectionModel().getSelectedItem();
				if(selected != null) {
					if(selected.isDirectory()) {
						refreshList(selected.getFull());
					}
				}
			}
		});
		this.placeHolderButton.setOnAction(e -> openFile());
		this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.placeHolderText.setWrapText(true);
		this.placeholder.getChildren().addAll(this.placeHolderText, this.placeHolderButton);
		this.placeholder.setPadding(new Insets(25));
		this.placeholder.setAlignment(Pos.CENTER);
		this.placeholder.setSpacing(10);
		this.listView.setPlaceholder(this.placeholder);
		this.listView.setOnDragOver(e -> {
			if(!e.getSource().equals(this.listView) && e.getDragboard().hasFiles()) {
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
		});
		this.listView.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			ArchiveUtils.addToZip(db.getFiles());
			e.setDropCompleted(db.hasFiles());
			e.consume();
		});
		this.root.setTop(this.menus);
		this.root.setCenter(this.listView);
		this.root.setBottom(this.bottom);
	}
	
	private void setupMenus() {
		this.menus = new VBox();
		this.toolBar = new ToolBar();
		this.upDir = new Button(new String(new char[] { 0x25B2 }));
		this.menuBar = new MenuBar();
		this.file = new Menu("File");
		this.edit = new Menu("Edit");
		this.extract = new Menu("Extract");
		this.window = new Menu("Window");
		
		this.fileNew = new MenuItem("New Archive");
		this.fileOpen = new MenuItem("Open Archive");
		this.fileClose = new MenuItem("Close Archive");
		this.editAddFile = new MenuItem("Put File into Archive");
		this.editRemoveFile = new MenuItem("Delete File from Archive");
		this.extractAll = new MenuItem("Extract All");
		this.extractSelected = new MenuItem("Extract Selected");
		this.windowExit = new MenuItem("Quit Program");
		
		this.fileOpen.setOnAction(e -> openFile());
		this.fileClose.setOnAction(e -> refreshList(null));
		this.editAddFile.setOnAction(e -> {
			if(currentDir != null) {
				FileChooser ch = new FileChooser();
				ch.setTitle("Add File to Archive");
				List<File> f = ch.showOpenMultipleDialog(this.stage);
				if(f != null && !f.isEmpty()) {
					ArchiveUtils.addToZip(f);
				}
			}
		});
		this.editRemoveFile.setOnAction(e -> {
			List<ArchiveFile> selected = this.listView.getSelectionModel().getSelectedItems();
			if(selected != null) {
				ArchiveUtils.removeFromZip(selected);
			}
		});
		this.windowExit.setOnAction(e -> this.stage.close());
		this.upDir.setOnAction(e -> upDir());
		
		this.toolBar.getItems().addAll(this.upDir);
		this.file.getItems().addAll(this.fileNew, this.fileOpen, this.fileClose);
		this.edit.getItems().addAll(this.editAddFile, this.editRemoveFile);
		this.extract.getItems().addAll(this.extractAll, this.extractSelected);
		this.window.getItems().addAll(this.windowExit);
		this.menuBar.getMenus().addAll(this.file, this.edit, this.extract, this.window);
		this.menus.getChildren().addAll(this.menuBar, this.toolBar);
		
		this.addContextMenu();
	}
	
	private void addContextMenu() {
		
	}
	
	public void refreshList(String dir) {
		this.listView.getItems().clear();
		this.currentDir = dir;
		this.upDir.setDisable(dir == null || dir.equals(""));
		if(dir != null) {
			if(ArchiveUtils.getOpenFile() != null) this.bottomText.setText(ArchiveUtils.getOpenFile() + "/" + dir);
			this.listView.getItems().addAll(ArchiveUtils.getFromDir(dir));
		} else {
			ArchiveUtils.close();
			this.bottomText.setText("");
		}
	}
	
	public void upDir() {
		if(this.currentDir != null && !this.currentDir.equals("")) {
			refreshList(ArchiveUtils.getFromName(this.currentDir).getDir());
		}
	}
	
	public void openFile() {
		FileChooser chooser = new FileChooser();
		ExtensionFilter ef = new ExtensionFilter("Archive Files (*.zip, *.rar, *.jar)", "*.zip", "*.rar", "*.jar");
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add(ef);
		chooser.setSelectedExtensionFilter(ef);
		chooser.setTitle("Open Archive");
		File f = chooser.showOpenDialog(this.stage);
		if(f != null) {
			try {
				ArchiveUtils.open(f);
			} catch (Exception e) {
				Util.error(e);
			}
		}
	}
	
	public void open() {
		this.stage.show();
	}
	
	public String currentDir() {
		return this.currentDir;
	}
	
	public void setPlaceholder(int id) {
		if(id == 0) {
			this.listView.setPlaceholder(this.placeholder);
		} else {
			this.listView.setPlaceholder(this.placeHolderEmpty);
		}
	}
	
}