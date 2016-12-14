package com.bright_side_it.fliesenui.plugin.model;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;

public class PluginVariable {
    private String id;
    private BasicType type;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public BasicType getType() {
        return type;
    }

    public void setType(BasicType type) {
        this.type = type;
    }

}
