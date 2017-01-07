package com.cjburkey.unizip;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Util {
	
	public static final void print(Object ln) {
		System.out.println(ln);
	}
	
	public static final void error(Throwable t, String msg) {
		Platform.runLater(() -> {
			t.printStackTrace();
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Error!");
			a.setContentText(msg);
			a.show();
		});
	}
	
	public static final void error(Throwable t) {
		error(t, "Error: " + t.getMessage());
	}
	
}