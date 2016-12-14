package com.bright_side_it.fliesenui.screendefinition.logic;

import java.util.ArrayList;

import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class NodePathLogic {

    public NodePath createChildNodePath(NodePath parent, int nodeIndex) {
        NodePath result = new NodePath();
        if (parent == null) {
            throw new RuntimeException("Parent node may not be null");
        } else {
            result.setNodeIndexChain(new ArrayList<Integer>(parent.getNodeIndexChain()));
            result.setTopElementID(parent.getTopElementID());
            result.setDefinitionDocumentFile(parent.getDefinitionDocumentFile());
            result.setDefinitionDocumentType(parent.getDefinitionDocumentType());
        }
        result.getNodeIndexChain().add(new Integer(nodeIndex));
        return result;
    }
}
