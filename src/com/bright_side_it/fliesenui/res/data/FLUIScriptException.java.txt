package generated.fliesenui.core;

public class FLUIScriptException extends RuntimeException{
	private static final long serialVersionUID = 7623842154205825031L;
	private String command;

	public FLUIScriptException(String command, Exception e) {
		super(e);
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
}
