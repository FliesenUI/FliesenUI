package com.bright_side_it.fliesenui.screendefinition.model;

import java.io.File;
import java.util.List;

public class NodePath {
    public enum DefinitionDocumentType {
        SCREEN, DTO, PROJECT, PLUGIN, STRING_RESOURCE
    }

    private String topElementID;
    private File definitionDocumentFile;
    private List<Integer> nodeIndexChain;
    private DefinitionDocumentType definitionDocumentType;


    public List<Integer> getNodeIndexChain() {
        return nodeIndexChain;
    }

    public void setNodeIndexChain(List<Integer> nodeIndexChain) {
        this.nodeIndexChain = nodeIndexChain;
    }

    public String getTopElementID() {
        return topElementID;
    }

    public void setTopElementID(String topElementID) {
        this.topElementID = topElementID;
    }

    public DefinitionDocumentType getDefinitionDocumentType() {
        return definitionDocumentType;
    }

    public void setDefinitionDocumentType(DefinitionDocumentType definitionDocumentType) {
        this.definitionDocumentType = definitionDocumentType;
    }

    public File getDefinitionDocumentFile() {
        return definitionDocumentFile;
    }

    public void setDefinitionDocumentFile(File definitionDocumentFile) {
        this.definitionDocumentFile = definitionDocumentFile;
    }

}
