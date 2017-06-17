package com.bright_side_it.fliesenui.validation.logic;

import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.LayoutBarDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.LayoutContainerDAO;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar.Position;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer.Orientation;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutContainerValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (LayoutContainer i : BaseUtil.getAllLayoutContainers(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
    	validateIDsNeededForVisibility(project, screenDefinition, container);
    	validateBorderLayoutOnlyForTopContainer(project, screenDefinition, container);
    	validateBorderLayoutMissingSizes(project, screenDefinition, container);
    	validateBarsInBorderLayoutNoDuplicatePositions(project, screenDefinition, container);
    	validateBarsInBorderLayoutSizeSpecified(project, screenDefinition, container);
    }

	private void validateBarsInBorderLayoutSizeSpecified(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
		if (container.getOrientation() != Orientation.BORDER_LAYOUT){
			return;
		}
		for (LayoutBar i: BaseUtil.toEmptyCollectionIfNull(container.getBars())){
			if ((i.getPosition() == Position.TOP) && (container.getTopSizeInCM() == null)){
				ValidationUtil.addError(project, screenDefinition, container.getNodePath(), null,
						ProblemType.LAYOUT_CONTAINER_MISSING_TOP_SIZE, "There already is a bar with position top, but the top size has not been specified");
			}
			if ((i.getPosition() == Position.BOTTOM) && (container.getBottomSizeInCM() == null)){
				ValidationUtil.addError(project, screenDefinition, container.getNodePath(), null,
						ProblemType.LAYOUT_CONTAINER_MISSING_BOTTOM_SIZE, "There already is a bar with position bottom, but the bottom size has not been specified");
			}
			if ((i.getPosition() == Position.LEFT) && (container.getLeftSizeInCM() == null)){
				ValidationUtil.addError(project, screenDefinition, container.getNodePath(), null,
						ProblemType.LAYOUT_CONTAINER_MISSING_LEFT_SIZE, "There already is a bar with position left, but the left size has not been specified");
			}
			if ((i.getPosition() == Position.RIGHT) && (container.getRightSizeInCM() == null)){
				ValidationUtil.addError(project, screenDefinition, container.getNodePath(), null,
						ProblemType.LAYOUT_CONTAINER_MISSING_RIGHT_SIZE, "There already is a bar with position right, but the right size has not been specified");
			}
		}
	}

	private void validateBarsInBorderLayoutNoDuplicatePositions(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
		if (container.getOrientation() != Orientation.BORDER_LAYOUT){
			return;
		}
		Set<Position> usedPositions = new TreeSet<>();
		for (LayoutBar i: BaseUtil.toEmptyCollectionIfNull(container.getBars())){
			if (i.getPosition() != null){
				if (usedPositions.contains(i.getPosition())){
					ValidationUtil.addError(project, screenDefinition, i.getNodePath(), null,
							ProblemType.LAYOUT_BAR_DUBLICATE_POSITION, "There already is a bar with position " + i.getPosition());
				}
				usedPositions.add(i.getPosition());
			}
		}
	}

	private void validateBorderLayoutMissingSizes(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
		if (container.getOrientation() != Orientation.BORDER_LAYOUT){
			return;
		}
		if ((container.getLeftSizeInCM() == null) && (container.getRightSizeInCM() == null) && (container.getTopSizeInCM() == null) && (container.getBottomSizeInCM() == null)){
			ValidationUtil.addError(project, screenDefinition, container.getNodePath(), null,
					ProblemType.LAYOUT_CONTAINER_BORDER_LAYOUT_WITHOUT_SIZES, "If border layout is chosen, at least one of the sizes must be specified: "
					 + LayoutContainerDAO.LEFT_SIZE_ATTRIBUTE_VALUE + ", " + LayoutContainerDAO.RIGHT_SIZE_ATTRIBUTE_VALUE + ", " + LayoutContainerDAO.TOP_SIZE_ATTRIBUTE_VALUE
					 + ", " + LayoutContainerDAO.BOTTOM_SIZE_ATTRIBUTE_VALUE);
		}
		
	}

	private void validateBorderLayoutOnlyForTopContainer(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
		if ((container.getOrientation() == Orientation.BORDER_LAYOUT) && (!container.isTopContainer())){
			ValidationUtil.addError(project, screenDefinition, container.getNodePath(), LayoutContainerDAO.ORIENTATION_ATTRIBUTE_NAME,
					ProblemType.LAYOUT_CONTAINER_BORDER_LAYOUT_IN_SUB_CONTAINER, "The border layout may only be used in the top container");
		}
	}

	private void validateIDsNeededForVisibility(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
		if ((!container.isVisible()) && (container.getID() == null)){
			ValidationUtil.addError(project, screenDefinition, container.getNodePath(), LayoutContainerDAO.VISIBLE_ATTRIBUTE_NAME,
					ProblemType.LAYOUT_CONTAINER_INVISIBLE_WITHOUT_ID, "If the layot container is made invisible, it needs to have an ID");
		}
	}
}
