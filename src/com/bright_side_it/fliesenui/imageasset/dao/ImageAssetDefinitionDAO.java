package com.bright_side_it.fliesenui.imageasset.dao;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.nio.file.Files;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.dao.DefinitionResourceDAO;

public class ImageAssetDefinitionDAO {

    public ImageAssetDefinition readImageAssetDefinition(File file) throws Exception {
    	if (file.getName().equals(BaseConstants.IMAGE_ASSET_FILE_TO_IGNORE)){ //: Windows creates a file "Thumbs.db" automatically which can be ignored
    		return null;
    	}
    	
        ImageAssetDefinition result = new ImageAssetDefinition();
        result.setID(FileUtil.getFilenameWithoutEnding(file.getName()));
        result.setFilename(file.getName());
//        result.setFormatType(readFormatType(FileUtil.getFilenameEnding(file.getName())));

        return result;
    }

//    private ImageFormatType readFormatType(String ending) throws Exception {
//        if (ending.equalsIgnoreCase(".png")) {
//            return ImageFormatType.PIXEL;
//        } else if (ending.equalsIgnoreCase(".jpg")) {
//            return ImageFormatType.PIXEL;
//        } else if (ending.equalsIgnoreCase(".jpeg")) {
//            return ImageFormatType.PIXEL;
//        } else if (ending.equalsIgnoreCase(".png")) {
//            return ImageFormatType.PIXEL;
//        } else if (ending.equalsIgnoreCase(".svg")) {
//            return ImageFormatType.VECTOR;
//        }
//        throw new Exception("Unknown file type: '" + ending + "'");
//    }

    public void copyToDir(File projectDir, ImageAssetDefinition image, File destDir) throws Exception {
        DefinitionResourceDAO definitionResourceDAO = new DefinitionResourceDAO();
        File source = definitionResourceDAO.getImageAssetFile(projectDir, image);
        File target = new File(destDir, image.getFilename());
        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
    }


}
