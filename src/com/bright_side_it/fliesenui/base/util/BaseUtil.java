package com.bright_side_it.fliesenui.base.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandlerContainer;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener.EventListenType;
import com.bright_side_it.fliesenui.screendefinition.model.EventListenerContainer;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSourceContainer;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenTopElement;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem.TableWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;

public class BaseUtil {

	public static String buildIDWithPrefix(String id, String prefix) {
        return prefix + idToFirstCharUpperCase(id);
    }

    public static String idToFirstCharUpperCase(String id) {
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    public static String idToFirstCharLowerCase(String id) {
    	if (id == null){
    		return null;
    	}
    	if (id.isEmpty()){
    		return id;
    	}
        return Character.toLowerCase(id.charAt(0)) + id.substring(1);
    }

    public static boolean isNotNullAndNotEmpty(Map<?, ?> map) {
        if (map == null) {
            return false;
        }
        return !map.isEmpty();
    }

    public static boolean isNotNullAndNotEmpty(List<?> list) {
        if (list == null) {
            return false;
        }
        return !list.isEmpty();
    }

    public static String getDTOInstanceName(String dtoString) {
        int pos = dtoString.indexOf(".");
        if (pos < 0) {
            return dtoString;
        }
        return dtoString.substring(0, pos);
    }

    public static List<String> getDTOFieldChain(String dtoString) {
        List<String> result = new ArrayList<String>();
        int pos = dtoString.indexOf(".");
        if (pos < 0) {
            return result;
        }
        String useString = dtoString.substring(pos + 1);
        StringTokenizer tokenizer = new StringTokenizer(useString, ".");
        while (tokenizer.hasMoreElements()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }

    public static List<LayoutCell> getAllLayoutCells(ScreenDefinition screenDefinition) {
        List<LayoutCell> result = new ArrayList<LayoutCell>();
        for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
            if (screenTopElement instanceof LayoutContainer) {
                result.addAll(getAllLayoutCells((LayoutContainer) screenTopElement));
            }
        }
        return result;
    }

    public static List<LayoutBar> getAllLayoutBars(ScreenDefinition screenDefinition) {
    	List<LayoutBar> result = new ArrayList<LayoutBar>();
    	for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
    		if (screenTopElement instanceof LayoutContainer) {
    			result.addAll(getAllLayoutBars((LayoutContainer) screenTopElement));
    		}
    	}
    	return result;
    }
    
