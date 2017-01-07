package com.cjburkey.unizip;

import javafx.application.Application;
import javafx.stage.Stage;

public class Unizip extends Application {
	
	public static final String version = "0.0.1";
	private static App inst;
	
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> Util.error(e));
		launch(args);
	}
	
	public static App getApp() {
		return inst;
	}
	
	public void start(Stage stage) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> Util.error(e));
		App app = new App();
		app.init(stage);
		app.open();
		inst = app;
	}
	
}