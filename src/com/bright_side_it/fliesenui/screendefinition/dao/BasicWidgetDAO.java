package com.bright_side_it.fliesenui.screendefinition.dao;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.Style;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class BasicWidgetDAO{

    public static final String TEXT_ATTRIBUTE_NAME = "text";
    public static final String PRIMARY_ATTRIBUTE_NAME = "primary";
    public static final String HEIGHT_ATTRIBUTE_NAME = "height";
    public static final String STYLE_ATTRIBUTE_NAME = "style";
    public static final String LABEL_TEXT_ATTRIBUTE_NAME = "labelText";
    public static final String TEXT_DTO_FIELD_ATTRIBUTE_NAME = "textDTOField";
    public static final String VISIBLE_ATTRIBUTE_NAME = "visible";
    public static final String READ_ONLY_ATTRIBUTE_NAME = "readOnly";
    public static final String SCROLL_TO_BOTTOM_ATTRIBUTE_NAME = "scrollToBottom";

    private static final String WIDGET_NODE_NAME_TEXT_FIELD = "textField";
    private static final String WIDGET_NODE_NAME_TEXT_AREA = "textArea";
    private static final String WIDGET_NODE_NAME_BUTTON = "button";
    private static final String WIDGET_NODE_NAME_IMAGE_BUTTON = "imageButton";
    private static final String WIDGET_NODE_NAME_IMAGE = "image";
    private static final String WIDGET_NODE_NAME_LABEL = "label";
    private static final String WIDGET_NODE_NAME_PROGRESS_BAR = "progressBar";
    private static final String WIDGET_NODE_NAME_SPACE = "space";
    private static final String WIDGET_NODE_NAME_CHECKBOX = "checkbox";
    private static final String WIDGET_NODE_NAME_SWITCH = "switch";
    private static final String WIDGET_NODE_NAME_FILE_UPLOAD = "fileUpload";
    private static final String WIDGET_NODE_NAME_MARKDOWN_VIEW = "markdownView";

    private static final String STYLE_VALUE_NAME_NORMAL = "normal";
    private static final String STYLE_VALUE_NAME_TINY = "tiny";
    private static final String STYLE_VALUE_NAME_SMALL = "small";
    private static final String STYLE_VALUE_NAME_MEDIUM = "medium";
    private static final String STYLE_VALUE_NAME_LARGE = "large";

    public boolean isWidgetNode(Node node) {
        Set<String> nodeNames = getNodeNameToTypeMap().keySet();
        return nodeNames.contains(node.getNodeName());
    }

    private static Map<String, BasicWidgetType> getNodeNameToTypeMap() {
        Map<String, BasicWidgetType> result = new TreeMap<String, BasicWidget.BasicWidgetType>();

        result.put(WIDGET_NODE_NAME_TEXT_FIELD, BasicWidgetType.TEXT_FIELD);
        result.put(WIDGET_NODE_NAME_MARKDOWN_VIEW, BasicWidgetType.MARKDOWN_VIEW);
        result.put(WIDGET_NODE_NAME_TEXT_AREA, BasicWidgetType.TEXT_AREA);
        result.put(WIDGET_NODE_NAME_BUTTON, BasicWidgetType.BUTTON);
        result.put(WIDGET_NODE_NAME_IMAGE_BUTTON, BasicWidgetType.IMAGE_BUTTON);
        result.put(WIDGET_NODE_NAME_IMAGE, BasicWidgetType.IMAGE);
        result.put(WIDGET_NODE_NAME_LABEL, BasicWidgetType.LABEL);
        result.put(WIDGET_NODE_NAME_PROGRESS_BAR, BasicWidgetType.PROGRESS_BAR);
        result.put(WIDGET_NODE_NAME_SPACE, BasicWidgetType.SPACE);
        result.put(WIDGET_NODE_NAME_CHECKBOX, BasicWidgetType.CHECKBOX);
        result.put(WIDGET_NODE_NAME_SWITCH, BasicWidgetType.SWITCH);
        result.put(WIDGET_NODE_NAME_FILE_UPLOAD, BasicWidgetType.FILE_UPLOAD);

        return result;
    }

    public static List<AssistValue> createAssistValues() {
        List<AssistValue> result = new ArrayList<AssistValue>();

        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_TEXT_FIELD, "Text Field"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_TEXT_AREA, "Text Area"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_BUTTON, "Button"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_IMAGE, "Image"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_IMAGE_BUTTON, "Image Button"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_LABEL, "Label"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_MARKDOWN_VIEW, "Markdown view: Display the text formatted as markdown"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_CHECKBOX, "Checkbox"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_SWITCH, "Switch"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_PROGRESS_BAR, "Progress Bar"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_SPACE, "Space"));
        result.add(BaseUtil.createAssistValue(null, WIDGET_NODE_NAME_FILE_UPLOAD, "File Upload"));


        return result;
    }

    public void readWidget(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws ScreenDefionitionReadException, Exception {
        if (layoutCell.getCellItems() == null) {
            layoutCell.setCellItems(new ArrayList<>());
        }

        BasicWidget widget = new BasicWidget();
        layoutCell.getCellItems().add(widget);

        widget.setNodePath(nodePath);

        widget.setType(getNodeNameToTypeMap().get(node.getNodeName()));
        if (widget.getType() == null) {
            throw new ScreenDefionitionReadException("Unknown widget type: '" + node.getNodeName() + "'", nodePath, "");
        }

        widget.setText(XMLUtil.getStringAttributeOptional(node, TEXT_ATTRIBUTE_NAME, null));
        widget.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        widget.setLabelText(XMLUtil.getStringAttributeOptional(node, LABEL_TEXT_ATTRIBUTE_NAME, null));
        widget.setPrimary(XMLUtil.getBooleanAttributeOptional(node, PRIMARY_ATTRIBUTE_NAME, false));
        widget.setTextDTOField(XMLUtil.getStringAttributeOptional(node, TEXT_DTO_FIELD_ATTRIBUTE_NAME, null));
        widget.setHeight(XMLUtil.getIntegerAttributeOptional(node, HEIGHT_ATTRIBUTE_NAME, null));
        widget.setReadOnly(XMLUtil.getBooleanAttributeOptional(node, READ_ONLY_ATTRIBUTE_NAME, null));
        widget.setScrollToBottom(XMLUtil.getBooleanAttributeOptional(node, SCROLL_TO_BOTTOM_ATTRIBUTE_NAME, null));
        widget.setVisible(XMLUtil.getBooleanAttributeOptional(node, VISIBLE_ATTRIBUTE_NAME, true));
        Style style = parseStyle(XMLUtil.getStringAttributeOptional(node, STYLE_ATTRIBUTE_NAME, null), nodePath, STYLE_ATTRIBUTE_NAME);
        widget.setStyle(style);

        EventParameterDAO eventParameterDAO = new EventParameterDAO();
        EventHandlerDAO eventHandlerDAO = new EventHandlerDAO();

        ImageSourceAttributesDAO imageSourceAttributesDAO = new ImageSourceAttributesDAO();
        
        imageSourceAttributesDAO.readImageSourceFromAttributes(node, nodePath, result, widget);
//        EventListenerDAO eventListenerDAO = new EventListenerDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
            	if (EventParameterDAO.getNodeName().equals(i.getNodeName())){
            		eventParameterDAO.readEventParameter(i, childNodePath, result, widget);
            	} else if (eventHandlerDAO.isEventHandlerNode(i.getNodeName())){
            		eventHandlerDAO.readEventHandler(i, childNodePath, result, widget);
//            	} else if (EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_PRESS.equals(i.getNodeName())){
//            		eventListenerDAO.readEventListener(i, childNodePath, result, widget);
//            	} else if (EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_DOWN.equals(i.getNodeName())){
//            		eventListenerDAO.readEventListener(i, childNodePath, result, widget);
            	} else {
            		throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
            	}
            	
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes(node.getNodeName())), result);
    }

    private Style parseStyle(String string, NodePath nodePath, String attributeName) throws ScreenDefionitionReadException {
        if (string == null) {
            return null;
        }
        if (STYLE_VALUE_NAME_NORMAL.equals(string)) {
            return Style.NORMAL;
        } else if (STYLE_VALUE_NAME_TINY.equals(string)) {
            return Style.TINY;
        } else if (STYLE_VALUE_NAME_SMALL.equals(string)) {
            return Style.SMALL;
        } else if (STYLE_VALUE_NAME_MEDIUM.equals(string)) {
            return Style.MEDIUM;
        } else if (STYLE_VALUE_NAME_LARGE.equals(string)) {
            return Style.LARGE;
        }
        throw new ScreenDefionitionReadException("Unknown style: '" + string + "'", nodePath, attributeName);
    }

    public static Collection<String> getNodeNames() {
        return getNodeNameToTypeMap().keySet();
    }

    public List<AssistValue> getTagAttributes(String nodeName) {
        List<AssistValue> result = new ArrayList<AssistValue>();

        BasicWidgetType type = getNodeNameToTypeMap().get(nodeName);
        if (type == null) {
            throw new RuntimeException("Unknown node name: '" + nodeName + "'");
        }

        if (in(type, BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON, BasicWidgetType.PROGRESS_BAR, BasicWidgetType.TEXT_AREA, BasicWidgetType.TEXT_FIELD, BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH, BasicWidgetType.FILE_UPLOAD, BasicWidgetType.MARKDOWN_VIEW)) {
            result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "ID"));
        } else {
            result.add(BaseUtil.createAssistValue(false, BaseConstants.ID_ATTRIBUTE_NAME, "ID"));
        }
        if (in(type, BasicWidgetType.FILE_UPLOAD)) {
        	result.add(BaseUtil.createAssistValue(true, TEXT_ATTRIBUTE_NAME, "initial text of the widget"));
        }

        if (in(type, BasicWidgetType.BUTTON, BasicWidgetType.LABEL, BasicWidgetType.TEXT_AREA, BasicWidgetType.TEXT_FIELD, BasicWidgetType.MARKDOWN_VIEW)) {
            result.add(BaseUtil.createAssistValue(true, TEXT_ATTRIBUTE_NAME, "initial text of the widget"));
            result.add(BaseUtil.createAssistValue(false, TEXT_DTO_FIELD_ATTRIBUTE_NAME,
                    "name of the field in a declared DTO that sets the text of this widget whenever the DTO is updated"));
        }
        if (in(type, BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH)) {
            result.add(BaseUtil.createAssistValue(true, TEXT_ATTRIBUTE_NAME, "initial text of the widget"));
        }
        if (in(type, BasicWidgetType.BUTTON)) {
            result.add(BaseUtil.createAssistValue(false, PRIMARY_ATTRIBUTE_NAME, "mark this button as primary which means it will be highlighted"));
        }
        if (in(type, BasicWidgetType.SPACE)) {
            result.add(BaseUtil.createAssistValue(true, HEIGHT_ATTRIBUTE_NAME, "height in pixels"));
        }
        if (in(type, BasicWidgetType.TEXT_FIELD, BasicWidgetType.TEXT_AREA)) {
            result.add(BaseUtil.createAssistValue(false, HEIGHT_ATTRIBUTE_NAME, "height in pixels"));
            result.add(BaseUtil.createAssistValue(false, READ_ONLY_ATTRIBUTE_NAME, "read only mode"));
            result.add(BaseUtil.createAssistValue(false, SCROLL_TO_BOTTOM_ATTRIBUTE_NAME, "scroll to bottom whenever the text is changed (by the presenter)"));
        }
        if (in(type, BasicWidgetType.LABEL, BasicWidgetType.BUTTON)) {
            result.add(BaseUtil.createAssistValue(false, STYLE_ATTRIBUTE_NAME, "style (" + STYLE_VALUE_NAME_NORMAL + " / " + STYLE_VALUE_NAME_SMALL + " / "
                    + STYLE_VALUE_NAME_MEDIUM + " / " + STYLE_VALUE_NAME_LARGE + ")"));
        }
        if (in(type, BasicWidgetType.TEXT_FIELD)) {
            result.add(BaseUtil.createAssistValue(false, LABEL_TEXT_ATTRIBUTE_NAME,
                    "this text is shown in the text field when empty and above the text field otherwise"));
        }
        result.add(BaseUtil.createAssistValue(false, VISIBLE_ATTRIBUTE_NAME, "is the widget shown or hidden"));

        if (in(type, BasicWidgetType.BUTTON, BasicWidgetType.LABEL, BasicWidgetType.IMAGE, BasicWidgetType.IMAGE_BUTTON)) {
        	result.addAll(new ImageSourceAttributesDAO().getTagAttributes());
        	
        }
        
        return result;
    }

    public List<AssistValue> getPossibleChildTags(String nodeName) {
        List<AssistValue> result = new ArrayList<>();
        if (in(nodeName, WIDGET_NODE_NAME_BUTTON)) {
            result.add(BaseUtil.createAssistValue(null, EventParameterDAO.getNodeName(), "Parameter that is passed to the method when the button is pressed"));
        }
        if (in(nodeName, WIDGET_NODE_NAME_TEXT_FIELD)) {
        	result.add(BaseUtil.createAssistValue(null, EventHandlerDAO.NODE_NAME_ON_ENTER, "Handle event 'enter key'"));
        }
//        if (in(nodeName, WIDGET_NODE_NAME_TEXT_AREA, WIDGET_NODE_NAME_TEXT_FIELD)) {
//        	result.add(BaseUtil.createAssistValue(null, EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_PRESS, "Handle event 'key press'"));
//        	result.add(BaseUtil.createAssistValue(null, EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_DOWN, "Handle event 'key down'"));
//        }
        return result;
    }

    public AssistValueListProvider createPossiblePrimaryAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "primary (highlighted in the UI)"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "not primary"));
        return new AssistValueList(assistValues);
    }

    public AssistValueListProvider createPossibleVisibleAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "visible"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "hidden"));
        return new AssistValueList(assistValues);
    }

    public AssistValueListProvider createPossibleReadOnlyAttributeValues() {
    	List<AssistValue> assistValues = new ArrayList<AssistValue>();
    	assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "read only"));
    	assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "editable"));
    	return new AssistValueList(assistValues);
    }

    public AssistValueListProvider createPossibleScrollToBottomAttributeValues() {
    	List<AssistValue> assistValues = new ArrayList<AssistValue>();
    	assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "scroll to bottom whenever the text is changed (by the presenter)"));
    	assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "do not scroll to bottom automatically"));
    	return new AssistValueList(assistValues);
    }
    
    public AssistValueListProvider createPossibleStyleAttributeValues(String nodeName) {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        if (nodeName.equals(WIDGET_NODE_NAME_LABEL)) {
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_NORMAL, "normal (default)"));
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_SMALL, "small"));
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_MEDIUM, "medium"));
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_LARGE, "large"));
        } else if (nodeName.equals(WIDGET_NODE_NAME_BUTTON)) {
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_NORMAL, "normal (default)"));
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_TINY, "tiny"));
            assistValues.add(BaseUtil.createAssistValue(null, STYLE_VALUE_NAME_SMALL, "small"));
        }
        return new AssistValueList(assistValues);
    }


}
