package com.cjburkey.unizip.lang;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.cjburkey.unizip.IO;
import com.cjburkey.unizip.Util;
import com.cjburkey.unizip.pref.PreferenceManager;

public class LanguageLoader {
	
	private static final String langDir = "/lang";
	private static Language currentLanguage = null;
	private static final List<Language> langs = new ArrayList<Language>();
	
	public static final void loadLangs() {
		langs.clear();
		try {
			Path path = IO.fromJar(langDir);
			if(path != null) {
				Stream<Path> walk = Files.walk(path, 1);
				for(Iterator<Path> it = walk.iterator(); it.hasNext();) {
					Path langFile = it.next();
					if(!langFile.getFileName().toString().equals(langDir.substring(1))) {
						Language f = loadLanguage(langFile);
						if(f != null) {
							langs.add(f);
							Util.print("Loaded: '" + f.getCodeName() + "' - " + f.getName());
						}
					}
				}
				walk.close();
			} else {
				Util.alertError("Error!", "Couldn't load languages from internal storage!  Please contact me at http://unizip.cjburkey.com/contact");
			}
			
		} catch(Exception e) {
			Util.error(e);
		}
		
		if(langs.size() < 1) {
			Util.alertError("Error!", "No languages loaded!  Please contact me at http://unizip.cjburkey.com/contact");
		} else {
			String pref = PreferenceManager.getString("language");
			if(pref != null) {
				setLanguage(pref);
			} else {
				currentLanguage = langs.get(0);
			}
			Util.print("Language set to: " + currentLanguage.getName());
		}
	}
	
	public static final boolean setLanguage(String codeName) {
		if(langs.size() > 0 && codeName != null) {
			for(Language lang : langs) {
				if(lang.getCodeName().equals(codeName)) {
					currentLanguage = lang;
					PreferenceManager.setPref("language", currentLanguage.getCodeName());
					return true;
				}
			}
		}
		return false;
	}
	
	public static final Language getLanguage() {
		return currentLanguage;
	}
	
	public static final Language[] getLanguages() {
		return langs.toArray(new Language[langs.size()]);
	}
	
	public static final String get(String key) {
		if(currentLanguage != null) {
			for(Entry<String, String> e : currentLanguage.getKeys().entrySet()) {
				if(e.getKey().equals(key)) {
					return e.getValue();
				}
			}
		}
		return null;
	}
	
	public static final Language loadLanguage(Path path) {
		try {
			List<String> lines = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
			String l = null;
			while((l = reader.readLine()) != null) {
				lines.add(l);
			}
			reader.close();
			Map<String, String> keys = new HashMap<String, String>();
			String name = lines.get(0);
			lines.remove(0);
			for(String line : lines) {
				String[] split = line.split("=");
				if(split.length == 2 && split[0] != null && split[1] != null) {
					keys.put(split[0], split[1]);
				}
			}
			return new Language(name, path.getFileName().toString(), keys);
		} catch(Exception e) {
			Util.error(e);
		}
		return null;
	}
	
}