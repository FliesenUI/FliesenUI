package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class CodeEditorWidgetValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, CodeEditorWidget widget) {
    }


}
