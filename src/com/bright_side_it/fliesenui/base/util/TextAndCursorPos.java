package com.bright_side_it.fliesenui.base.util;

public class TextAndCursorPos{
	private String text;
	private int line;
	private int posInLine;

	public TextAndCursorPos(String text, int line, int posInLine) {
		this.text = text;
		this.line = line;
		this.posInLine = posInLine;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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

	@Override
	public String toString() {
		return "TextAndCursorPos [text=" + text.replace("\n", "\\n") + ", line=" + line + ", posInLine=" + posInLine + "]";
	}
	
}
