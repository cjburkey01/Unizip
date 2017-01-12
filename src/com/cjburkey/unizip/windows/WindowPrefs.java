package com.cjburkey.unizip.windows;

import com.cjburkey.unizip.Unizip;
import com.cjburkey.unizip.Util;
import com.cjburkey.unizip.archive.ArchiveUtils;
import com.cjburkey.unizip.lang.Language;
import com.cjburkey.unizip.lang.LanguageLoader;
import com.cjburkey.unizip.pref.PreferenceManager;
import com.cjburkey.unizip.window.IWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WindowPrefs implements IWindow {
	
	private Stage stage;
	private VBox root;
	private Scene scene;
	private HBox buttons;
	private Button save;
	private Button cancel;
	
	private ComboBox<Language> language;
	private CheckBox customTheme;
	
	public void init() {
		this.stage = new Stage();
		this.root = new VBox();
		this.scene = new Scene(this.root);
		this.buttons = new HBox();
		this.save = new Button(LanguageLoader.get("save"));
		this.cancel = new Button(LanguageLoader.get("cancel"));
		
		this.language = new ComboBox<Language>();
		this.customTheme = new CheckBox(LanguageLoader.get("buttonTheme"));
		
		build();
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
	}
	
	private void buildMain() {
		Util.addCss(this.scene);
		
		this.root.setPadding(new Insets(10));
		this.root.setSpacing(10);
		this.root.setAlignment(Pos.CENTER_LEFT);
		this.root.getChildren().addAll(this.language, this.customTheme, this.buttons);
		
		this.buttons.setSpacing(10);
		this.buttons.setAlignment(Pos.CENTER_RIGHT);
		this.buttons.setMaxWidth(Double.MAX_VALUE);
		this.buttons.getChildren().addAll(this.save, this.cancel);
		
		this.language.setMaxWidth(Double.MAX_VALUE);
		
		this.stage.initModality(Modality.APPLICATION_MODAL);
		this.stage.setScene(this.scene);
		this.stage.setResizable(false);
		this.stage.setTitle("Preferences");
		this.stage.sizeToScene();
		this.stage.centerOnScreen();
	}
	
	private void buildMainActions() {
		this.stage.setOnCloseRequest(e -> { e.consume(); this.stage.hide(); });
		this.cancel.setOnAction(e -> this.stage.hide());
		this.stage.setOnShown(e -> {
			this.customTheme.setSelected(PreferenceManager.getBool("customTheme"));
			
			this.language.getItems().clear();
			this.language.getItems().addAll(LanguageLoader.getLanguages());
			this.language.getSelectionModel().select(LanguageLoader.getLanguage());
		});
		this.save.setOnAction(e -> {
			PreferenceManager.setPref("customTheme", customTheme.isSelected() + "");
			LanguageLoader.setLanguage(this.language.getSelectionModel().getSelectedItem().getCodeName());
			
			this.stage.hide();
			Unizip.getApp().getMainWindow().init();
			if(ArchiveUtils.getOpenFile() != null) {
				String current = Unizip.getApp().getMainWindow().getCurrentDir();
				ArchiveUtils.open(ArchiveUtils.getOpenFile());
				Unizip.getApp().getMainWindow().refreshList(current);
			}
		});
	}
	
}