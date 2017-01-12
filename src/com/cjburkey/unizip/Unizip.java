package com.cjburkey.unizip;

import com.cjburkey.unizip.lang.LanguageLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class Unizip extends Application {
	
	public static final String version = "0.0.4-2";
	private static App inst;
	
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> Util.error(e));
		LanguageLoader.loadLangs();
		launch(args);
	}
	
	public static App getApp() {
		return inst;
	}
	
	public void start(Stage stage) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> Util.error(e));
		App app = new App();
		app.init(stage);
		inst = app;
	}
	
}