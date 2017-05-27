package generated.fliesenui.core;

public class FLUIKeyEvent {
	public enum EventType{KEY_PRESS, KEY_DOWN}
	public enum KeyType{CURSOR_UP, CURSOR_DOWN, CURSOR_LEFT, CURSOR_RIGHT, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, TAB, ESC, PAGE_UP, PAGE_DOWN, ENTER};
	
	private boolean shift;
	private boolean alt;
	private boolean control;
	private boolean meta;
	private Integer keyCode;
	private Character keyChar;
	private EventType eventType;
	private String editorText;
	private int line;
	private int posInLine;
	private KeyType keyType;
	
	private String info;
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public boolean isShift() {
		return shift;
	}
	public void setShift(boolean shift) {
		this.shift = shift;
	}
	public boolean isAlt() {
		return alt;
	}
	public void setAlt(boolean alt) {
		this.alt = alt;
	}
	public boolean isControl() {
		return control;
	}
	public void setControl(boolean control) {
		this.control = control;
	}
	public boolean isMeta() {
		return meta;
	}
	public void setMeta(boolean meta) {
		this.meta = meta;
	}
	public int getKeyCode() {
		return keyCode;
	}
	public Character getKeyChar() {
		return keyChar;
	}
	public void setKeyChar(Character keyChar) {
		this.keyChar = keyChar;
	}
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	public void setKeyCode(Integer keyCode) {
		this.keyCode = keyCode;
	}
	public String getEditorText() {
		return editorText;
	}
	public void setEditorText(String editorText) {
		this.editorText = editorText;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public int getPosInLine() {
		return posInLine;
	}
	public void setPosInLine(int posInLine) {
		this.posInLine = posInLine;
	}
	public KeyType getKeyType() {
		return keyType;
	}
	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}
	@Override
	public String toString() {
		return "FLUIKeyEvent [keyCode=" + keyCode + ", keyChar=" + keyChar + ", keyType=" + keyType + ", eventType=" + eventType
				+ ", shift=" + shift + ", alt=" + alt + ", control=" + control + ", meta=" + meta + ", line=" + line + ", posInLine="
				+ posInLine + ", info=" + info + "]";
	}

}
