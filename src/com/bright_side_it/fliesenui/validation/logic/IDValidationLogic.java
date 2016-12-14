package com.bright_side_it.fliesenui.validation.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenTopElement;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class IDValidationLogic {
    private static final Set<Character> ALLOWED_CHARS_IN_IDS = createAllowedCharsInIDs();

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            validateScreenDefinition(project, screenDefinition);
        }
    }

    private static Set<Character> createAllowedCharsInIDs() {
        Set<Character> result = new HashSet<>();
        for (char i = 'a'; i <= 'z'; i++) {
            result.add(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            result.add(i);
        }
        for (char i = '0'; i <= '9'; i++) {
            result.add(i);
        }
        return result;
    }

    private void validateScreenDefinition(Project project, ScreenDefinition screenDefinition) {
        Set<String> knownIDs = new HashSet<String>();
        validateID(project, screenDefinition, knownIDs, screenDefinition.getNodePath(), screenDefinition.getID());
        if (screenDefinition.getDTODeclarations() != null) {
            for (DTODeclaration i : screenDefinition.getDTODeclarations().values()) {
                validateID(project, screenDefinition, knownIDs, i.getNodePath(), i.getID());
            }
        }
        for (ScreenTopElement i : screenDefinition.getTopElements()) {
            if (i instanceof LayoutContainer) {
                validateLayoutContainer(project, screenDefinition, knownIDs, (LayoutContainer) i);
            }
        }
    }

    private void validateLayoutContainer(Project project, ScreenDefinition screenDefinition, Set<String> knownIDs, LayoutContainer layoutContainer) {
    	validateID(project, screenDefinition, knownIDs, layoutContainer.getNodePath(), layoutContainer.getID());
        if (layoutContainer.getBars() != null) {
            for (LayoutBar i : layoutContainer.getBars()) {
                validateBar(project, screenDefinition, knownIDs, i);
            }
        }
    }

    private void validateBar(Project project, ScreenDefinition screenDefinition, Set<String> knownIDs, LayoutBar bar) {
    	validateID(project, screenDefinition, knownIDs, bar.getNodePath(), bar.getID());
        int totalSize = 0;
        if ((bar.getCells() == null) || (bar.getCells().isEmpty())){
            ValidationUtil.addError(project, screenDefinition, bar.getNodePath(), null, ProblemType.BAR_PROBLEM_EMPTY_BAR,
                    "A bar must contain at least one cell");
        	return;
        }
        for (LayoutCell i : bar.getCells()) {
            validateCell(project, screenDefinition, knownIDs, i);
            totalSize += i.getSize();
        }
        if (totalSize != 100) {
            ValidationUtil.addError(project, screenDefinition, bar.getNodePath(), null, ProblemType.BAR_PROBLEM_SUM_NOT_100,
                    "The sum of the cell sized in a bar must be 100 but it is " + totalSize);
        }
    }

    private void validateCell(Project project, ScreenDefinition screenDefinition, Set<String> knownIDs, LayoutCell layoutCell) {
    	validateID(project, screenDefinition, knownIDs, layoutCell.getNodePath(), layoutCell.getID());
        if (layoutCell.getCellItems() != null) {
            for (CellItem i : layoutCell.getCellItems()) {
                validateCellItem(project, screenDefinition, knownIDs, i);
            }
        }
    }

    private void validateCellItem(Project project, ScreenDefinition screenDefinition, Set<String> knownIDs, CellItem widget) {
    	if (!(widget instanceof LayoutContainer)) {
    		//: layout containers are validated separately which would lead to double validation
    		validateID(project, screenDefinition, knownIDs, widget.getNodePath(), widget.getID());
    	}

        if (widget instanceof BasicWidget) {
        } else if (widget instanceof LayoutContainer) {
            validateLayoutContainer(project, screenDefinition, knownIDs, (LayoutContainer) widget);
        } else if (widget instanceof TableWidget) {
            validateTableWidget(project, screenDefinition, (TableWidget) widget);
        }
    }

    private void validateTableWidget(Project project, ScreenDefinition screenDefinition, TableWidget widget) {
        if (widget.getColumns() == null) {
            return;
        }
        Set<String> knownIDsInTable = new TreeSet<>(); //: it is OK if an ID occurs in two different tables. Each ID may only occur once per table
        for (TableWidgetColumn i : widget.getColumns()) {
            validateTableWidgetColumn(project, screenDefinition, knownIDsInTable, i);
        }
    }

    private void validateTableWidgetColumn(Project project, ScreenDefinition screenDefinition, Set<String> knownIDs, TableWidgetColumn tableWidgetColumn) {
        for (TableWidgetItem i : BaseUtil.toEmptyCollectionIfNull(tableWidgetColumn.getTableItems())) {
            validateID(project, screenDefinition, knownIDs, i.getNodePath(), i.getID());
        }
    }

    private void validateID(Project project, ScreenDefinition screenDefinition, Set<String> knownIDs, NodePath nodePath, String id) {
        if (id == null) {
            return;
        }
        
        if (knownIDs.contains(id)) {
            ValidationUtil.addError(project, screenDefinition, nodePath, BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.ID_PROBLEM_ID_USED_MULTIPLE_TIMES,
                    "The ID '" + id + "' is used multiple times.");
            return;
        }
        knownIDs.add(id);

        if (id.isEmpty()) {
            ValidationUtil.addError(project, screenDefinition, nodePath, BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.ID_PROBLEM_ID_EMPTY, "The id may not be empty");
            return;
        }

        char firstChar = id.charAt(0);
        if ((firstChar < 'a') || (firstChar > 'z')) {
            ValidationUtil.addError(project, screenDefinition, nodePath, BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.ID_PROBLEM_ID_DOES_NOT_START_WITH_LOWER_CHAR,
                    "The id must start with a lowercase letter a-z. ID is '" + id + "'");
            return;
        }

        for (char i : id.toCharArray()) {
            if (!ALLOWED_CHARS_IN_IDS.contains(i)) {
                ValidationUtil.addError(project, screenDefinition, nodePath, BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.ID_PROBLEM_ID_CONTAINS_WRONG_CHAR,
                        "The id contains the wrong character '" + i + "'. Only a-z, A-Z and 0-9 are allowed");
                return;
            }
        }
    }

    @SuppressWarnings("unused")
	private void log(String message) {
    	System.out.println("IDValidationLogic> " + message);
	}

	public boolean isValidID(String id) {
        char firstChar = id.charAt(0);
        if ((firstChar < 'a') || (firstChar > 'z')) {
            return false;
        }

        for (char i : id.toCharArray()) {
            if (!ALLOWED_CHARS_IN_IDS.contains(i)) {
                return false;
            }
        }
        return true;
    }

}
