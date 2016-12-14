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
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class SelectBoxDAO {
    private static final String NODE_NAME = "selectBox";
    public static final String DTO_ATTRIBUTE_NAME = "dto";
    public static final String ID_DTO_FIELD_ATTRIBUTE_NAME = "idDTOField";
    public static final String LABEL_DTO_FIELD_ATTRIBUTE_NAME = "labelDTOField";
    public static final String SELECTED_ID_DTO_FIELD_ATTRIBUTE_NAME = "selectedIDDTOField";
    
    public boolean isSelectBoxNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readSelectBox(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws Exception {
        if ("#text".equals(node.getNodeName())) {
            log("Found text node. Type = " + node.getNodeType() + ", text content = '" + node.getTextContent() + "'");
        }

        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        
        SelectBox selectBox = new SelectBox();
        selectBox.setNodePath(nodePath);
        selectBox.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        selectBox.setDTO(XMLUtil.getStringAttributeRequired(node, DTO_ATTRIBUTE_NAME));
        selectBox.setIDDTOField(XMLUtil.getStringAttributeRequired(node, ID_DTO_FIELD_ATTRIBUTE_NAME));
        selectBox.setSelectedIDDTOField(XMLUtil.getStringAttributeOptional(node, SELECTED_ID_DTO_FIELD_ATTRIBUTE_NAME, null));
        selectBox.setLabelDTOField(XMLUtil.getStringAttributeRequired(node, LABEL_DTO_FIELD_ATTRIBUTE_NAME));
        
        if (layoutCell.getCellItems() == null) {
            layoutCell.setCellItems(new ArrayList<>());
        }
        layoutCell.getCellItems().add(selectBox);

        EventParameterDAO eventParameterDAO = new EventParameterDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
            	if (EventParameterDAO.getNodeName().equals(i.getNodeName())){
            		eventParameterDAO.readEventParameter(i, childNodePath, result, selectBox);
            	} else {
            		throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
            	}
            	
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    private void log(String message) {
        System.out.println("SelectBoxDAO: " + message);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, EventParameterDAO.getNodeName(), "event parameter"));
        return result;
    }
    
    public AssistValueListProvider createPossibleActiveAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "timer is active"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "timer is deactivated"));
        return new AssistValueList(assistValues);
    }


    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "ID"));
        result.add(BaseUtil.createAssistValue(true, DTO_ATTRIBUTE_NAME, "ID of the DTO that contains the list that should be shown in the select box"));
        result.add(BaseUtil.createAssistValue(true, ID_DTO_FIELD_ATTRIBUTE_NAME, "Field in the list that contains the ID"));
        result.add(BaseUtil.createAssistValue(true, LABEL_DTO_FIELD_ATTRIBUTE_NAME, "Field in the list that contains the displayed label"));
        result.add(BaseUtil.createAssistValue(false, SELECTED_ID_DTO_FIELD_ATTRIBUTE_NAME, "A field in a DTO (usually NOT the DTO that contains the IDs and labels for the select box) that contians the ID selected in the SelectBox"));
        return result;
    }

}
