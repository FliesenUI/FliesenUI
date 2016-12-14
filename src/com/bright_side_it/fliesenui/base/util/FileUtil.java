package com.bright_side_it.fliesenui.base.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {
    private static final String TEXT_CHARSET = "UTF-8";

    public static void writeStringToFile(File file, String string) throws Exception {
        Files.write(file.toPath(), string.getBytes(TEXT_CHARSET));
    }

    public static String readFileAsString(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), Charset.forName(TEXT_CHARSET));
    }

    public static void removeFilesAndDirsInDir(File directory) throws Exception {
        if (!directory.exists()) {
            return;
        }
        try {
            List<File> filesInFolder = Files.walk(directory.toPath()).map(Path::toFile).collect(Collectors.toList());
            Collections.sort(filesInFolder);
            filesInFolder.remove(0); //: remove the first item which is the source dir itself. Comparing does not work
            Collections.reverse(filesInFolder);
            for (File i : filesInFolder) {
                i.delete();
                if (i.exists()) {
                    throw new Exception("Could not remove file '" + i.getAbsolutePath() + "'");
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not clear directory '" + directory.getAbsolutePath() + "'", e);
        }
    }

    public static String getFilenameWithoutEnding(String filename) {
        int pos = filename.lastIndexOf(".");
        if (pos < 0) {
            return filename;
        }
        return filename.substring(0, pos);
    }

    public static String getFilenameEnding(String filename) {
        int pos = filename.lastIndexOf(".");
        if (pos < 0) {
            return "";
        }
        return filename.substring(pos);
    }



    public static boolean isChanged(File file, Long lastTime) {
        if (lastTime == null) {
            return true;
        }
        return file.lastModified() != lastTime.longValue();
    }

    private static File createRollingFile(File dir, String filenameWithoutEnding, String ending, int index) {
        return new File(dir, filenameWithoutEnding + index + ending);
    }

    public static void appendToRollingFile(File dir, String filenameWithoutEnding, String ending, long maxFileSize, int maxFiles, String text) throws Exception {
        byte[] bytes = text.getBytes(TEXT_CHARSET);

        File currentFile = createRollingFile(dir, filenameWithoutEnding, ending, 0);
        if (!currentFile.exists()) {
            Files.write(currentFile.toPath(), bytes, StandardOpenOption.CREATE);
        } else if (currentFile.length() + bytes.length <= maxFileSize) {
            Files.write(currentFile.toPath(), bytes, StandardOpenOption.APPEND);
        } else {
            File[] files = new File[maxFiles];
            for (int i = 0; i < maxFiles; i++) {
                files[i] = createRollingFile(dir, filenameWithoutEnding, ending, i);
            }
            if (files[maxFiles - 1].exists()) {
                files[maxFiles - 1].delete();
            }
            for (int i = maxFiles - 2; i >= 0; i--) {
                if (files[i].exists()) {
                    files[i].renameTo(files[i + 1]);
                }
            }
            Files.write(currentFile.toPath(), bytes, StandardOpenOption.CREATE);
        }
    }
    
    public static String getRelativePath(File baseFile, File file, String resultIfImpossible){
        Path pathBase = Paths.get(baseFile.getAbsolutePath());
        Path pathAbsolute = Paths.get(file.getAbsolutePath());
        try{
        	Path pathRelative = pathBase.relativize(pathAbsolute);
        	return pathRelative.toString();
        } catch (Exception e){
        	return resultIfImpossible;
        }
    }
    
    public static File getFileFromBaseFileAndPathThatMayBeRelative(File baseDir, String pathThatMayBeRelative) throws IOException{
    	if (Paths.get(pathThatMayBeRelative).isAbsolute()){
    		return new File(pathThatMayBeRelative);
    	} else {
    		return Paths.get(baseDir.getAbsolutePath(), pathThatMayBeRelative).toFile().getCanonicalFile();
    	}
    }

}
