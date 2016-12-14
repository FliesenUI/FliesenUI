package com.bright_side_it.fliesenui.base.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class XMLParserUtil {
    private static final String[] LETTER = createLetterArray();

    public static XMLParserTagItem parseItems(String text) throws Exception {
        return parseItems(text, 0, null);
    }

    private static String[] createLetterArray() {
        List<String> items = new ArrayList<>();
        for (char i = 'A'; i <= 'Z'; i++) {
            items.add("" + i);
        }
        for (char i = 'a'; i <= 'z'; i++) {
            items.add("" + i);
        }
        String[] result = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            result[i] = items.get(i);
        }
        return result;
    }

    private static XMLParserTagItem parseItems(String text, int startPos, XMLParserTagItem parent) throws Exception {
        XMLParserTagItem result = new XMLParserTagItem();
        int pos = text.indexOf("?>", startPos);
        if (pos < 0) {
            pos = startPos;
        }

        int tagStartPos = text.indexOf("<", pos);
        if (tagStartPos < 0) {
            throw new Exception("Missing beginning of tag");
        }
        while (isComment(text, tagStartPos)) {
            //            log("tag is comment at pos = " + tagStartPos + "->" + toInfoString(text, tagStartPos));
            tagStartPos = text.indexOf("<", tagStartPos + 1);
            if (tagStartPos < 0) {
                throw new Exception("Missing beginning of tag");
            }
        }

        //        log("tagStartPos = " + tagStartPos + "->" + toInfoString(text, tagStartPos));


        int tagNameEndPos = TextUtil.nextIndexOf(text, tagStartPos, " ", "/", ">", "\t", "\n", "\r");
        if (tagNameEndPos < 0) {
            throw new Exception("Could not read tag name");
        }
        //        log("tagNameEndPos = " + tagNameEndPos + "->" + toInfoString(text, tagNameEndPos));
        result.setName(text.substring(tagStartPos + 1, tagNameEndPos));
        //        log("reading tag '" + result.getTagName() + "' at start pos " + startPos);

        result.setOpenTagStartPos(tagStartPos);
        result.setParent(parent);
        int tagEndPos = text.indexOf(">", tagStartPos);
        if (tagEndPos < 0) {
            throw new Exception("Missing end of tag");
        }

        result.setOpenTagEndPos(tagEndPos);

        result.setChildren(new ArrayList<XMLParserTagItem>());
        if (hasClosingTag(text, tagStartPos)) {
            if (hasMoreChildren(text, tagEndPos + 1)) {
                //                log("reading tag '" + result.getTagName() + "'. It has children");
                int startPosOfNextChild = text.indexOf("<", tagStartPos + 1);
                //                log("reading tag '" + result.getTagName() + "'. reading first child");
                XMLParserTagItem childItem = parseItems(text, startPosOfNextChild, result);
                while (childItem != null) {
                    result.getChildren().add(childItem);
                    //                    log("reading tag '" + result.getTagName() + "'. read child: " + childItem.getTagName() + ", openTagStart="
                    //                            + childItem.getOpenTagStartPos() + ", openTagEnd = " + childItem.getOpenTagEndPos() + ", endTagStart = "
                    //                            + childItem.getCloseTagStartPos() + ", closeTagEnd=" + childItem.getCloseTagEndPos());
                    if (childItem.getCloseTagEndPos() != null) {
                        startPosOfNextChild = childItem.getCloseTagEndPos() + 1;
                    } else {
                        startPosOfNextChild = childItem.getOpenTagEndPos() + 1;
                    }

                    if (hasMoreChildren(text, startPosOfNextChild)) {
                        //                        log("reading tag '" + result.getTagName() + "'. reading next child");
                        childItem = parseItems(text, startPosOfNextChild, result);
                    } else {
                        childItem = null;
                    }
                }
                int endTagStart = text.indexOf("</", startPosOfNextChild);
                result.setCloseTagStartPos(endTagStart);
                int endTagEnd = text.indexOf(">", endTagStart);
                result.setCloseTagEndPos(endTagEnd);
            } else {
                int endTagStart = text.indexOf("</", tagStartPos);
                result.setCloseTagStartPos(endTagStart);
                int endTagEnd = text.indexOf(">", endTagStart);
                result.setCloseTagEndPos(endTagEnd);
            }
        }

        //        log("done reading tag '" + result.getTagName() + "'.");

        return result;
    }


    private static boolean isComment(String text, int tagStartPos) {
        return (text.charAt(tagStartPos) == '<') && (text.charAt(tagStartPos + 1) == '!');
    }

    private static boolean hasMoreChildren(String text, int tagStartPos) throws Exception {
        //        log("hasMoreChildren = " + tagStartPos + "->" + toInfoString(text, tagStartPos));
        int nextOccurenceOfLessThanSlash = text.indexOf("</", tagStartPos);
        int nextPosOfLessThan = text.indexOf("<", tagStartPos);
        if (nextPosOfLessThan < 0) {
            throw new Exception("Could not find tag start");
        }
        if ((nextOccurenceOfLessThanSlash >= 0) && (nextOccurenceOfLessThanSlash <= nextPosOfLessThan)) {
            return false;
        }
        return true;
    }

    private static boolean hasClosingTag(String text, int tagStartPos) throws Exception {
        int nextOccurenceOfSlashGreaterThan = text.indexOf("/>", tagStartPos);
        int nextPosOfGreaterThan = text.indexOf(">", tagStartPos);
        if (nextPosOfGreaterThan < 0) {
            throw new Exception("Could not find tag end");
        }
        if ((nextOccurenceOfSlashGreaterThan >= 0) && (nextOccurenceOfSlashGreaterThan < nextPosOfGreaterThan)) {
            return false;
        }


        return true;
    }

    public static XMLParserTagItem getTag(String text) throws Exception {
        return getTag(text, null);
    }

    public static XMLParserTagItem getTag(String text, List<Integer> nodeIndexChain) throws Exception {
        List<Integer> useNodeIndexChain = nodeIndexChain;
        if (useNodeIndexChain == null) {
            useNodeIndexChain = Arrays.asList(0);
        }

        XMLParserTagItem tag = parseItems(text);
        ArrayList<Integer> remainingItems = new ArrayList<>(useNodeIndexChain);
        int firstIndex = remainingItems.remove(0);
        if (firstIndex != 0) {
            throw new Exception("Expected 0 as the first item in the node index chain");
        }

        for (int i : remainingItems) {
            tag = tag.getChildren().get(i);
        }

        return tag;
    }

    public static void log(String message) {
        System.out.println("XMLParserUtil> " + message);
    }

    public static Integer getAttributeValueStartPos(String text, XMLParserTagItem tag, String attribute) {
        int startPos = tag.getOpenTagStartPos() + tag.getName().length() + 1;

        while (startPos < tag.getOpenTagEndPos()) {
            int attributeNameStart = TextUtil.nextIndexOf(text, startPos, LETTER);
            if (attributeNameStart < 0) {
                return null;
            }
            int attributeNameEnd = TextUtil.nextIndexOf(text, attributeNameStart, "=", ">", "/");
            if (attributeNameEnd < 0) {
                return null;
            }
            if (text.charAt(attributeNameEnd) != '=') {
                return null;
            }
            String currentAttributeName = text.substring(attributeNameStart, attributeNameEnd);
            //            log("getAttributeValueStartPos: currentAttributeName = '" + currentAttributeName + "'");

            int startQuotePos = TextUtil.nextIndexOf(text, attributeNameEnd, "'", "\"");
            if (startQuotePos < 0) {
                return null;
            }
            String endQuote = "" + text.charAt(startQuotePos);
            int attributeValueStartPos = startQuotePos + 1;
            int attributeValueEndPos = text.indexOf(endQuote, attributeValueStartPos);
            if (attributeValueEndPos < 0) {
                return null;
            }

            if (currentAttributeName.equals(attribute)) {
                return attributeValueStartPos;
            }
            startPos = attributeValueEndPos + 1;
        }
        return null;
    }

    public static Integer getAttributeValueEndPos(String text, XMLParserTagItem tag, String attribute) {
        Integer startPos = getAttributeValueStartPos(text, tag, attribute);
        if (startPos == null) {
            return null;
        }
        char endQuote = text.charAt(startPos - 1);
        int endPos = text.indexOf(endQuote, startPos);
        return endPos;
    }

    public static XMLParserTagItem findTagAtPos(XMLParserTagItem rootTag, int pos) {
        log("findTagAtPos: rootTag = '" + rootTag.getName() + "', pos = " + pos);
        if (pos < rootTag.getOpenTagStartPos()) {
            return null;
        }
        Integer end = rootTag.getCloseTagEndPos();
        if (end == null) {
            end = rootTag.getOpenTagEndPos();
        }
        if (pos > end) {
            return null;
        }
        if (rootTag.getChildren() == null) {
            return rootTag;
        }
        for (XMLParserTagItem i : rootTag.getChildren()) {
            XMLParserTagItem foundItem = findTagAtPos(i, pos);
            if (foundItem != null) {
                return foundItem;
            }
        }
        return rootTag;
    }

    public static String readTagNameAsPos(String text, int beginningPos) {
        int startPos = text.lastIndexOf("<", beginningPos);
        if (startPos < 0) {
            return null;
        }
//        int endPos = text.indexOf(" ", startPos);
        int endPos = TextUtil.nextIndexOf(text, startPos, " ", "\t", "\n", "\r");
        if (endPos < 0) {
            return null;
        }

        return text.substring(startPos + 1, endPos);
    }

    private static String removeTagStartEndEnd(String tagText) {
        String useText = tagText.trim();
        if (useText.startsWith("<")) {
            useText = useText.substring(1);
        } else {
            return null;
        }
        if (useText.endsWith("/>")) {
            useText = useText.substring(0, useText.length() - 2);
        } else if (useText.endsWith(">")) {
            useText = useText.substring(0, useText.length() - 1);
        } else {
            return null;
        }

        int pos = useText.indexOf(" ");
        if (pos < 0) {
            return ""; //: the tag is empty
        }
        return useText.substring(pos).trim();
    }

    /**
     * reads a tagText like "<mytag value='hi' value2='test' >"
     */
    public static Map<String, String> readTagAttributes(String tagText) {
    	try{
    		Map<String, String> result = new TreeMap<String, String>();
    		String useText = removeTagStartEndEnd(tagText);
    		
    		int length = useText.length();
    		int currentPos = 0;
    		
    		while (currentPos < length) {
    			int valueStartPos = TextUtil.nextIndexOf(useText, currentPos, "'", "\"");
    			if (valueStartPos < 0) {
    				return result;
    			}
    			char charAtPos = useText.charAt(valueStartPos);
    			int valueEndPos = useText.indexOf("" + charAtPos, valueStartPos + 1);
    			if (valueEndPos < 0){
    				//: there is an open " but not a closing one, so the tag is incomplete. Just return what was read so far
    				return result;
    			}
    			String value = useText.substring(valueStartPos + 1, valueEndPos);
    			
    			int attribNameStartPos = useText.lastIndexOf(" ", valueStartPos);
    			if (attribNameStartPos < 0) {
    				if (currentPos == 0) {
    					attribNameStartPos = 0;
    				} else {
    					return result;
    				}
    			}
    			
    			int equalsPos = useText.indexOf("=", attribNameStartPos);
    			if (equalsPos < 0) {
    				return result;
    			}
    			String attribute = useText.substring(attribNameStartPos, equalsPos).trim();
    			result.put(attribute, value);
    			
    			currentPos = valueEndPos + 1;
    		}
    		return result;
    	} catch (Throwable t){
    		RuntimeException error = new RuntimeException("Could not read tag attributes. tagText = >>" + tagText + "<<", t); 
    		error.printStackTrace();
    		throw error;
    	}

    }

}
