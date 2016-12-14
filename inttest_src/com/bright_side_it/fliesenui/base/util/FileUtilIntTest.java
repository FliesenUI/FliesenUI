package com.bright_side_it.fliesenui.base.util;

import java.io.File;

import org.junit.Test;

import com.bright_side_it.fliesenui.base.util.FileUtil;

public class FileUtilIntTest {
    @Test
    public void test_appendFile_normal() throws Exception {
        File testDir = new File("G:\\DA1D\\appendTest");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }

        if (!testDir.exists()) {
            throw new Exception("Could not crete test dir: " + testDir);
        }

        for (int i = 0; i < 20; i++) {
            String text = "hello! This is a text to be appended: " + i + "\n";
            FileUtil.appendToRollingFile(testDir, "filename", ".txt", 300, 5, text);
        }
    }
}
