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
        if (widget.getHeight() != null){
        	tagLogic.setAttribute(divTag, "style", "width:99%; height:" + widget.getHeight() + "px;");
        }
    }


}
