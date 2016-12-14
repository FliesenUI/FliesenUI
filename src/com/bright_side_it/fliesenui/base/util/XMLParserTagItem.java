package com.bright_side_it.fliesenui.base.util;

import java.util.List;

public class XMLParserTagItem {
    private String name;
    private int openTagStartPos;
    private int openTagEndPos;
    private Integer closeTagStartPos;
    private Integer closeTagEndPos;
    private List<XMLParserTagItem> children;
    private XMLParserTagItem parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<XMLParserTagItem> getChildren() {
        return children;
    }

    public void setChildren(List<XMLParserTagItem> children) {
        this.children = children;
    }

    public int getOpenTagStartPos() {
        return openTagStartPos;
    }

    public void setOpenTagStartPos(int openTagStartPos) {
        this.openTagStartPos = openTagStartPos;
    }

    public int getOpenTagEndPos() {
        return openTagEndPos;
    }

    public void setOpenTagEndPos(int openTagEndPos) {
        this.openTagEndPos = openTagEndPos;
    }

    public Integer getCloseTagStartPos() {
        return closeTagStartPos;
    }

    public void setCloseTagStartPos(Integer closeTagStartPos) {
        this.closeTagStartPos = closeTagStartPos;
    }

    public Integer getCloseTagEndPos() {
        return closeTagEndPos;
    }

    public void setCloseTagEndPos(Integer closeTagEndPos) {
        this.closeTagEndPos = closeTagEndPos;
    }

    public XMLParserTagItem getParent() {
        return parent;
    }

    public void setParent(XMLParserTagItem parent) {
        this.parent = parent;
    }

}
