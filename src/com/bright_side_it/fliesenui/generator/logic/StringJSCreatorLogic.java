package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.stringres.model.StringResource;
import com.bright_side_it.fliesenui.stringres.model.StringResourceItem;
import com.google.gson.Gson;

public class StringJSCreatorLogic {
	public void generateStringJS(Project project, File dir) throws Exception {
		Map<String, String> map = new TreeMap<String, String>();
		
		for (Entry<String, StringResource> stringResource : project.getStringResourceMap().entrySet()){
			for (Entry<String, StringResourceItem> item : stringResource.getValue().getStrings().entrySet()){
				map.put(stringResource.getKey() + ":" + BaseUtil.toJSStringID(item.getKey()), item.getValue().getString());
			}
		}
		
		File fileName = new File(dir, GeneratorConstants.STRINGS_JS_FILENAME);
		FileUtil.writeStringToFile(fileName, "STRINGS = " + new Gson().toJson(map) + ";");
	}
}
