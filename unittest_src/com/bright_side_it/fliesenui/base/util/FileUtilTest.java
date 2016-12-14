package com.bright_side_it.fliesenui.base.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.bright_side_it.fliesenui.base.util.FileUtil;

public class FileUtilTest {

    @Test
    public void test_getFilenameWithoutEnding_simple() {
        String result = FileUtil.getFilenameWithoutEnding("myFile.txt");
        assertEquals("myFile", result);
    }

    @Test
    public void test_getFilenameWithoutEnding_noEnding() {
        String result = FileUtil.getFilenameWithoutEnding("myFile");
        assertEquals("myFile", result);
    }

    @Test
    public void test_getFilenameWithoutEnding_onlyEnding() {
        String result = FileUtil.getFilenameWithoutEnding(".txt");
        assertEquals("", result);
    }
    
    @Test
    public void test_getRelativePath_simple(){
    	File baseFile = new File("C:\\myDir\\work\\stuff\\projectA\\xyz");
    	File file = new File("C:\\myDir\\work\\stuff\\other\\abc\\next");
		String result = FileUtil.getRelativePath(baseFile, file, null);
		assertEquals("..\\..\\other\\abc\\next", result);
		
		assertEquals(false, Paths.get(result).isAbsolute());
		assertEquals(true, Paths.get(baseFile.getAbsolutePath()).isAbsolute());
		
		
		File outputDir = Paths.get(baseFile.getAbsolutePath(), result).toFile();
		try {
			System.out.println("outputDir = >>" + outputDir.getCanonicalPath() + "<<");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }

    @Test
    public void test_getRelativePath_differntDrive(){
    	File baseFile = new File("C:\\myDir\\work\\stuff\\projectA\\xyz");
    	File file = new File("D:\\test");
    	String result = FileUtil.getRelativePath(baseFile, file, null);
    	assertEquals(null, result);
    }
    
}
