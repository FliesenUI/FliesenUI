package com.bright_side_it.fliesenui.generator.logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bright_side_it.fliesenui.generator.model.HTMLTag;

import java.util.Objects;

public class HTMLTagLogic {

    public HTMLTag createTag(String name) {
        HTMLTag tag = new HTMLTag();
        tag.setName(name);
        return tag;
    }

    public HTMLTag createTag(String name, String content, String... attributeKeysAndValues) {
        HTMLTag tag = new HTMLTag();
        tag.setName(name);
        if (content != null) {
            tag.setContent(content);
        }
        if ((attributeKeysAndValues != null) && (attributeKeysAndValues.length != 0)) {
            setAttributes(tag, attributeKeysAndValues);
        }
        return tag;
    }

    //	public HTMLTag addTag(HTMLTag parentTag, String name) {
    //		HTMLTag tag = createTag(name);
    //		List<HTMLTag> subTags = parentTag.getSubTags();
    //		if (subTags == null){
    //			subTags = new ArrayList<>();
    //			parentTag.setSubTags(subTags);
    //		}
    //		subTags.add(tag);
    //		return tag;
    //	}

    public HTMLTag addTag(HTMLTag parentTag, String name) {
        return addTag(parentTag, name, null);
    }

    public HTMLTag addTag(HTMLTag parentTag, String name, String content, String... attributeKeysAndValues) {
        HTMLTag tag = createTag(name);
        List<HTMLTag> subTags = parentTag.getSubTags();
        if (subTags == null) {
            subTags = new ArrayList<>();
            parentTag.setSubTags(subTags);
        }
        subTags.add(tag);
        if (content != null) {
            tag.setContent(content);
        }

        if ((attributeKeysAndValues == null) || (attributeKeysAndValues.length == 0)) {
            return tag;
        }

        setAttributes(tag, attributeKeysAndValues);

        return tag;
    }

    public void setAttributes(HTMLTag tag, String... attributeKeysAndValues) {
        if (attributeKeysAndValues.length % 2 != 0) {
            throw new RuntimeException("attributeKeysAndValues must contain a list of key-value pairs, but there is a key without value: "
                    + attributeKeysAndValues[attributeKeysAndValues.length - 1]);
        }
        Map<String, String> attributes = new LinkedHashMap<>();
        tag.setAttributes(attributes);
        for (int i = 0; i < attributeKeysAndValues.length; i += 2) {
            String key = attributeKeysAndValues[i];
            String value = attributeKeysAndValues[i + 1];
            //          log("adding key = '" + key + "' -> '" + value + "'. Map size before = " + attributes.size());
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            attributes.put(key, value);
        }
    }

    private void writeIndentString(StringBuilder writer, int length) {
        for (int i = 0; i < length; i++) {
            writer.append("\t");
        }
    }

    public void writeTag(HTMLTag tag, StringBuilder writer, int indent) {
        if (tag.getCustomText() != null) {
            StringBuilder indentStringBuilder = new StringBuilder();
            writeIndentString(indentStringBuilder, indent);
            writer.append(tag.getCustomText().replace("\n", "\n" + indentStringBuilder.toString()));
            return;
        }

        writer.append("<" + tag.getName());
        if (tag.getAttributes() != null) {
            writer.append(" ");
            boolean firstAttribute = true;
            for (Entry<String, String> i : tag.getAttributes().entrySet()) {
                if (!firstAttribute) {
                    writer.append(" ");
                }
                writer.append(i.getKey() + "=\"" + i.getValue() + "\"");
                firstAttribute = false;
            }
        }
        if ((tag.getContent() == null) && ((tag.getSubTags() == null) || ((tag.getSubTags().isEmpty())))) {
            writer.append("/>\n");
            return;
        }
        writer.append(">");
        if ((tag.getContent() == null) || (tag.getSubTags() != null)) {
            writer.append("\n");
        }
        if (tag.getContent() != null) {
            writer.append(tag.getContent());
        }
        if (tag.getSubTags() != null) {
            for (HTMLTag subTag : tag.getSubTags()) {
                writeIndentString(writer, indent);
                writeTag(subTag, writer, indent + 1);
            }
        }

        if ((tag.getContent() == null) || (tag.getSubTags() != null)) {
            writeIndentString(writer, indent - 1);
        }

        writer.append("</" + tag.getName() + ">\n");
    }


    @SuppressWarnings("unused")
    private void log(String message) {
        System.out.println("HTMLTagLogic> " + message);
    }

    public void setAttribute(HTMLTag tag, String attributeName, String attributeValue) {
        if (tag.getAttributes() == null) {
            tag.setAttributes(new LinkedHashMap<String, String>());
        }
        tag.getAttributes().put(attributeName, attributeValue);
    }

}
