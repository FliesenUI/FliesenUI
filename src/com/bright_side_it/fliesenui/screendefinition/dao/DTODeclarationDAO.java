package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class DTODeclarationDAO {
    private static final String NODE_NAME = "dto";
    public static final String TYPE_ATTRIBUTE_NAME = "type";

    public boolean isDTODeclarationNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readDTODeclaration(Node node, NodePath nodePath, ScreenDefinitionDAOResult result) throws Exception {
        if ("#text".equals(node.getNodeName())) {
            log("Found text node. Type = " + node.getNodeType() + ", text content = '" + node.getTextContent() + "'");
        }

        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        DTODeclaration dtoDeclaration = new DTODeclaration();
        dtoDeclaration.setNodePath(nodePath);
        dtoDeclaration.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        dtoDeclaration.setType(XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME));

        if (result.getScreenDefinition() == null) {
            result.setScreenDefinition(new ScreenDefinition());
        }
        if (result.getScreenDefinition().getDTODeclarations() == null) {
            result.getScreenDefinition().setDTODeclarations(new TreeMap<String, DTODeclaration>());
        }
        if (result.getScreenDefinition().getDTODeclarations().containsKey(dtoDeclaration.getID())) {
            throw new Exception("A DTO with the id '" + dtoDeclaration.getID() + "' has already been declared");
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        
        result.getScreenDefinition().getDTODeclarations().put(dtoDeclaration.getID(), dtoDeclaration);

    }

    private void log(String message) {
        System.out.println("DTODeclarationDAO: " + message);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, BaseConstants.ID_ATTRIBUTE_NAME, "ID of the DTO 'instance' that can be used in the screen"));
        result.add(BaseUtil.createAssistValue(null, TYPE_ATTRIBUTE_NAME, "Type of DTO (which is the name of the XML file of the DTO)"));
        return result;
    }

}