    public static List<LayoutContainer> getAllLayoutContainers(ScreenDefinition screenDefinition) {
    	List<LayoutContainer> result = new ArrayList<>();
    	for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
    		if (screenTopElement instanceof LayoutContainer) {
    			result.add((LayoutContainer)screenTopElement);
    			result.addAll(getAllLayoutContainers((LayoutContainer) screenTopElement));
    		}
    	}
    	return result;
    }
    
    public static List<PluginInstance> getAllPluginInstances(ScreenDefinition screenDefinition) {
        List<PluginInstance> result = new ArrayList<PluginInstance>();
        for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
            if (screenTopElement instanceof PluginInstance) {
                result.add((PluginInstance) screenTopElement);
            } else if (screenTopElement instanceof LayoutContainer) {
                result.addAll(getAllPluginInstances((LayoutContainer) screenTopElement));
            }
        }
        return result;
    }

    private static List<PluginInstance> getAllPluginInstances(LayoutContainer layoutContainer) {
        List<PluginInstance> result = new ArrayList<PluginInstance>();
        for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
            for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
                if (cell.getCellItems() != null) {
                    for (CellItem cellItem : cell.getCellItems()) {
                        if (cellItem instanceof PluginInstance) {
                            result.add((PluginInstance) cellItem);
                        } else if (cellItem instanceof LayoutContainer) {
                            result.addAll(getAllPluginInstances((LayoutContainer) cellItem));
                        }
                    }
                }
            }
        }
        return result;
    }



    private static List<LayoutCell> getAllLayoutCells(LayoutContainer layoutContainer) {
        List<LayoutCell> result = new ArrayList<LayoutCell>();
        for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
            for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
                result.add(cell);
                if (cell.getCellItems() != null) {
                    for (CellItem cellItem : cell.getCellItems()) {
                        if (cellItem instanceof LayoutContainer) {
                            result.addAll(getAllLayoutCells((LayoutContainer) cellItem));
                        }
                    }
                }
            }
        }
        return result;
    }

    private static List<LayoutBar> getAllLayoutBars(LayoutContainer layoutContainer) {
    	List<LayoutBar> result = new ArrayList<LayoutBar>();
    	for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
    		result.add(bar);
    		for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
    			if (cell.getCellItems() != null) {
    				for (CellItem cellItem : cell.getCellItems()) {
    					if (cellItem instanceof LayoutContainer) {
    						result.addAll(getAllLayoutBars((LayoutContainer) cellItem));
    					}
    				}
    			}
    		}
    	}
    	return result;
    }
    
    private static List<LayoutContainer> getAllLayoutContainers(LayoutContainer layoutContainer) {
    	List<LayoutContainer> result = new ArrayList<LayoutContainer>();
    	for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
    		for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
    			if (cell.getCellItems() != null) {
    				for (CellItem cellItem : cell.getCellItems()) {
    					if (cellItem instanceof LayoutContainer) {
    						result.add((LayoutContainer)cellItem);
    						result.addAll(getAllLayoutContainers((LayoutContainer) cellItem));
    					}
    				}
    			}
    		}
    	}
    	return result;
    }
    
    public static List<CodeEditorWidget> getAllCodeEditorWidgets(ScreenDefinition screenDefinition) {
        List<CodeEditorWidget> result = new ArrayList<CodeEditorWidget>();
        for (CellItem i : getAllCellItems(screenDefinition)) {
            if (i instanceof CodeEditorWidget) {
                result.add((CodeEditorWidget) i);
            }
        }
        return result;
    }



    public static List<CellItem> getAllCellItems(ScreenDefinition screenDefinition) {
        List<CellItem> result = new ArrayList<CellItem>();
        for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
            if (screenTopElement instanceof LayoutContainer) {
                result.addAll(getAllCellItems((LayoutContainer) screenTopElement));
            }
        }
        return result;
    }



    private static List<CellItem> getAllCellItems(LayoutContainer layoutContainer) {
        List<CellItem> result = new ArrayList<CellItem>();
        for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
            for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
                if (cell.getCellItems() != null) {
                    for (CellItem cellItem : cell.getCellItems()) {
                        result.add(cellItem);
                        if (cellItem instanceof LayoutContainer) {
                            result.addAll(getAllCellItems((LayoutContainer) cellItem));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<EventParameterContainer> getAllEventParameterContainers(ScreenDefinition screenDefinition) {
        List<EventParameterContainer> result = new ArrayList<EventParameterContainer>();
        result.addAll(getAllBasicWidgets(screenDefinition));
        result.addAll(getAllSelectBoxes(screenDefinition));
        result.addAll(getAllTableWidgets(screenDefinition));
        result.addAll(getAllTableWidgetItems(screenDefinition));
        result.addAll(getAllTimers(screenDefinition));
        return result;
    }

    public static List<EventHandlerContainer> getAllEventHandlerContainers(ScreenDefinition screenDefinition) {
    	List<EventHandlerContainer> result = new ArrayList<EventHandlerContainer>();
    	result.addAll(getAllBasicWidgets(screenDefinition));
    	return result;
    }
    
    public static List<Timer> getAllTimers(ScreenDefinition screenDefinition) {
    	return new ArrayList<Timer>(BaseUtil.toEmptyMapIfNull(screenDefinition.getTimers()).values());
    }

    public static List<BasicWidget> getAllBasicWidgets(ScreenDefinition screenDefinition) {
    	List<BasicWidget> result = new ArrayList<BasicWidget>();
    	for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
    		if (screenTopElement instanceof LayoutContainer) {
    			result.addAll(getAllBasicWidgets((LayoutContainer) screenTopElement));
    		}
    	}
    	return result;
    }
    
    public static List<BasicWidget> getAllBasicWidgets(ScreenDefinition screenDefinition, BasicWidgetType ... types) {
    	List<BasicWidget> result = new ArrayList<BasicWidget>();
    	for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
    		if (screenTopElement instanceof LayoutContainer) {
    			result.addAll(getAllBasicWidgets((LayoutContainer) screenTopElement, types));
    		}
    	}
    	return result;
    }
    
    public static BasicWidget getBasicWidgetWithIDOptional(ScreenDefinition screenDefinition, String id) {
    	for (BasicWidget i: getAllBasicWidgets(screenDefinition)){
    		if (i == null){
    			throw new RuntimeException("Unkonwn basic widget in screen definition: null");
    		}
    		if ((i.getID() != null) && (i.getID().equals(id))){
    			return i;
    		}
    	}
    	return null;
    }

    public static List<EventListenerContainer> getAllEventListenerContainers(ScreenDefinition screenDefinition) {
    	List<EventListenerContainer> result = new ArrayList<EventListenerContainer>();
    	result.add(screenDefinition);
    	result.addAll(getAllCodeEditorWidgets(screenDefinition));
    	return result;
    }
    
    public static List<SelectBox> getAllSelectBoxes(ScreenDefinition screenDefinition) {
    	List<SelectBox> result = new ArrayList<SelectBox>();
    	for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
    		if (screenTopElement instanceof LayoutContainer) {
    			result.addAll(getAllSelectBoxes((LayoutContainer) screenTopElement));
    		}
    	}
    	return result;
    }
    
    public static List<ImageSourceContainer> getAllImageSourceContainers(ScreenDefinition screenDefinition) {
        List<ImageSourceContainer> result = new ArrayList<ImageSourceContainer>();
        for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
            if (screenTopElement instanceof LayoutContainer) {
                result.addAll(getAllImageSourceContainers((LayoutContainer) screenTopElement));
            }
        }
        return result;
    }

    private static List<BasicWidget> getAllBasicWidgets(LayoutContainer layoutContainer) {
        List<BasicWidget> result = new ArrayList<BasicWidget>();
        for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
            for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
                if (cell.getCellItems() != null) {
                    for (CellItem cellItem : cell.getCellItems()) {
                        if (cellItem instanceof BasicWidget) {
                            result.add((BasicWidget) cellItem);
                        } else if (cellItem instanceof LayoutContainer) {
                            result.addAll(getAllBasicWidgets((LayoutContainer) cellItem));
                        }
                    }
                }
            }
        }
        return result;
    }

    private static List<BasicWidget> getAllBasicWidgets(LayoutContainer layoutContainer, BasicWidgetType ... types) {
    	List<BasicWidget> result = new ArrayList<BasicWidget>();
    	Set<BasicWidgetType> typeSet = new HashSet<>(Arrays.asList(types));
    	for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
    		for (LayoutCell cell : toEmptyCollectionIfNull(bar.getCells())) {
    			if (cell.getCellItems() != null) {
    				for (CellItem cellItem : cell.getCellItems()) {
    					if ((cellItem instanceof BasicWidget) && (typeSet.contains(((BasicWidget)cellItem).getType()))){
    						result.add((BasicWidget) cellItem);
    					} else if (cellItem instanceof LayoutContainer) {
    						result.addAll(getAllBasicWidgets((LayoutContainer) cellItem, types));
    					}
    				}
    			}
    		}
    	}
    	return result;
    }
    
    private static List<SelectBox> getAllSelectBoxes(LayoutContainer layoutContainer) {
    	List<SelectBox> result = new ArrayList<SelectBox>();
    	for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
    		for (LayoutCell cell : BaseUtil.toEmptyCollectionIfNull(bar.getCells())) {
    			if (cell.getCellItems() != null) {
    				for (CellItem cellItem : cell.getCellItems()) {
    					if (cellItem instanceof SelectBox) {
    						result.add((SelectBox) cellItem);
    					} else if (cellItem instanceof LayoutContainer) {
    						result.addAll(getAllSelectBoxes((LayoutContainer) cellItem));
    					}
    				}
    			}
    		}
    	}
    	return result;
    }
    
    private static List<ImageSourceContainer> getAllImageSourceContainers(LayoutContainer layoutContainer) {
        List<ImageSourceContainer> result = new ArrayList<ImageSourceContainer>();
        for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
            for (LayoutCell cell : bar.getCells()) {
                if (cell.getCellItems() != null) {
                    for (CellItem cellItem : cell.getCellItems()) {
                        if (cellItem instanceof ImageSourceContainer) {
                            result.add((ImageSourceContainer) cellItem);
                        } else if (cellItem instanceof LayoutContainer) {
                            result.addAll(getAllImageSourceContainers((LayoutContainer) cellItem));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<TableWidget> getAllTableWidgets(ScreenDefinition screenDefinition) {
        List<TableWidget> result = new ArrayList<TableWidget>();
        for (ScreenTopElement screenTopElement : screenDefinition.getTopElements()) {
            if (screenTopElement instanceof LayoutContainer) {
                result.addAll(getAllTableWidgets((LayoutContainer) screenTopElement));
            }
        }
        return result;
    }

    private static List<TableWidget> getAllTableWidgets(LayoutContainer layoutContainer) {
        List<TableWidget> result = new ArrayList<TableWidget>();
        for (LayoutBar bar : toEmptyCollectionIfNull(layoutContainer.getBars())) {
            for (LayoutCell cell : BaseUtil.toEmptyCollectionIfNull(bar.getCells())) {
                if (cell.getCellItems() != null) {
                    for (CellItem cellItem : cell.getCellItems()) {
                        if (cellItem instanceof TableWidget) {
                            result.add((TableWidget) cellItem);
                        } else if (cellItem instanceof LayoutContainer) {
                            result.addAll(getAllTableWidgets((LayoutContainer) cellItem));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static <K> Collection<K> toEmptyCollectionIfNull(Collection<K> list) {
        if (list == null) {
            return new ArrayList<K>();
        }
        return list;
    }

    public static <K, V> Map<K, V> toEmptyMapIfNull(Map<K, V> map) {
        if (map == null) {
            return new TreeMap<K, V>();
        }
        return map;
    }

    public static List<TableWidgetItem> getAllTableWidgetItems(ScreenDefinition screenDefinition) {
        List<TableWidgetItem> result = new ArrayList<TableWidgetItem>();
        for (TableWidget tableWidget : getAllTableWidgets(screenDefinition)) {
            for (TableWidgetColumn tableColumn : toEmptyCollectionIfNull(tableWidget.getColumns())) {
                for (TableWidgetItem tableItem : toEmptyCollectionIfNull(tableColumn.getTableItems())) {
                    result.add(tableItem);
                }
            }
        }
        return result;
    }

    public static List<TableWidgetItem> getAllTableWidgetItemsOfType(ScreenDefinition screenDefinition, TableWidgetType... types) {
        List<TableWidgetItem> result = new ArrayList<TableWidgetItem>();
        for (TableWidgetType i : types) {
            result.addAll(getAllTableWidgetItemsOfType(screenDefinition, i));
        }
        return result;
    }

    public static List<TableWidgetItem> getAllTableWidgetItemsOfType(ScreenDefinition screenDefinition, TableWidgetType type) {
        List<TableWidgetItem> result = new ArrayList<TableWidgetItem>();
        for (TableWidgetItem i : getAllTableWidgetItems(screenDefinition)) {
            if (i.getType() == type) {
                result.add(i);
            }
        }
        return result;
    }

    public static boolean isLinkedToDTO(BasicWidget widget, String dtoID) {
        return ((widget.getTextDTOField() != null) && (widget.getTextDTOField().startsWith(dtoID + ".")));
    }

    public static boolean isLinkedToDTO(SelectBox selectBox, String dtoID) {
    	return ((selectBox.getSelectedIDDTOField() != null) && (selectBox.getSelectedIDDTOField().startsWith(dtoID + ".")));
    }
    
    public static AssistValue createAssistValue(Boolean mandatory, String text, String label) {
        AssistValue result = new AssistValue();
        String mandatoryText = "";
        if (mandatory != null){
        	if (mandatory){
        		mandatoryText = "MANDATORY: ";
        	} else {
        		mandatoryText = "OPTIONAL: ";
        	}
        }
        if (label == null) {
        	result.setApiDocLabel(text);
            result.setLabel(mandatoryText + text);
        } else {
        	result.setApiDocLabel(label);
            result.setLabel(mandatoryText + text + " - " + label);
        }
        result.setMandatory(mandatory);
        result.setText(text);
        return result;
    }

    public static AssistValueListProvider createSingleItemAssistValueListProvider(Boolean mandatory, String text, String label) {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(mandatory, text, label));
        return new AssistValueList(assistValues);
    }

    @SafeVarargs
    public static <K> boolean in(K value, K... setOfValues) {
        return new HashSet<K>(Arrays.asList(setOfValues)).contains(value);
    }

    public static List<DTODefinition> getAllDefinedDTOs(Project project) {
        return new ArrayList<DTODefinition>(toEmptyMapIfNull(project.getDTODefinitionsMap()).values());
    }

    public static AssistValueListProvider createInfoOnlyContextAssist(String info) {
        return new AssistValueList(createInfoOnlyContextAssistValues(info));
    }

    public static List<AssistValue> createInfoOnlyContextAssistValues(String info) {
        List<AssistValue> values = new ArrayList<AssistValue>();
        AssistValue assistValue = new AssistValue();
        assistValue.setText("");
        assistValue.setLabel(info);
        values.add(assistValue);
        return values;
    }

    public static List<AssistValue> getMatchingChoices(List<AssistValue> choices, String startText) {
        List<AssistValue> result = new ArrayList<AssistValue>();
        for (AssistValue i : choices) {
            if (i.getText().startsWith(startText)) {
                result.add(i);
            }
        }

        return result;
    }

    /**
     * @return the definition of the DTO. Example: myDTO.type2.type3 returns the definition of type3 or null if the type cannot be found
     */
    public static DTODefinition getDTOFieldDTODefinition(Project project, ScreenDefinition screen, String dtoString) {
        List<String> dtoFieldChain = getDTOFieldChain(dtoString);
        if (dtoFieldChain.isEmpty()) {
            return null;
        }

        DTODeclaration declaration = BaseUtil.toEmptyMapIfNull(screen.getDTODeclarations()).get(getDTOInstanceName(dtoString));
        if (declaration == null) {
            return null;
        }

        DTODefinition definition = null;
        String typeName = declaration.getType();
        while (typeName != null) {
            definition = project.getDTODefinitionsMap().get(typeName);
            if (definition == null) {
                return null;
            }
            if (dtoFieldChain.isEmpty()) {
                return definition;
            }
            DTOField field = definition.getFields().get(dtoFieldChain.remove(0));
            if (field == null) {
                return null;
            }
            typeName = field.getDTOType();
        }
        return null;
    }

    /**
     * @return the BasicType of the field in the DTO. Example: myDTO.type2.type3 returns the basic type of type3 if it is a basic type (e.g. "string")
     * or null if it does not exist or is a DTO type (to find the DTODefinition use getDTODefinition)
     */
    public static BasicType getDTOFieldBasicType(Project project, ScreenDefinition screen, String dtoString) {
    	List<String> dtoFieldChain = getDTOFieldChain(dtoString);
    	if (dtoFieldChain.isEmpty()) {
    		return null;
    	}
    	
    	DTODeclaration declaration = BaseUtil.toEmptyMapIfNull(screen.getDTODeclarations()).get(getDTOInstanceName(dtoString));
    	if (declaration == null) {
    		return null;
    	}
    	
    	DTODefinition definition = null;
    	String typeName = declaration.getType();
    	while (typeName != null) {
    		definition = project.getDTODefinitionsMap().get(typeName);
    		if (definition == null) {
    			return null;
    		}
    		if (dtoFieldChain.isEmpty()) {
    			return null;
    		}
    		DTOField field = definition.getFields().get(dtoFieldChain.remove(0));
    		if (field == null) {
    			return null;
    		}
    		
    		if (dtoFieldChain.isEmpty()){
				return field.getBasicType();
    		}
    		
    		typeName = field.getDTOType();
    	}
    	return null;
    }
    
    public static PluginInstance findPluginInstance(ScreenDefinition screenDefinition, String pluginID) {
        for (PluginInstance i : getAllPluginInstances(screenDefinition)) {
            if (i.getID().equals(pluginID)) {
                return i;
            }
        }
        return null;
    }

    public static boolean isPrimitiveDataType(BasicType type) {
        return (type != BasicType.STRING);
    }

    public static Set<String> getTextSet(List<AssistValue> list) {
        Set<String> result = new TreeSet<String>();
        for (AssistValue i : list) {
            result.add(i.getText());
        }
        return result;
    }

    public static String nodePathToString(NodePath nodePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("type: " + nodePath.getDefinitionDocumentType() + ", file: '" + nodePath.getDefinitionDocumentFile().getName() + "', TopElementID: "
                + nodePath.getTopElementID() + ", ");
        sb.append(BaseUtil.nodePathToStringShort(nodePath));
        return sb.toString();
    }

    public static String nodePathToStringShort(NodePath nodePath) {
    	if (nodePath == null){
    		return "[?]";
    	}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodePath.getNodeIndexChain().size(); i++) {
            if (i > 0) {
                sb.append("->");
            }
            sb.append("" + (nodePath.getNodeIndexChain().get(i) + 1));
        }
        return sb.toString();
    }

	public static List<EventListener> getAllEventListenersOfContainer(EventListenerContainer container, EventListenType ... eventListenTypes) {
		List<EventListener> result = new ArrayList<>();
		if (container.getEventListeners() == null){
			return result;
		}
		
		for (EventListenType eventListenType : eventListenTypes){
			for (EventListener i : container.getEventListeners()){
				if (i.getEventListenType() == eventListenType){
					result.add(i);
				}
			}
		}
		
		return result;
	}

	public static String toStringEnumID(String stringKey){
		return stringKey.toUpperCase();
	}
	
	public static String toJSStringID(String stringKey){
		return stringKey.toLowerCase();
	}
	
    
}
