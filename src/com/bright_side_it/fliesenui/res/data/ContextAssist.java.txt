package generated.fliesenui.core;

import java.util.List;

public class ContextAssist {
    private CursorPos replaceFrom;
    private CursorPos replaceTo;
    private int selectedItem;
    private List<ContextAssistChoice> choices;

    public CursorPos getReplaceFrom() {
        return replaceFrom;
    }

    public void setReplaceFrom(CursorPos replaceFrom) {
        this.replaceFrom = replaceFrom;
    }

    public CursorPos getReplaceTo() {
        return replaceTo;
    }

    public void setReplaceTo(CursorPos replaceTo) {
        this.replaceTo = replaceTo;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public List<ContextAssistChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<ContextAssistChoice> choices) {
        this.choices = choices;
    }





}
