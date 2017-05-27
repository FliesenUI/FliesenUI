package generated.fliesenui.core;

public class KeyModifier {
	private boolean shift;
	private boolean alt;
	private boolean control;
	private boolean meta;
	private Integer keyCode;
	private String eventType;
	
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
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public void setKeyCode(Integer keyCode) {
		this.keyCode = keyCode;
	}

	
}
