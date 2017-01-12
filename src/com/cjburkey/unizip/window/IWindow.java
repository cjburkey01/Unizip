package com.cjburkey.unizip.window;

import javafx.stage.Stage;

public interface IWindow {
	
	void init();
	void show();
	void hide();
	Stage getStage();
	
}