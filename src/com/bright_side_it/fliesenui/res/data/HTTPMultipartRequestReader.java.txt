package generated.fliesenui.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HTTPMultipartRequestReader {
	private static final String LINEBREAK = "\r\n";
	private static final String UTF8 = "UTF-8";
	private static final String HEADER_KEY_FILENAME = "filename";
	private static final String HEADER_KEY_FORM_ITEM_NAME = "name";

	private int pos = 0;
	private byte[] bytes;

	public class RequestPart{
		private String filename;
		private byte[] bytes;

		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

	}

	private int indexOf(int startPos, byte[] outerArray, byte[] mark) {
		for(int i = startPos; i < outerArray.length - mark.length + 1; i ++) {
			boolean found = true;
			for(int j = 0; j < mark.length; ++j) {
				if (outerArray[i+j] != mark[j]) {
					found = false;
					break;
				}
			}
			if (found) return i;
		}
		return -1;
	}

	/**
	 *
	 * @param mark
	 * @return bytes until mark or null if the mark is not found
	 */
	private byte[] readUntil(byte[] mark){
		int foundPos = indexOf(pos, bytes, mark);
		if (foundPos < 0){
			return null;
		}
		int start = pos;
		pos = foundPos + mark.length;
		return Arrays.copyOfRange(bytes, start, foundPos);
	}

	/**
	 * @param mark
	 * @return bytes until mark
	 * @throws UnsupportedEncodingException
	 */
	private byte[] readBytesUntilUTF8(String mark) throws Exception{
		byte[] markBytes = mark.getBytes(UTF8);
		int foundPos = indexOf(pos, bytes, markBytes);
		if (pos < 0){
			return null;
		}
		int start = pos;
		pos = foundPos + markBytes.length;
		return Arrays.copyOfRange(bytes, start, foundPos);
	}

	private List<byte[]> getByteParts() throws Exception{
		int linebreakBytesLength = LINEBREAK.getBytes(UTF8).length;

		byte[] boundary = readBytesUntilUTF8(LINEBREAK);
		List<byte[]> parts = new ArrayList<>();

		boundary = joinArrays(LINEBREAK.getBytes(UTF8), boundary);

		byte[] part = readUntil(boundary);
		while(part != null){
			parts.add(part);
			pos += linebreakBytesLength;
			part = readUntil(boundary);
		}

		return parts;
	}

	private byte[] joinArrays(byte[] bytes1, byte[] bytes2) {
		byte[] result = new byte[bytes1.length + bytes2.length];
		int pos = 0;
		for (int i = 0; i < bytes1.length; i++){
			result[pos] = bytes1[i];
			pos ++;
		}
		for (int i = 0; i < bytes2.length; i++){
			result[pos] = bytes2[i];
			pos ++;
		}
		return result;
	}

	private Map<String, RequestPart> getRequestPartsMap(List<byte[]> byteParts) throws Exception{
		Map<String, RequestPart> result = new TreeMap<>();
		int partIndex = 1;
		byte[] linebreakBytes = "\r\n".getBytes(UTF8);
		for (byte[] part: byteParts){
			try{
				int headerLineEnd = indexOf(0, part, linebreakBytes);
				if (headerLineEnd < 0){
					throw new Exception("Could not read header line");
				}
				headerLineEnd += linebreakBytes.length;
				String headerLine = new String(Arrays.copyOfRange(part, 0, headerLineEnd), UTF8);
				Map<String, String> keyValueMap = toKeyValueMap(headerLine);
				int dataStart = headerLineEnd;
				String filename = keyValueMap.get(HEADER_KEY_FILENAME);
				String formItemName = keyValueMap.get(HEADER_KEY_FORM_ITEM_NAME);
				if (filename != null){ //: if it is a file skip the next line which contains the content type
					dataStart = indexOf(headerLineEnd, part, linebreakBytes);
					dataStart += linebreakBytes.length;
				}
				dataStart += linebreakBytes.length; //:there is another blank line before the data

				if (formItemName == null){
					throw new Exception("Could not find form item name");
				}

				RequestPart requestPart = new RequestPart();
				requestPart.setBytes(Arrays.copyOfRange(part, dataStart, part.length));
				requestPart.setFilename(filename);

				result.put(formItemName, requestPart);
			} catch (Exception e){
				throw new Exception("Could not process part # " + partIndex, e);
			}
			partIndex ++;
		}
		return result;
	}


	private Map<String, String> toKeyValueMap(String headerLine) throws Exception {
		String expectedStart = "Content-Disposition: form-data;";
		if (!headerLine.startsWith(expectedStart)){
			throw new Exception("Unexpected header line start: >>" + headerLine + "<<. Expected: >>" + expectedStart + "<<");
		}
		String rest = headerLine.substring(expectedStart.length()).trim();
		Map<String, String> result = new TreeMap<>();
		for (String item: rest.split(";")){
			int pos = item.indexOf("=");
			if (pos < 0){
				throw new Exception("Could not find '=' in item '" + item + "'");
			}
			String key = item.substring(0, pos).trim();
			String value = item.substring(pos + 1);

			if ((value.startsWith("\"")) && (value.endsWith("\""))){
				value = value.substring(1, value.length() - 1);
			}
			result.put(key, value);
		}
		return result;
	}

	private void writeAllBytesToStream(InputStream inputStream, OutputStream outputStream, long maximumLength) throws MaximumLengthExceededException, Exception {
		int readBytes;
		byte[] buffer = new byte[4096];
		long writtenLength = 0;
		while ((readBytes = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, readBytes);
			writtenLength += readBytes;
			if (writtenLength > maximumLength){
				throw new MaximumLengthExceededException("There are more than " + maximumLength + " bytes in the input stream");
			}
		}
	}

	public Map<String, RequestPart> getRequestParts(InputStream inputStream, int maximumAllowedLength) throws MaximumLengthExceededException, Exception{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		writeAllBytesToStream(inputStream, bytes, maximumAllowedLength);
		return getRequestParts(bytes.toByteArray());
	}

	public Map<String, RequestPart> getRequestParts(byte[] bytes) throws Exception{
		this.bytes = bytes;
		List<byte[]> parts = getByteParts();
		Map<String, RequestPart> requestPartsMap = getRequestPartsMap(parts);
		return requestPartsMap;
	}

	public byte[] getRawBytes(){
		return bytes;
	}

	public class MaximumLengthExceededException extends Exception{
		private static final long serialVersionUID = 1681747304241369536L;
		public MaximumLengthExceededException(String message) {
			super(message);
		}
	}

}
