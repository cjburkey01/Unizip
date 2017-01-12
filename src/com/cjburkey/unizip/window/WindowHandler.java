package com.cjburkey.unizip.window;

import java.util.ArrayList;
import java.util.List;

public class WindowHandler {
	
	private final List<IWindow> shownWindows = new ArrayList<IWindow>();
	
	public void init(IWindow window) {
		window.init();
	}
	
	public void show(IWindow window) {
		shownWindows.add(window);
		window.show();
	}
	
	public void hide(IWindow window) {
		shownWindows.remove(window);
		window.hide();
	}
	
	public IWindow[] getShownWindows() {
		return shownWindows.toArray(new IWindow[shownWindows.size()]);
	}
	
}