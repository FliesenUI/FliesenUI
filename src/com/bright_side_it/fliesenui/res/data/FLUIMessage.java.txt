package generated.fliesenui.core;

public class FLUIMessage {
    public static int TYPE_ID_INFO_DIALOG = 101;
    public static int TYPE_ID_WARNING_DIALOG = 102;
    public static int TYPE_ID_ERROR_DIALOG = 103;
    public static int TYPE_ID_INFO_TOAST = 201;

    private String title;
    private String text;
    private int typeID;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }



}
