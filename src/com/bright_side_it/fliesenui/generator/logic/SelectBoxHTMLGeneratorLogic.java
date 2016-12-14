package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;

public class SelectBoxHTMLGeneratorLogic {
	private HTMLTagLogic tagLogic = new HTMLTagLogic();

	public void generateHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, SelectBox selectBox, BrowserType browserType) throws Exception {
		String model = GeneratorUtil.getJSSelectBoxSelectedItemVariableName(screenDefinition, selectBox);
		String changeMethod = GeneratorUtil.getJSSelectBoxChangeMethodName(screenDefinition, selectBox) + "(" + model + "." + selectBox.getIDDTOField() + ")";
		tagLogic.addTag(parentTag, "select", "", "ng-model", model, "ng-options", "i." + selectBox.getLabelDTOField() + " for i in " + selectBox.getDTO(), "ng-change", changeMethod);
	}
}
