package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class CodeEditorWidgetHTMLGeneratorLogic {
    private HTMLTagLogic tagLogic = new HTMLTagLogic();

    public void generateHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, CodeEditorWidget widget) throws Exception {
    	String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(screenDefinition);
    	HTMLTag divTag = tagLogic.addTag(parentTag, "div");
        tagLogic.addTag(divTag, "textarea", "&lt;testFromGenerator attrib='7'&gt;Test&lt;/testFromGenerator&gt;", "id", screenIDPrefix + widget.getID(), "name",
        		screenIDPrefix + widget.getID());
        String style = "width:99%;height:99%;";
        if (widget.getHeight() != null){
        	style += "height:" + widget.getHeight() + "px;";
        }
        tagLogic.setAttribute(divTag, "style", style);
    }


}
