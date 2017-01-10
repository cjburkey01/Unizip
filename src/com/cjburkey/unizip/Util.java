package com.cjburkey.unizip;

import com.cjburkey.unizip.pref.PreferenceManager;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

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
		Alert alert = new Alert(AlertType.ERROR);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setContentText(text);
		alert.setTitle(title);
		alert.showAndWait();
	}
	
	public static final void error(Throwable t) {
		error(t, "Error: " + t.getMessage());
	}
	
}