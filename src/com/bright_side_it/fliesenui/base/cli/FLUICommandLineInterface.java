package com.bright_side_it.fliesenui.base.cli;

import java.io.File;

import com.bright_side_it.fliesenui.generator.service.CodeGeneratorService;

public class FLUICommandLineInterface {
	private static final String VERSION = "1.0.0";
	private static final String ACTION_GENERATE = "-generate";
	
	public static void main(String[] args) {
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("Fliesen UI - version " + VERSION + ", 2016 by Philip Heyse - fliesenui@bright-side-it.com");
		
		if (args.length != 2){
			printUsage();
			return;
		}
		
		String action = args[0];
		String path = args[1];
		
		if (!ACTION_GENERATE.equals(action)){
			printUsage();
			return;
		}
		
		File dir = new File(path);
		if (!dir.exists()){
			System.out.println("Path '" + dir.getAbsolutePath() + "' does not exist");
			System.exit(-1);
			return;
		}
		
		try{
			new CodeGeneratorService().generateCode(new File(path));
		} catch (Throwable e){
			e.printStackTrace();
			System.out.println("\nFailed.");
			System.exit(-2);
			return;
		}
		
		System.out.println("\nFinished successfully.");
	}

	private static void printUsage() {
		System.out.println("");
		System.out.println("");
		System.out.println("   usage: " + ACTION_GENERATE + " <project-dir>");
		System.out.println("   <project-dir>: the directory that contains the Fliesen UI project including the 'FliesenUIProject.xml'");
		System.out.println("");
		System.out.println("   example: java -jar FliesenUI_v01_00_00.jar -generate C:\\My_FLUI_Project");
		System.out.println("");
	}
}
