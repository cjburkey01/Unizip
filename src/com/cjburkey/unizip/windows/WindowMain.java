package com.cjburkey.unizip.windows;

import java.io.File;
import java.util.List;
import com.cjburkey.unizip.Unizip;
import com.cjburkey.unizip.Util;
import com.cjburkey.unizip.archive.ArchiveFile;
import com.cjburkey.unizip.archive.ArchiveUtils;
import com.cjburkey.unizip.lang.LanguageLoader;
import com.cjburkey.unizip.window.IWindow;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class WindowMain implements IWindow {
	
	// DIR
	private String currentDir;
	
	// MAIN
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
	
	// MENUS
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
	private MenuItem windowPrefs;
	
	public WindowMain(Stage stage) {
		this.stage = stage;
	}

	public void init() {
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
	}

	public void show() {
		this.stage.show();
	}

	public void hide() {
		this.stage.hide();
	}

	public Stage getStage() {
		return this.stage;
	}
	
	private void build() {
		buildMain();

		buildMainActions();
		buildMenusActions();
	}
	
	private void buildMain() {
		this.root = new BorderPane();
		this.scene = new Scene(this.root);
		this.placeholder = new VBox();
		this.placeHolderText = new Label(LanguageLoader.get("noArchiveOpen"));
		this.placeHolderEmpty = new Label(LanguageLoader.get("dirArchiveEmpty"));
		this.placeHolderButton = new Button(LanguageLoader.get("openArchive"));
		this.listView = new ListView<ArchiveFile>();
		this.bottomText = new Label();
		this.bottom = new ToolBar();
		
		Util.addCss(this.scene);
		buildMenus();
		
		this.bottomText.setMaxWidth(Double.MAX_VALUE);
		this.bottom.getItems().addAll(this.bottomText);
		this.bottomText.setAlignment(Pos.CENTER_RIGHT);
		this.bottomText.setTextAlignment(TextAlignment.RIGHT);
		this.bottomText.setWrapText(false);
		
		this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.placeHolderText.setWrapText(true);
		this.placeholder.getChildren().addAll(this.placeHolderText, this.placeHolderButton);
		this.placeholder.setPadding(new Insets(25));
		this.placeholder.setAlignment(Pos.CENTER);
		this.placeholder.setSpacing(10);
		this.listView.setPlaceholder(this.placeholder);
		this.listView.setStyle("-fx-border-color: rgb(200, 200, 200);");
		this.root.setTop(this.menus);
		this.root.setCenter(this.listView);
		this.root.setBottom(this.bottom);
	}
	
	private void buildMenus() {
		this.menus = new VBox();
		this.toolBar = new ToolBar();
		this.upDir = new Button(new String(new char[] { 0x25B2 }));
		this.menuBar = new MenuBar();
		this.file = new Menu(LanguageLoader.get("file"));
		this.edit = new Menu(LanguageLoader.get("edit"));
		this.extract = new Menu(LanguageLoader.get("extract"));
		this.window = new Menu(LanguageLoader.get("window"));
		this.fileNew = new MenuItem(LanguageLoader.get("newArchive"));
		this.fileOpen = new MenuItem(LanguageLoader.get("openArchive"));
		this.fileClose = new MenuItem(LanguageLoader.get("closeArchive"));
		this.editAddFile = new MenuItem(LanguageLoader.get("into"));
		this.editRemoveFile = new MenuItem(LanguageLoader.get("delete"));
		this.extractAll = new MenuItem(LanguageLoader.get("exAll"));
		this.extractSelected = new MenuItem(LanguageLoader.get("exSel"));
		this.windowExit = new MenuItem(LanguageLoader.get("exit"));
		this.windowPrefs = new MenuItem(LanguageLoader.get("prefs"));
		
		this.toolBar.getItems().addAll(this.upDir);
		this.file.getItems().addAll(this.fileNew, this.fileOpen, this.fileClose);
		this.edit.getItems().addAll(this.editAddFile, this.editRemoveFile);
		this.extract.getItems().addAll(this.extractAll, this.extractSelected);
		this.window.getItems().addAll(this.windowPrefs, this.windowExit);
		this.menuBar.getMenus().addAll(this.file, this.edit, this.extract, this.window);
		this.menus.getChildren().addAll(this.menuBar, this.toolBar);
	}
	
	private void buildMainActions() {
		this.stage.setOnCloseRequest(e -> Platform.exit());
		this.placeHolderButton.setOnAction(e -> openFile());
		this.listView.setOnDragExited(e -> this.listView.setStyle("-fx-border-color: rgb(200, 200, 200);"));
		this.listView.setOnDragOver(e -> {
			if(e.getDragboard().hasFiles()) {
				e.acceptTransferModes(TransferMode.COPY);
				this.listView.setStyle("-fx-border-color: rgb(100, 100, 255);");
			}
		});
		this.listView.setOnDragDropped(e -> {
			this.listView.setStyle("-fx-border-color: rgb(200, 200, 200);");
			Dragboard db = e.getDragboard();
			e.setDropCompleted(db.hasFiles());
			if(db.hasFiles()) {
				if(db.getFiles().size() == 1 && ArchiveUtils.isArchive(db.getFiles().get(0)) && this.currentDir == null) {
					ArchiveUtils.open(db.getFiles().get(0));
				} else {
					ArchiveUtils.addToZip(db.getFiles());
				}
			}
			e.consume();
		});
		this.listView.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				ArchiveFile selected = this.listView.getSelectionModel().getSelectedItem();
				if(selected != null) {
					if(selected.isDirectory()) {
						this.refreshList(selected.getFull());
					}
				}
			}
		});
	}
	
	private void buildMenusActions() {
		this.fileOpen.setOnAction(e -> this.openFile());
		this.fileClose.setOnAction(e -> this.refreshList(null));
		this.windowExit.setOnAction(e -> this.stage.close());
		this.windowPrefs.setOnAction(e -> Unizip.getApp().getWindowHandler().show(Unizip.getApp().getPrefsWindow()));
		this.upDir.setOnAction(e -> this.upDir());
		this.editRemoveFile.setOnAction(e -> {
			List<ArchiveFile> selected = this.listView.getSelectionModel().getSelectedItems();
			if(selected != null) {
				ArchiveUtils.removeFromZip(selected);
			}
		});
		this.editAddFile.setOnAction(e -> {
			FileChooser ch = new FileChooser();
			ch.setTitle("Add File to Archive");
			List<File> f = ch.showOpenMultipleDialog(this.stage);
			if(f != null && !f.isEmpty()) {
				ArchiveUtils.addToZip(f);
			}
		});
	}
	
	// UTILS
	
	public void upDir() {
		if(this.currentDir != null && !this.currentDir.equals("")) {
			refreshList(ArchiveUtils.getFromName(this.currentDir).getDir());
		}
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
	
	public void setPlaceholder(int id) {
		if(id == 0) {
			this.listView.setPlaceholder(this.placeholder);
		} else {
			this.listView.setPlaceholder(this.placeHolderEmpty);
		}
	}
	
	public void openFile() {
		FileChooser chooser = new FileChooser();
		ExtensionFilter ef = new ExtensionFilter(LanguageLoader.get("archiveFiles") + " (*.zip, *.rar, *.jar)", "*.zip", "*.rar", "*.jar");
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add(ef);
		chooser.setSelectedExtensionFilter(ef);
		chooser.setTitle(LanguageLoader.get("openArchive"));
		File f = chooser.showOpenDialog(this.stage);
		if(f != null) {
			try {
				ArchiveUtils.open(f);
			} catch (Exception e) {
				Util.error(e);
			}
		}
	}
	
	public String getCurrentDir() {
		return this.currentDir;
	}
	
}