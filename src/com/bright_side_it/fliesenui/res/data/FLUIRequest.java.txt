package generated.fliesenui.core;

import java.util.Map;

public class FLUIRequest {
    private String screenID;
    private String action;
    private Map<String, Object> parameters;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getScreenID() {
        return screenID;
    }

    public void setScreenID(String screenID) {
        this.screenID = screenID;
    }

}
