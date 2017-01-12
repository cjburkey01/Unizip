package com.cjburkey.unizip;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class IO {
	
	public static final File dir = new File(System.getProperty("user.home"), "/unizip/");
	public static final File prefs = new File(dir, "/prefs.dat");
	public static final File tmpZip = new File(dir, "/zip.tmp");
	
	static {
		dir.mkdirs();
	}
	
	public static final Path fromJar(String pathInside) {
		try {
			URL url = Unizip.class.getResource(pathInside);
			if(url != null) {
				URI uri = url.toURI();
				if(uri != null && uri.getScheme().equals("jar")) {
					FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
					return fileSystem.getPath(pathInside);
				} else {
					return Paths.get(uri);
				}
			}
		} catch(Exception e) {
			Util.error(e);
		}
		return null;
	}
	
}