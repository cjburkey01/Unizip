package com.cjburkey.unizip.archive;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import com.cjburkey.unizip.Unizip;
import com.cjburkey.unizip.Util;

public class ArchiveUtils {
	
	private static File openFile;
	private static final List<ArchiveFile> zipFile = new ArrayList<ArchiveFile>();
	
	public static final void open(File file) {
		try {
			close();
			openFile = file;
			ZipFile zfile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zfile.entries();
			while(entries.hasMoreElements()) {
				zipFile.add(new ArchiveFile(entries.nextElement()));
			}
			zfile.close();
			Collections.sort(zipFile);
			Unizip.getApp().refreshList("");
			Unizip.getApp().setPlaceholder(1);
		} catch(Exception e) {
			Util.error(e, "There was an error while trying to open your archive.  This could be because the file is corrupt, or that it is an unsupported file type.");
		}
	}
	
	public static final void close() {
		zipFile.clear();
		openFile = null;
		Unizip.getApp().setPlaceholder(0);
	}
	
	public static final List<ArchiveFile> getFromDir(String dir) {
		List<ArchiveFile> n = new ArrayList<ArchiveFile>();
		zipFile.forEach(e -> {
			if(e.getDir().equals(dir)) n.add(e);
		});
		Collections.sort(n);
		return n;
	}
	
	public static final void removeFromZip(List<ArchiveFile> aff) {
		if(Unizip.getApp().currentDir() != null) {
			List<ArchiveFile> newList = new ArrayList<ArchiveFile>();
			newList.addAll(aff);
			for(ArchiveFile ae : aff) {
				List<ArchiveFile> del = getChildren(ae);
				newList.addAll(del);
				Util.print(del.size());
			}
			ArchiveEdit.removeFromZip(openFile, newList);
		}
	}
	
	public static final List<ArchiveFile> getChildren(ArchiveFile af) {
		List<ArchiveFile> newList = new ArrayList<ArchiveFile>();
		if(af.isDirectory()) {
			for(ArchiveFile test : zipFile) {
				if(test.getZipEntry().getName().startsWith(af.getZipEntry().getName())) {
					newList.add(test);
				}
			}
		}
		return newList;
	}
	
	public static final void addToZip(List<File> toAdd) {
		if(Unizip.getApp().currentDir() != null) {
			ArchiveEdit.addFile(openFile, toAdd);
		}
	}
	
	public static final ArchiveFile getFromName(String name) {
		for(ArchiveFile af : zipFile) {
			if(af.getFull().equals(name)) {
				return af;
			}
		}
		return null;
	}
	
	public static final ArchiveFile[] getEntries() {
		return zipFile.toArray(new ArchiveFile[zipFile.size()]);
	}
	
	public static final File getOpenFile() {
		return openFile;
	}
	
	public static final boolean existsInArchive(String path) {
		for(ArchiveFile ae : zipFile) {
			if(ae.getZipEntry().getName().equals(path)) return true;
		}
		return false;
	}
	
	public static final boolean isArchive(File f) {
		String n = f.getName();
		if(n.endsWith(".zip") || n.endsWith(".rar") || n.endsWith(".jar")) {
			return true;
		}
		return false;
	}
	
}