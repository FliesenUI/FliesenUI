package com.bright_side_it.fliesenui.generator.model;

import java.util.List;
import java.util.Map;

public class HTMLTag {
    private String name;
    private Map<String, String> attributes;
    private String content;

    /** If custom text is set this text will just be written to the output string instead of name, attributes and content */
    private String customText;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private List<HTMLTag> subTags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<HTMLTag> getSubTags() {
        return subTags;
    }

    public void setSubTags(List<HTMLTag> subTags) {
        this.subTags = subTags;
    }

    /** If custom text is set this text will just be written to the output string instead of name, attributes and content */
    public String getCustomText() {
        return customText;
    }

    /** If custom text is set this text will just be written to the output string instead of name, attributes and content */
    public void setCustomText(String customText) {
        this.customText = customText;
    }



}
