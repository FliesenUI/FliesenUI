package generated.fliesenui.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

public class FLUIUtil {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");

    public static TextHighlighting createErrorTextHighlighting(int startLine, int startPosInLine, int endLine, int endPosInLine) {
        TextHighlighting result = new TextHighlighting();
        result.setStartLine(startLine);
        result.setStartPosInLine(startPosInLine);
        result.setEndLine(endLine);
        result.setEndPosInLine(endPosInLine);
        result.setStyle("textHighlightError");
        return result;
    }

    public static TextHighlighting createWarningTextHighlighting(int startLine, int startPosInLine, int endLine, int endPosInLine) {
        TextHighlighting result = new TextHighlighting();
        result.setStartLine(startLine);
        result.setStartPosInLine(startPosInLine);
        result.setEndLine(endLine);
        result.setEndPosInLine(endPosInLine);
        result.setStyle("textHighlightWarning");
        return result;
    }

    public static CursorPos createCursorPos(int line, int posInLine) {
        CursorPos result = new CursorPos();
        result.setLine(line);
        result.setPosInLine(posInLine);
        return result;
    }

    public static ContextAssistChoice createContextAssistChoice(String label, String text) {
        ContextAssistChoice result = new ContextAssistChoice();
        result.setLabel(label);
        result.setText(text);
        return result;
    }

    public static String toActionLogString(FLUIRequest request) {
        String json = new Gson().toJson(request);
        json = json.replace("\\", "\\\\").replace("\n", "\\n");
        return TIMESTAMP_FORMAT.format(new Date()) + ";" + request.getScreenID() + ";" + request.getAction() + ";" + json;
    }
    
//    public static String toURLParameter(Object object){
//    	String json = new Gson().toJson(object);
//    	byte[] bytes = null;
//    	try{
//    		bytes = json.getBytes("UTF-8");
//    	} catch (Exception e){
//    		throw new RuntimeException("could not get bytes of string '" + json + "' in UTF-8!");
//    	}
//    	String base64 = Base64.getEncoder().encodeToString(bytes);
//    	return base64.replace("+", "-").replace("/", "_").replace("=", "~");
//    }
    
    public static String reescapeEscapeCharacters(String string) {
        return string.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
    }
}
