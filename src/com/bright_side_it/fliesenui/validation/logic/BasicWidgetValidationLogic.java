package com.bright_side_it.fliesenui.validation.logic;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.in;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.BUTTON;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.CHECKBOX;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.IMAGE;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.IMAGE_BUTTON;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.LABEL;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.PROGRESS_BAR;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.SPACE;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.SWITCH;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.TEXT_AREA;
import static com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType.TEXT_FIELD;

import java.util.Arrays;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.BasicWidgetDAO;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.Style;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class BasicWidgetValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (BasicWidget i : BaseUtil.getAllBasicWidgets(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, BasicWidget widget) {
        if (widget.getType() == null) {
            ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), "", ProblemType.BASIC_WIDGET_UNKNOWN_TYPE, "Unknonwn widget type: null");
            return;
        }

        switch (widget.getType()) {
        case BUTTON:
            validateButton(project, screenDefinition, widget);
            break;
        case LABEL:
            validateLabel(project, screenDefinition, widget);
            break;
        case SPACE:
            validateSpace(project, screenDefinition, widget);
            break;
        default:
            break;
        }

        new ImageSourceValidator().validate(project, screenDefinition, widget.getNodePath(), widget);

        validateAttributeMustExists(project, screenDefinition, widget, BasicWidgetDAO.TEXT_ATTRIBUTE_NAME, widget.getText(), BUTTON, LABEL, TEXT_FIELD, TEXT_AREA, CHECKBOX, SWITCH);
        validateAttributeMayNotExists(project, screenDefinition, widget, BasicWidgetDAO.TEXT_ATTRIBUTE_NAME, widget.getText(), PROGRESS_BAR, SPACE, IMAGE, IMAGE_BUTTON);

        validateAttributeMayNotExists(project, screenDefinition, widget, BasicWidgetDAO.LABEL_TEXT_ATTRIBUTE_NAME, widget.getLabelText(), IMAGE_BUTTON, BUTTON, LABEL,
                PROGRESS_BAR, SPACE, IMAGE, CHECKBOX, SWITCH);

        validateAttributeMustExists(project, screenDefinition, widget, BaseConstants.ID_ATTRIBUTE_NAME, widget.getID(), IMAGE_BUTTON, BUTTON, TEXT_FIELD,
                TEXT_AREA, LABEL, PROGRESS_BAR, CHECKBOX, SWITCH);

        validateAttributeMayNotExists(project, screenDefinition, widget, BasicWidgetDAO.HEIGHT_ATTRIBUTE_NAME, widget.getHeight(), IMAGE_BUTTON, BUTTON, LABEL,
                TEXT_FIELD, PROGRESS_BAR, IMAGE, CHECKBOX, SWITCH);

        validateAttributeMayNotExists(project, screenDefinition, widget, BasicWidgetDAO.STYLE_ATTRIBUTE_NAME, widget.getStyle(), IMAGE_BUTTON, IMAGE, TEXT_FIELD,
                TEXT_AREA, PROGRESS_BAR, SPACE, CHECKBOX, SWITCH);

        validateAttributeMayNotExists(project, screenDefinition, widget, BasicWidgetDAO.READ_ONLY_ATTRIBUTE_NAME, widget.isReadOnly(), LABEL, BUTTON, IMAGE_BUTTON, IMAGE
        		, PROGRESS_BAR, SPACE, CHECKBOX, SWITCH);

        validateAttributeMayNotExists(project, screenDefinition, widget, BasicWidgetDAO.SCROLL_TO_BOTTOM_ATTRIBUTE_NAME, widget.getScrollToBottom(), LABEL, BUTTON, IMAGE_BUTTON, IMAGE
        		, PROGRESS_BAR, SPACE, CHECKBOX, SWITCH, TEXT_FIELD);
        
        validateAttributeMayNotExists(project, screenDefinition, widget, null, widget.getEventParameters(), LABEL, TEXT_FIELD, TEXT_AREA, PROGRESS_BAR, SPACE, IMAGE, CHECKBOX, SWITCH);


        validateAttributeMayNotExists(project, screenDefinition, widget, null, widget.getImageSource(), TEXT_FIELD, TEXT_AREA, PROGRESS_BAR, SPACE, CHECKBOX, SWITCH);
        validateAttributeMustExists(project, screenDefinition, widget, null, widget.getImageSource(), IMAGE_BUTTON, IMAGE);

        if ((widget.getImageSource() != null) && (widget.getID() == null)){
        	ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.BASIC_WIDGET_WITH_IMAGE_SOURCE_IS_MISSING_ID,
                    "If the widget has an image source it also needs to have an ID");
        }
        
        if (widget.getTextDTOField() != null){
        	if (!ValidationUtil.doesDTOFieldExist(project, screenDefinition, widget.getTextDTOField())){
                ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.TEXT_DTO_FIELD_ATTRIBUTE_NAME, ProblemType.BASIC_WIDGET_TEXT_DTO_FIELD_DOES_NOT_EXIST,
                        "No such DTO field: '" + widget.getTextDTOField() + "'");
        	}
        }
        
        if (!ValidationUtil.isTextOrTextResourceValid(project, widget.getLabelText())){
            ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.LABEL_TEXT_ATTRIBUTE_NAME, ProblemType.BASIC_WIDGET_LABEL_TEXT_STRING_RESOURCE_DOES_NOT_EXIST,
                    "The string resource does not exist");
        }
        if (!ValidationUtil.isTextOrTextResourceValid(project, widget.getText())){
            ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.TEXT_ATTRIBUTE_NAME, ProblemType.BASIC_WIDGET_TEXT_STRING_RESOURCE_DOES_NOT_EXIST,
                    "The string resource does not exist");
        }
        
    }
    
    private void validateAttributeMustExists(Project project, ScreenDefinition screenDefinition, BasicWidget widget, String attributeName, Object value,
            BasicWidgetType... types) {
        if (Arrays.asList(types).contains(widget.getType())) {
            if (value == null) {
                ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), attributeName, ProblemType.BASIC_WIDGET_MISSING_ATTRIBUTE,
                        "Attribute value missing for basic widget of type " + widget.getType() + ": " + attributeName);

            }
        }
    }

    private void validateAttributeMayNotExists(Project project, ScreenDefinition screenDefinition, BasicWidget widget, String attributeName, Object value,
            BasicWidgetType... types) {
        if (Arrays.asList(types).contains(widget.getType())) {
            if (value != null) {
                ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), attributeName, ProblemType.BASIC_WIDGET_UNEXPECTED_ATTRIBUTE,
                        "Attribute may not be used for basic widget: " + attributeName + ". value = '" + value + "'");

            }
        }
    }

    @SuppressWarnings("unused")
    private void log(String message) {
        System.out.println("BasicWidgetValidationLogic> " + message);
    }


    private void validateSpace(Project project, ScreenDefinition screenDefinition, BasicWidget widget) {
        if (widget.getHeight() == null) {
            ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.HEIGHT_ATTRIBUTE_NAME, ProblemType.SPACE_WIDGET_MISSING_HEIGHT,
                    "Space widget needs to have height value");
        } else if (widget.getHeight() <= 0) {
            ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.HEIGHT_ATTRIBUTE_NAME, ProblemType.SPACE_WIDGET_HEIGHT_TOO_SMALL,
                    "Space widget height needs to be > 0");
        }
    }

    private void validateButton(Project project, ScreenDefinition screenDefinition, BasicWidget widget) {
        if (widget.getStyle() != null) {
            if (!in(widget.getStyle(), Style.NORMAL, Style.SMALL, Style.TINY)) {
                ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.STYLE_ATTRIBUTE_NAME, ProblemType.BUTTON_WIDGET_UNEXPECTED_STYLE,
                        "This style is not possible for buttons");
            }
        }
    }

    private void validateLabel(Project project, ScreenDefinition screenDefinition, BasicWidget widget) {
        if (widget.getStyle() != null) {
            if (!in(widget.getStyle(), Style.NORMAL, Style.SMALL, Style.MEDIUM, Style.LARGE)) {
                ValidationUtil.addError(project, screenDefinition, widget.getNodePath(), BasicWidgetDAO.STYLE_ATTRIBUTE_NAME, ProblemType.LABEL_WIDGET_UNEXPECTED_STYLE,
                        "This style is not possible for labels");
            }
        }

    }

}
