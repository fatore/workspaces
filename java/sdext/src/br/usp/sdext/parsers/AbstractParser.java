package br.usp.sdext.parsers;

import java.io.File;

public abstract class AbstractParser { 
	
	public void parse(String baseDirStr) throws Exception {
		
		File baseDir = null;
		
		if (baseDirStr != null) {
			baseDir = new File(baseDirStr);
		}
		
		System.out.print("Looking for directory " + baseDirStr + ".");
		
		if ( baseDir != null && baseDir.exists() && baseDir.isDirectory()) {
			
			System.out.println(" OK!");	
			
		} else {
			
			System.out.println(" FAILED!");
			throw new Exception("\nError: Invalid directory.");
		}
		
		load(baseDir);
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
	public abstract void save() throws Exception;
	
}
