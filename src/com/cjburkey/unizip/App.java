package com.cjburkey.unizip;

import com.cjburkey.unizip.window.WindowHandler;
import com.cjburkey.unizip.windows.WindowMain;
import com.cjburkey.unizip.windows.WindowPrefs;
import javafx.stage.Stage;

public class App {
	
	private WindowHandler windowHandler;
	private WindowMain windowMain;
	private WindowPrefs windowPrefs;
	
	public void init(Stage stage) {
		this.windowHandler = new WindowHandler();
		this.windowMain = new WindowMain(stage);
		this.windowPrefs = new WindowPrefs();
		
		this.windowHandler.init(this.windowMain);
		this.windowHandler.init(this.windowPrefs);
		
		this.windowHandler.show(this.windowMain);
	}
	
	public WindowHandler getWindowHandler() {
		return this.windowHandler;
	}
	
	public WindowMain getMainWindow() {
		return this.windowMain;
	}
	
	public WindowPrefs getPrefsWindow() {
		return this.windowPrefs;
	}
	
}