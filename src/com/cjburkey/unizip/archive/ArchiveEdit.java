package com.cjburkey.unizip.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import com.cjburkey.unizip.IO;
import com.cjburkey.unizip.Unizip;
import com.cjburkey.unizip.Util;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ArchiveEdit {
	
	private static Stage removeStage = null;
	private static ProgressBar removeProg = null;
	private static Label removeInfo = null;
	public static final void removeFromZip(File f, List<ArchiveFile> af) {
		Thread thread = new Thread(() -> {
			try {
				Platform.runLater(() -> {
					removeStage = new Stage();
					BorderPane root = new BorderPane();
					Scene scene = new Scene(root);
					removeProg = new ProgressBar();
					removeInfo = new Label("Please wait...");
					
					scene.getStylesheets().add("css/app.css");
					
					removeProg.setMaxWidth(Double.MAX_VALUE);
					removeInfo.setPadding(new Insets(10));
					removeInfo.setTextAlignment(TextAlignment.CENTER);
					
					root.setPadding(new Insets(10));
					root.setCenter(removeProg);
					root.setBottom(removeInfo);
					BorderPane.setAlignment(removeInfo, Pos.CENTER);
					
					removeStage.initModality(Modality.APPLICATION_MODAL);
					removeStage.setScene(scene);
					removeStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
					removeStage.setResizable(false);
					removeStage.setTitle("Working...");
					removeStage.show();
					removeStage.setOnCloseRequest(e -> e.consume());
					removeProg.setProgress(0);
				});
				
				IO.tmpZip.delete();
				ZipFile zf = new ZipFile(f);
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(IO.tmpZip));
				Enumeration<? extends ZipEntry> entries = zf.entries();
				byte[] buffer = new byte[4096];
				List<ZipEntry> es = new ArrayList<ZipEntry>();
				while(entries.hasMoreElements()) {
					es.add(entries.nextElement());
				}
				List<ZipEntry> keep = new ArrayList<ZipEntry>();
				for(int i = 0; i < es.size(); i ++) {
					ZipEntry e = es.get(i);
					keep.add(e);
					for(ArchiveFile aff : af) {
						if(e.getName().equals(aff.getZipEntry().getName())) {
							keep.remove(e);
						}
					}
				}
				int i = 0;
				for(ZipEntry ae : keep) {
					double prog = (double) ((i + 1d) / (double) es.size());
					int ib = i;
					Platform.runLater(() -> {
						removeProg.setProgress(prog);
						NumberFormat formatter = new DecimalFormat("#0.00");
						String out = formatter.format(prog * 100);
						removeInfo.setText(ib + " / " + es.size() + " = " + out + "%");
					});
					ZipEntry clone = new ZipEntry(ae);
					clone.setCompressedSize(-1);
					zos.putNextEntry(clone);
					InputStream in = zf.getInputStream(ae);
					while(in.available() > 0) {
						int read = in.read(buffer);
						zos.write(buffer, 0, read);
					}
					in.close();
					zos.closeEntry();
					i ++;
				}
				zos.close();
				zf.close();
				Platform.runLater(() -> {
					removeInfo.setText("Copying TMP file, this may take a few seconds...");
					removeProg.setProgress(-1);
				});
				Util.print("TMP Done");
				Files.move(IO.tmpZip.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch(Exception e) {
				Util.error(e);
			}
			Util.print("Done.");
			Platform.runLater(() -> {
				removeStage.hide();
				String currentDir = Unizip.getApp().currentDir();
				ArchiveUtils.open(f);
				Unizip.getApp().refreshList(currentDir);
			});
		});
		thread.start();
	}
	
	private static Stage addStage = null;
	private static ProgressBar addProg = null;
	private static Label addInfo = null;
	public static final void addFile(File f, List<File> add) {
		for(File file : add) {
			String name = Unizip.getApp().currentDir();
			if(!name.equals("")) name += "/";
			name += file.getName();
			if(ArchiveUtils.existsInArchive(name)) {
				Util.alertError("Error", "That file is already in the archive.");
				return;
			}
		}
		Thread thread = new Thread(() -> {
			try {
				Platform.runLater(() -> {
					addStage = new Stage();
					BorderPane root = new BorderPane();
					Scene scene = new Scene(root);
					addProg = new ProgressBar();
					addInfo = new Label("Please wait...");
					
					scene.getStylesheets().add("css/app.css");
					
					addProg.setMaxWidth(Double.MAX_VALUE);
					addInfo.setPadding(new Insets(10));
					addInfo.setTextAlignment(TextAlignment.CENTER);
					
					root.setPadding(new Insets(10));
					root.setCenter(addProg);
					root.setBottom(addInfo);
					BorderPane.setAlignment(addInfo, Pos.CENTER);
					
					addStage.initModality(Modality.APPLICATION_MODAL);
					addStage.setScene(scene);
					addStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
					addStage.setResizable(false);
					addStage.setTitle("Working...");
					addStage.show();
					addStage.setOnCloseRequest(e -> e.consume());
					addProg.setProgress(0);
				});
				
				IO.tmpZip.delete();
				ZipFile zf = new ZipFile(f);
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(IO.tmpZip));
				Enumeration<? extends ZipEntry> entries = zf.entries();
				byte[] buffer = new byte[4096];
				List<ZipEntry> es = new ArrayList<ZipEntry>();
				while(entries.hasMoreElements()) {
					es.add(entries.nextElement());
				}
				for(int i = 0; i < es.size(); i ++) {
					double prog = (double) ((i + 1d) / (double) es.size());
					int ib = i;
					Platform.runLater(() -> {
						addProg.setProgress(prog);
						NumberFormat formatter = new DecimalFormat("#0.00");
						String out = formatter.format(prog * 100);
						addInfo.setText(ib + " / " + es.size() + " = " + out + "%");
					});
					ZipEntry e = es.get(i);
					ZipEntry clone = new ZipEntry(e);
					clone.setCompressedSize(-1);
					zos.putNextEntry(clone);
					InputStream in = zf.getInputStream(e);
					while(in.available() > 0) {
						int read = in.read(buffer);
						zos.write(buffer, 0, read);
					}
					in.close();
					zos.closeEntry();
				}
				for(File file : add) {
					String name = Unizip.getApp().currentDir();
					if(!name.equals("")) name += "/";
					name += file.getName();
					if(file.exists()) {
						ZipEntry addF = new ZipEntry(name);
						addF.setSize(file.length());
						addF.setTime(file.lastModified());
						zos.putNextEntry(addF);
						FileInputStream fis = new FileInputStream(file);
						CRC32 crc32 = new CRC32();
						byte[] data = new byte[4096];
						int len;
						while((len = fis.read(data)) > -1) {
							zos.write(data, 0, len);
							crc32.update(data, 0, len);
						}
						addF.setCrc(crc32.getValue());
						fis.close();
						zos.closeEntry();
					}
				}
				zos.close();
				zf.close();
				Platform.runLater(() -> {
					addInfo.setText("Copying TMP file, this may take a few seconds...");
					addProg.setProgress(-1);
				});
				Util.print("TMP Done");
				Files.move(IO.tmpZip.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch(Exception e) {
				Util.error(e);
			}
			Util.print("Done.");
			Platform.runLater(() -> {
				addStage.hide();
				String currentDir = Unizip.getApp().currentDir();
				ArchiveUtils.open(f);
				Unizip.getApp().refreshList(currentDir);
			});
		});
		thread.start();
	}
	
}