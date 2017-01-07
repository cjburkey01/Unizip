package com.cjburkey.unizip.archive;

import java.io.File;
import java.util.zip.ZipEntry;

public class ArchiveFile implements Comparable<ArchiveFile> {
	
	private ZipEntry entry;
	
	public ArchiveFile(ZipEntry entry) {
		this.entry = entry;
	}
	
	public boolean isDirectory() {
		return this.entry.isDirectory();
	}
	
	public String getDir() {
		String par = new File(this.entry.getName()).getParent();
		return (par == null) ? "" : par;
	}
	
	public String getName() {
		return new File(this.entry.getName()).getName();
	}
	
	public String getFull() {
		return new File(this.entry.getName()).toString();
	}
	
	public String toString() {
		return this.getName() + ((this.isDirectory()) ? "/" : "");
	}
	
	public ZipEntry getZipEntry() {
		return this.entry;
	}
	
	public int compareTo(ArchiveFile o) {
		return this.entry.getName().compareToIgnoreCase(o.getZipEntry().getName());
	}
	
}