package com.cjburkey.unizip.pref;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.cjburkey.unizip.IO;
import com.cjburkey.unizip.Util;

public class PreferenceManager {
	
	private static HashMap<Object, Object> prefs = new HashMap<Object, Object>();
	
	public static final boolean setPref(Object key, String data) {
		try {
			prefs.put(key, data);
			save();
			return true;
		} catch (Exception e) {
			Util.error(e);
		}
		return false;
	}
	
	public static final Object getPref(Object key) {
		try {
			load();
			return prefs.get(key);
		} catch (Exception e) {
			Util.error(e);
		}
		return null;
	}
	
	public static final boolean getBool(Object key) {
		Object o = getPref(key);
		if(o == null) return false;
		return Boolean.valueOf(o + "");
	}
	
	public static final String getString(Object key) {
		Object o = getPref(key);
		if(o == null) return null;
		return (String) o;
	}
	
	public static final Set<Entry<Object, Object>> getPrefs() {
		try {
			load();
			return prefs.entrySet();
		} catch(Exception e) {
			Util.error(e);
		}
		return new HashSet<Entry<Object, Object>>();
	}
	
	private static final void save() throws Exception {
		Properties props = new Properties();
		for(Entry<Object, Object> e : prefs.entrySet()) {
			props.put(e.getKey(), e.getValue());
		}
		FileOutputStream fos = new FileOutputStream(IO.prefs);
		props.store(fos, "Settings for Unizip");
		fos.close();
	}
	
	private static final void load() throws Exception {
		if(!IO.prefs.exists()) return;
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(IO.prefs);
		props.load(fis);
		fis.close();
		prefs.clear();
		for(Entry<Object, Object> e : props.entrySet()) {
			prefs.put(e.getKey(), e.getValue());
		}
	}
	
}