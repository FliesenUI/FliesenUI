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
import com.bright_side_it.fliesenui.screendefinition.logic.UnitValueLogic;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell.AlignType;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell.CellStyle;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.screendefinition.model.UnitValue.Unit;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutCellDAO {
    public static final String SIZE_ATTRIBUTE_NAME = "size";
    public static final String HEIGHT_ATTRIBUTE_NAME = "height";
    public static final String VISIBLE_ATTRIBUTE_NAME = "visible";
    public static final String CONTENT_ALIGN_ATTRIBUTE_NAME = "contentAlign";
    public static final String CELL_STYLE_ATTRIBUTE_NAME = "style";
    public static final String BACKGROUND_COLOR_ATTRIBUTE_NAME = "backgroundColor";
    public static final String HEADLINE_TEXT_ATTRIBUTE_NAME = "headline";
    public static final String SUBHEAD_TEXT_ATTRIBUTE_NAME = "subhead";

    private static final String LAYOUT_CELL_NODE_NAME = "cell";
    
    private static final String CONTENT_ALIGN_TEXT_LEFT = "left";
    private static final String CONTENT_ALIGN_TEXT_RIGHT = "right";
    private static final String CONTENT_ALIGN_TEXT_CENTER = "center";
    private static final String CONTENT_ALIGN_TEXT_DEFAULT = "default";

    private static final String CELL_STYLE_NONE = "none";
    private static final String CELL_STYLE_CARD = "card";
    
    public void readLayoutCell(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutBar layoutBar) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, LAYOUT_CELL_NODE_NAME, nodePath, result)) {
            return;
        }
        if (layoutBar.getCells() == null) {
            layoutBar.setCells(new ArrayList<>());
        }
        LayoutCell layoutCell = new LayoutCell();
        layoutCell.setNodePath(nodePath);
        layoutBar.getCells().add(layoutCell);

        int size = XMLUtil.getIntAttributeRequired(node, SIZE_ATTRIBUTE_NAME);
        layoutCell.setSize(size);
        layoutCell.setHeight(new UnitValueLogic().parse(XMLUtil.getStringAttributeOptional(node, HEIGHT_ATTRIBUTE_NAME, null), Unit.PIXEL, null));
        layoutCell.setBackgroundColor(XMLUtil.getStringAttributeOptional(node, BACKGROUND_COLOR_ATTRIBUTE_NAME, null));
        layoutCell.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        layoutCell.setVisible(XMLUtil.getBooleanAttributeOptional(node, VISIBLE_ATTRIBUTE_NAME, true));
        layoutCell.setHeadlineText(XMLUtil.getStringAttributeOptional(node, HEADLINE_TEXT_ATTRIBUTE_NAME, null));
        layoutCell.setSubheadText(XMLUtil.getStringAttributeOptional(node, SUBHEAD_TEXT_ATTRIBUTE_NAME, null));
        
        String contentAlignText = XMLUtil.getStringAttributeOptional(node, CONTENT_ALIGN_ATTRIBUTE_NAME, null);
        if (contentAlignText != null){
        	log("contentAlignText = '" + contentAlignText + "'");
        	layoutCell.setContentAlign(parseContentAlignText(contentAlignText));
        }
        String cellStyleText = XMLUtil.getStringAttributeOptional(node, CELL_STYLE_ATTRIBUTE_NAME, null);
        if (cellStyleText != null){
        	log("cellStyleText = '" + cellStyleText + "'");
        	layoutCell.setCellStyle(parseCellStyleText(cellStyleText));
        }

        BasicWidgetDAO widgetDAO = new BasicWidgetDAO();
        CodeEditorWidgetDAO codeEditorDAO = new CodeEditorWidgetDAO();
        TableWidgetDAO tableWidgetDAO = new TableWidgetDAO();
        SelectBoxDAO selectBoxDAO = new SelectBoxDAO();
        LayoutContainerDAO layoutContainerDAO = new LayoutContainerDAO();
        PluginInstanceDAO pluginInstanceDAO = new PluginInstanceDAO();

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                if (widgetDAO.isWidgetNode(i)) {
                    widgetDAO.readWidget(i, childNodePath, result, layoutCell);
                } else if (tableWidgetDAO.isTableWidgetNode(i)) {
                    tableWidgetDAO.readTableWidget(i, childNodePath, result, layoutCell);
                } else if (selectBoxDAO.isSelectBoxNode(i)) {
                	selectBoxDAO.readSelectBox(i, childNodePath, result, layoutCell);
                } else if (layoutContainerDAO.isLayoutContainerNode(i)) {
                    layoutContainerDAO.readLayoutContainer(i, childNodePath, result, layoutCell);
                } else if (pluginInstanceDAO.isPluginDeclarationNode(i)) {
                    pluginInstanceDAO.readPluginInstance(i, childNodePath, result, layoutCell);
                } else if (codeEditorDAO.isCodeEditorNode(i)) {
                    codeEditorDAO.readCodeEditorWidget(i, childNodePath, result, layoutCell);
                } else {
                    throw new Exception("Unexpected node in cell node: '" + i.getNodeName() + "'");
                }
            } catch (ScreenDefionitionReadException e) {
                ScreenDefinitionDAO.addError(result, e);
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }

        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    private void log(String message) {
    	System.out.println("LayoutCellDAO> " + message);
	}

	private AlignType parseContentAlignText(String contentAlignText) throws Exception {
    	if (CONTENT_ALIGN_TEXT_LEFT.equals(contentAlignText)){
    		return AlignType.LEFT;
    	} else if (CONTENT_ALIGN_TEXT_RIGHT.equals(contentAlignText)){
    		return AlignType.RIGHT;
    	} else if (CONTENT_ALIGN_TEXT_CENTER.equals(contentAlignText)){
    		return AlignType.CENTER;
    	} else if (CONTENT_ALIGN_TEXT_DEFAULT.equals(contentAlignText)){
    		return null;
    	} else {
    		throw new Exception("Unknown align value: '" + contentAlignText + "'");
    	}
	}

	private CellStyle parseCellStyleText(String cellStyleText) throws Exception {
		if (CELL_STYLE_CARD.equals(cellStyleText)){
			return CellStyle.CARD;
		} else if (CELL_STYLE_NONE.equals(cellStyleText)){
			return null;
		} else {
			throw new Exception("Unknown cell style value: '" + cellStyleText + "'");
		}
	}
	
	public static String getNodeName() {
        return LAYOUT_CELL_NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.addAll(BasicWidgetDAO.createAssistValues());
        result.add(BaseUtil.createAssistValue(null, TableWidgetDAO.getNodeName(), "table"));
        result.add(BaseUtil.createAssistValue(null, SelectBoxDAO.getNodeName(), "select box"));
        result.add(BaseUtil.createAssistValue(null, CodeEditorWidgetDAO.NODE_NAME_CODE_EDITOR, "code editor"));
        result.add(BaseUtil.createAssistValue(null, CodeEditorWidgetDAO.NODE_NAME_TEXT_EDITOR, "text editor"));
        result.add(BaseUtil.createAssistValue(null, LayoutContainerDAO.getNodeName(), "nested layout container"));
        result.add(BaseUtil.createAssistValue(null, PluginInstanceDAO.getNodeName(), "plugin (defined in separate file)"));

        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, SIZE_ATTRIBUTE_NAME,
                "number between 1 and 100 that specifies the size in percent. The sum of all cell sizes in a bar must be 100"));
        result.add(BaseUtil.createAssistValue(false, HEIGHT_ATTRIBUTE_NAME, "height of the cell in pixels."));
        result.add(BaseUtil.createAssistValue(false, BACKGROUND_COLOR_ATTRIBUTE_NAME, "background color of the cel in the hex format (example: #ff0000)."));
        result.add(BaseUtil.createAssistValue(false, BaseConstants.ID_ATTRIBUTE_NAME, "ID. If the ID is set the visibility of the cell may be changed"));
        result.add(BaseUtil.createAssistValue(false, VISIBLE_ATTRIBUTE_NAME, "visibility"));
        result.add(BaseUtil.createAssistValue(false, CONTENT_ALIGN_ATTRIBUTE_NAME, "define how the content should be aligned within the cell"));
        result.add(BaseUtil.createAssistValue(false, CELL_STYLE_ATTRIBUTE_NAME, "style of the cell (e.g. card)"));
        result.add(BaseUtil.createAssistValue(false, HEADLINE_TEXT_ATTRIBUTE_NAME, "headline of cell"));
        result.add(BaseUtil.createAssistValue(false, SUBHEAD_TEXT_ATTRIBUTE_NAME, "sub headline of the cell"));
        return result;
    }
    
    public AssistValueListProvider createPossibleContentAlignAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, CONTENT_ALIGN_TEXT_LEFT, "align left"));
        assistValues.add(BaseUtil.createAssistValue(null, CONTENT_ALIGN_TEXT_RIGHT, "align right"));
        assistValues.add(BaseUtil.createAssistValue(null, CONTENT_ALIGN_TEXT_CENTER, "center"));
        assistValues.add(BaseUtil.createAssistValue(null, CONTENT_ALIGN_TEXT_DEFAULT, "default alignment"));
        return new AssistValueList(assistValues);
    }

    public AssistValueListProvider createPossibleCellStyleAttributeValues() {
    	List<AssistValue> assistValues = new ArrayList<AssistValue>();
    	assistValues.add(BaseUtil.createAssistValue(null, CELL_STYLE_NONE, "none (no decoractions)"));
    	assistValues.add(BaseUtil.createAssistValue(null, CELL_STYLE_CARD, "card"));
    	return new AssistValueList(assistValues);
    }
    

}
