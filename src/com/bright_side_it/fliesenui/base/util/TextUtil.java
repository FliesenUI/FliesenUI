package com.bright_side_it.fliesenui.base.util;

public class TextUtil {

    public static int toLine(String text, int posInText) {
        int line = 0;
        int currentPos = 0;
        for (char i : text.toCharArray()) {
            currentPos++;
            if (currentPos > posInText) {
                return line;
            }
            if (i == '\n') {
                line++;
            }
        }
        return line;
    }

    public static int toPosInLine(String text, int posInText) {
        int pos = text.lastIndexOf("\n", posInText);
        if (pos < 0) {
            return posInText;
        }
        if (pos == posInText) {
            //: special case: at the pos in text there is a \n. This means the cursor is at the end of the previous line
            if (pos == 0) {
                return 0;
            }
            int previousLineStart = text.lastIndexOf("\n", pos - 1);
            if (previousLineStart < 0) {
                return pos;
            }
            return posInText - previousLineStart - 1;
        }

        return posInText - pos - 1;
    }

    public static int toPosInText(String text, int line, int posInLine) {
        int lineBreakPos = 0;
        for (int i = 0; i < line; i++) {
            lineBreakPos = text.indexOf("\n", lineBreakPos);
            lineBreakPos++;
            if (lineBreakPos < 0) {
                return -1;
            }
            if (lineBreakPos > 0) {
                if (text.charAt(lineBreakPos - 1) == '\r') {
                    lineBreakPos++;
                }
            }
        }
        return lineBreakPos + posInLine;
    }

    public static int nextIndexOf(String text, int startPos, String... searchStrings) {
        int bestPos = -1;
        for (String i : searchStrings) {
            int pos = text.indexOf(i, startPos);
            if (pos >= 0) {
                if ((bestPos < 0) || (pos < bestPos)) {
                    bestPos = pos;
                }
            }
        }
        return bestPos;
    }

    public static int lastIndexOf(String text, int startPos, String... searchStrings) {
        int bestPos = -1;
        for (String i : searchStrings) {
            int pos = text.lastIndexOf(i, startPos);
            if (pos >= 0) {
                if ((bestPos < 0) || (pos > bestPos)) {
                    bestPos = pos;
                }
            }
        }
        return bestPos;
    }


    public static String markCharInString(String text, int pos) {
        String result;
        if (pos < 0) {
            return "";
        }
        if (pos >= text.length()) {
            return "??? ... pos > textlength";
        }

        int start = pos - 10;
        if (start < 0) {
            start = 0;
        }
        result = text.substring(start, pos) + "[ " + text.charAt(pos) + " ]";
        int endPos = pos + 10;
        if (endPos > text.length()) {
            endPos = text.length();
        }
        result += text.substring(pos + 1, endPos);

        return result.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
    }
    
	public static String toUppercaseAndUnderscore(String string) {
		StringBuilder result = new StringBuilder();
		boolean lastWasLowerCase = false;
		
		for (char i: string.toCharArray()){
			if (Character.isUpperCase(i)){
				if (lastWasLowerCase){
					result.append("_");
				}
			}
			result.append(Character.toUpperCase(i));
			lastWasLowerCase = Character.isLowerCase(i);
		}
		
		return result.toString();
	}
	
	public static String addSuffixIfMissing(String text, String suffix){
		if (text == null){
			return null;
		}
		if (text.endsWith(suffix)){
			return text;
		}
		return text + suffix;
	}

	public static String addPrefixIfMissing(String text, String prefix){
		if (text == null){
			return null;
		}
		if (text.startsWith(prefix)){
			return text;
		}
		return prefix + text;
	}
	
	public static String removePrefixIsExisting(String text, String prefix){
		if (text.startsWith(prefix)){
			return text.substring(prefix.length());
		}
		return text;
	}

	public static String removeSuffixIsExisting(String text, String suffix){
		if (text.endsWith(suffix)){
			return text.substring(0, text.length() - suffix.length());
		}
		return text;
	}


}
