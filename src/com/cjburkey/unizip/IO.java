package com.cjburkey.unizip;

import java.io.File;

public class IO {
	
	public static final File dir = new File(System.getProperty("user.home"), "/unizip/");
	public static final File prefs = new File(dir, "/prefs.dat");
	public static final File tmpZip = new File(dir, "/zip.tmp");
	
	static {
		dir.mkdirs();
	}
	
}