package com.bright_side_it.fliesenui.validation.logic;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.EventHandlerDAO;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandler;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandlerContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class EventHandlerValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            validate(project, screenDefinition);
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition) {
        SortedSet<String> dtoNames = readDTOIDs(screenDefinition);
        SortedMap<String, CellItem> widgetIDs = readWidgetIDMap(screenDefinition);

        for (EventHandlerContainer handlerContainer : BaseUtil.getAllEventHandlerContainers(screenDefinition)) {
            if (handlerContainer.getEventHandlers() != null) {
                for (EventHandler handler : handlerContainer.getEventHandlers()) {
                    validate(project, screenDefinition, handlerContainer, handler, dtoNames, widgetIDs);
                }
            }
        }
    }

    private SortedSet<String> readDTOIDs(ScreenDefinition screenDefinition) {
        SortedSet<String> result = new TreeSet<>();
        if (screenDefinition.getDTODeclarations() != null) {
            result = new TreeSet<>(screenDefinition.getDTODeclarations().keySet());
        }
        return result;
    }

    private SortedMap<String, CellItem> readWidgetIDMap(ScreenDefinition screenDefinition) {
        SortedMap<String, CellItem> result = new TreeMap<>();
        for (BasicWidget i : BaseUtil.getAllBasicWidgets(screenDefinition)) {
            if (i.getID() != null) {
                result.put(i.getID(), i);
            }
        }
        for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
            if (i.getID() != null) {
                result.put(i.getID(), i);
            }
        }
        return result;
    }

    private void validate(Project project, ScreenDefinition screenDefinition, EventHandlerContainer handlerContainer, EventHandler handler,
            SortedSet<String> dtoNames, SortedMap<String, CellItem> widgets) {
    	
    	if (!(handlerContainer instanceof BasicWidget)){
			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), null, ProblemType.EVENT_HANDLER_UNKNOWN_CONTAINER,
					"This event handler has an unexpected container: " + handlerContainer.getClass().getSimpleName());
			return;
    	}
    	BasicWidget basicWidget = (BasicWidget)handlerContainer;
    	if (!BaseUtil.in(basicWidget.getType(), BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON)){
			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), null, ProblemType.EVENT_CONTAINER_NOT_A_BUTTON,
					"The event handler must be part of a button");
    	}
    	
    	
    	if ((handler.getOpenScreenParameterDTO() != null) && (handler.getScreenToOpen() == null)){
			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), EventHandlerDAO.PROPERTY_NAME_OPEN_SCREEN_PARAMETER_DTO, ProblemType.EVENT_HANDLER_SCREEN_PARAMETER_DTO_WITHOUT_SCREEN_PARAMETER,
					"If you specify a parameter DTO to open with a screen you must also specify the screen with property " + EventHandlerDAO.PROPERTY_NAME_SCREEN_TO_OPEN);
    	}
    	
    	if (handler.getScreenToOpen() != null){
    		if (handler.getURLToOpen() != null){
    			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), EventHandlerDAO.PROPERTY_NAME_SCREEN_TO_OPEN, ProblemType.EVENT_HANDLER_SCREEN_TO_OPEN_AND_URL_TO_OPEN,
    					"Either a screen or a URL can be opened, but no both");
    		}
    		
    		ScreenDefinition screenToOpen = project.getScreenDefinitionsMap().get(handler.getScreenToOpen());
    		if (screenToOpen == null){
    			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), EventHandlerDAO.PROPERTY_NAME_SCREEN_TO_OPEN, ProblemType.EVENT_HANDLER_SCREEN_TO_OPEN_UNKNOWN,
    					"There is no screen with ID '" + handler.getScreenToOpen() + "'");
    		} else {
    			if (screenToOpen.getParameterDTOID() != null){
    				if (handler.getOpenScreenParameterDTO() == null){
    	    			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), EventHandlerDAO.PROPERTY_NAME_SCREEN_TO_OPEN, ProblemType.EVENT_HANDLER_MISSING_PARAMETER_DTO,
    	    					"The screen '" + handler.getScreenToOpen() + "' that is supposed to be opened needs a parameter DTO that is set by property '" + EventHandlerDAO.PROPERTY_NAME_OPEN_SCREEN_PARAMETER_DTO + "'");
    				} else {
    					DTODeclaration openScreenExistingDTODeclaration = screenToOpen.getDTODeclarations().get(screenToOpen.getParameterDTOID());
    					DTODeclaration openScreenProvidedDTODeclaration = screenDefinition.getDTODeclarations().get(handler.getOpenScreenParameterDTO());
    					if (!openScreenExistingDTODeclaration.getType().equals(openScreenProvidedDTODeclaration.getType())){
        	    			ValidationUtil.addError(project, screenDefinition, handlerContainer.getNodePath(), EventHandlerDAO.PROPERTY_NAME_SCREEN_TO_OPEN, ProblemType.EVENT_HANDLER_WRONG_PARAMETER_DTO_TYPE,
        	    					"The screen '" + handler.getScreenToOpen() + "' that is supposed to be opened needs a parameter DTO of tyoe '" + openScreenExistingDTODeclaration.getType() + "', but type '" + openScreenProvidedDTODeclaration.getType() + "' was provided");
    					}
    				}
    			}
    		}
    	} 
    }


}
