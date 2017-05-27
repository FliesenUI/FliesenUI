package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget.Style;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class TableWidgetDAO {
    public static final String DTO_ATTRIBUTE_NAME = "dto";
    public static final String CONTENT_HEIGHT_ATTRIBUTE_NAME = "contentHeight";
    public static final String ID_DTO_FIELD_ATTRIBUTE_NAME = "idDTOField";
    public static final String BACKGROUND_COLOR_DTO_FIELD_ATTRIBUTE_NAME = "backgroundColorDTOField";
    public static final String STYLE_FIELD_ATTRIBUTE_NAME = "style";
    public static final String SHOW_COLUMN_HEADERS_ATTRIBUTE_NAME = "showColumnHeaders";
    public static final String SHOW_FILTER_ATTRIBUTE_NAME = "showFilter";
    public static final String ROW_CHECKBOXES_ATTRIBUTE_NAME = "rowCheckboxes";

    private static final String NODE_NAME = "table";
    private static final String STYLE_VALUE_NORMAL = "normal";
    private static final String STYLE_VALUE_SMALL = "small";

    public boolean isTableWidgetNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readTableWidget(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        TableWidget tableWidget = new TableWidget();
        if (layoutCell.getCellItems() == null) {
            layoutCell.setCellItems(new ArrayList<>());
        }
        layoutCell.getCellItems().add(tableWidget);

        tableWidget.setNodePath(nodePath);
        tableWidget.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        tableWidget.setDTO(XMLUtil.getStringAttributeRequired(node, DTO_ATTRIBUTE_NAME));
        tableWidget.setIDDTOField(XMLUtil.getStringAttributeRequired(node, ID_DTO_FIELD_ATTRIBUTE_NAME));
        tableWidget.setBackgroundColorDTOField(XMLUtil.getStringAttributeOptional(node, BACKGROUND_COLOR_DTO_FIELD_ATTRIBUTE_NAME, null));
        tableWidget.setContentHeight(XMLUtil.getIntegerAttributeOptional(node, CONTENT_HEIGHT_ATTRIBUTE_NAME, null));
        tableWidget.setStyle(parseStyle(XMLUtil.getStringAttributeOptional(node, STYLE_FIELD_ATTRIBUTE_NAME, STYLE_VALUE_NORMAL)));
        tableWidget.setShowColumnHeader(XMLUtil.getBooleanAttributeOptional(node, SHOW_COLUMN_HEADERS_ATTRIBUTE_NAME, true));
        tableWidget.setShowFilter(XMLUtil.getBooleanAttributeOptional(node, SHOW_FILTER_ATTRIBUTE_NAME, false));
        tableWidget.setRowCheckboxes(XMLUtil.getBooleanAttributeOptional(node, ROW_CHECKBOXES_ATTRIBUTE_NAME, false));
        
        TableWidgetColumnDAO tableWidgetColumnDAO = new TableWidgetColumnDAO();
        EventParameterDAO eventParameterDAO = new EventParameterDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                if (eventParameterDAO.isEventParameterNode(i)) {
                    eventParameterDAO.readEventParameter(i, childNodePath, result, tableWidget);
                } else if (tableWidgetColumnDAO.isTableWidgetColumnNode(i)) {
                    tableWidgetColumnDAO.readTableWidgetColumn(i, childNodePath, result, tableWidget);
                } else {
                    throw new Exception("Unexpected node:" + i.getNodeName());
                }
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    private Style parseStyle(String string) throws Exception {
        if (STYLE_VALUE_NORMAL.equals(string)) {
            return Style.NORMAL;
        } else if (STYLE_VALUE_SMALL.equals(string)) {
            return Style.SMALL;
        } else {
            throw new Exception("Unknown mode: '" + string + "'");
        }
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, TableWidgetColumnDAO.getNodeName(), "Column"));
        result.add(BaseUtil.createAssistValue(null, EventParameterDAO.getNodeName(), "Event parameter that is passed when a table row is clicked"));
        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();

        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "ID of the table"));
        result.add(BaseUtil.createAssistValue(true, DTO_ATTRIBUTE_NAME,
                "name of the DTO or field in a DTO that contains a list of items which are used as the data for each row"));
        result.add(BaseUtil.createAssistValue(true, ID_DTO_FIELD_ATTRIBUTE_NAME,
                "Field of the DTO that contains an ID that can be passed to the methods that are called if a button or row is clicked to identify a row"));
        result.add(BaseUtil.createAssistValue(false, CONTENT_HEIGHT_ATTRIBUTE_NAME,
                "is specified the height of the table content will be set to this value (in pixels) and scroll bars will be shown"));
        result.add(BaseUtil.createAssistValue(false, STYLE_FIELD_ATTRIBUTE_NAME, "style of the table (" + STYLE_VALUE_NORMAL + " / " + STYLE_VALUE_SMALL + ")"));
        result.add(BaseUtil.createAssistValue(false, SHOW_COLUMN_HEADERS_ATTRIBUTE_NAME, "if set to false the column headers will be hidden"));
        result.add(BaseUtil.createAssistValue(false, ROW_CHECKBOXES_ATTRIBUTE_NAME, "makes each row selectable by a checkbox"));
        result.add(BaseUtil.createAssistValue(false, SHOW_FILTER_ATTRIBUTE_NAME, "if set to true a filter will be shown"));
        result.add(BaseUtil.createAssistValue(false, BACKGROUND_COLOR_DTO_FIELD_ATTRIBUTE_NAME,
        		"Field of the DTO that contains a string (e.g. '#ff0000') that defines the background color of the row"));
        return result;
    }

    public static AssistValueListProvider getPossibleModeAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NORMAL, "normal table size"));
        assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_SMALL, "small table size"));
        return new AssistValueList(assistValues);
    }

	public static AssistValueListProvider getPossibleShowColumnHeadersAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + true, "show column headers (defaul)"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + false, "hide column headers"));
        return new AssistValueList(assistValues);
	}

}
