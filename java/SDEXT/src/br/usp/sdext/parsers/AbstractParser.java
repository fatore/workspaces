package br.usp.sdext.parsers;

import java.io.File;

public abstract class AbstractParser { 
	
	public void parseAndSave(String baseDirStr) throws Exception {
		
		File baseDir = null;
		
		if (baseDirStr != null) {
			baseDir = new File(baseDirStr);
		}
		
		System.out.println("Looking for directory " + baseDirStr);
		
		if ( baseDir != null && baseDir.exists() && baseDir.isDirectory()) {
			System.out.println("Directory successfully found.\n");					
		} else {
			throw new Exception("\nError: Invalid directory.");
		}
		
		load(baseDir);
		
		save();
	}
	
	protected void load(File file) throws Exception {
		if (file.isDirectory()) {
			for (File contents : file.listFiles()) {
				load(contents);
			}
		} else {
			loadFile(file);
		}
	}
	
	protected abstract void loadFile(File file) throws Exception;
	protected abstract void save() throws Exception;
	
}
