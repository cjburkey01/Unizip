package com.cjburkey.unizip;

import com.cjburkey.unizip.pref.PreferenceManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Util {
	
	public static final void print(Object ln) {
		System.out.println(ln);
	}
	
	public static final void error(Throwable t, String msg) {
		Platform.runLater(() -> {
			t.printStackTrace();
			alertError("Error!", msg);
		});
	}
	
	public static final void addCss(Scene scene) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add("css/common.css");
		scene.getStylesheets().add("css/" + ((PreferenceManager.getBool("customTheme")) ? "custom" : "app") + ".css");
	}
	
	public static final void alertError(String title, String text) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(title);
			alert.setContentText(text);
			
			DialogPane pane = alert.getDialogPane();
			Stage stage = new Stage();
			for(ButtonType buttonType : pane.getButtonTypes()) {
				ButtonBase button = (ButtonBase) pane.lookupButton(buttonType);
				button.setOnAction(evt -> {
					pane.setUserData(buttonType);
					stage.close();
				});
			}
			pane.getScene().setRoot(new Group());
			pane.setPadding(new Insets(10, 0, 10, 0));
			
			Scene scene = new Scene(pane);
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setAlwaysOnTop(true);
			stage.setResizable(false);
			stage.showAndWait();
		});
	}
	
	public static final void error(Throwable t) {
		error(t, "Error: " + t.getMessage());
	}
	
}