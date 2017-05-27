package generated.fliesenui.core;

import generated.fliesenui.core.FLUIKeyEvent.EventType;
import generated.fliesenui.core.FLUIKeyEvent.KeyType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FLUIUtil {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");

	private static final String ENCODING = "UTF-8";
	private static final Map<Integer, KeyType> KEY_CODE_TO_KEY_TYPE_MAP = createKeyCodeToKeyTypeMap();
	
	public static void writeFile(File file, String text) throws Exception{
		writeFile(file, text, false);
	}
	
	public static void writeFile(File file, String text, boolean append) throws Exception{
		Writer out = null;
		try{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING));
			out.append(text);
			out.flush();
		} catch (Exception e){
			throw e;
		} finally {
			if (out != null){
				out.close();
			}
		}
	}
	
	public static String readFile(File file) throws Exception{
	    StringBuilder result = new StringBuilder();
	    BufferedReader in = null;
	    try {
	    	in = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
	    	String line;
	    	while ((line = in.readLine()) != null) {
	    		result.append(line);
	    		result.append("\n");
			}
	    } catch (Exception e){
	    	throw e;
	    } finally {
	    	in.close();
	    }
	    return result.toString();
	}
    
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
    	if (request == null){
    		return null;
    	}
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

    public static void saveActionRecording(FLUIActionRecording recording, File file) throws Exception{
        writeFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(recording));
    }

    public static FLUIActionRecording loadActionRecording(File file) throws Exception{
    	String json = readFile(file);
    	return new Gson().fromJson(json, FLUIActionRecording.class);
    }
    
    public static FLUIKeyEvent createFLUIKeyEvent(KeyModifier keyModifier, String keyCharString, Double keyCode, String editorText, int line, int posInLine, String info) throws Exception{
		FLUIKeyEvent result = new FLUIKeyEvent();
		result.setControl(keyModifier.isControl());
		result.setShift(keyModifier.isShift());
		result.setAlt(keyModifier.isAlt());
		result.setMeta(keyModifier.isMeta());
		result.setEditorText(editorText);
		result.setEventType(parseEventType(keyModifier.getEventType()));
		if ((keyCharString != null) && (!keyCharString.isEmpty())){
			result.setKeyChar(keyCharString.charAt(0));	
		}
		if (keyCode != null){
			result.setKeyCode(keyCode.intValue());
			result.setKeyType(KEY_CODE_TO_KEY_TYPE_MAP.get(keyCode.intValue()));
		}
		result.setLine(line);
		result.setPosInLine(posInLine);
		result.setInfo(info);
		
		return result;
	}

	public static EventType parseEventType(String eventType) throws Exception{
		if ("keydown".equals(eventType)){
			return EventType.KEY_DOWN;
		} else if ("keypress".equals(eventType)){
			return EventType.KEY_PRESS;
		} else {
			throw new Exception("Unknown key event type: '" + eventType + "'");
		}
	}

	private static Map<Integer, KeyType> createKeyCodeToKeyTypeMap() {
		Map<Integer, KeyType> result = new HashMap<>();
		result.put(38, KeyType.CURSOR_UP);
		result.put(40, KeyType.CURSOR_DOWN);
		result.put(37, KeyType.CURSOR_LEFT);
		result.put(39, KeyType.CURSOR_RIGHT);
		result.put(13, KeyType.ENTER);
		result.put(9, KeyType.TAB);
		result.put(27, KeyType.ESC);
		result.put(33, KeyType.PAGE_DOWN);
		result.put(34, KeyType.PAGE_UP);
		result.put(112, KeyType.F1);
		result.put(113, KeyType.F2);
		result.put(114, KeyType.F3);
		result.put(115, KeyType.F4);
		result.put(116, KeyType.F5);
		result.put(117, KeyType.F6);
		result.put(118, KeyType.F7);
		result.put(119, KeyType.F8);
		result.put(120, KeyType.F9);
		result.put(121, KeyType.F10);
		result.put(122, KeyType.F11);
		result.put(123, KeyType.F12);

		return result;
	}

	public static boolean matchesKeyEvent(FLUIKeyEvent event, char character, boolean control, boolean shift, boolean alt){
		if ((event.getKeyChar() == null) || (event.isAlt() != alt) || (event.isShift() != shift) || (event.isControl() != control)){
			return false;
		}
		return (event.getKeyChar().charValue() == character);
	}

	public static boolean matchesKeyEvent(FLUIKeyEvent event, FLUIKeyEvent.KeyType keyType, boolean control, boolean shift, boolean alt){
		if ((event.getKeyType() == null) || (event.isAlt() != alt) || (event.isShift() != shift) || (event.isControl() != control)){
			return false;
		}
		return (event.getKeyType() == keyType);
	}


}
