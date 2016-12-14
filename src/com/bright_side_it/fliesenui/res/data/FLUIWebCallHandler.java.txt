package generated.fliesenui.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FLUIWebCallHandler {
	private static final int MAX_STATIC_STRING_CACHE_SIZE = 100;
	private static Map<String, String> staticStringCache = new HashMap<>();
	private static final String IMAGE_STREAM_REQUEST_PREFIX = "imagestream/";
	private static final String DOWNLOAD_REQUEST_PREFIX = "download/";
	private static final String CHARSET = "UTF-8";
	private FLUIScreenManagerInterface screenManager;

	protected FLUIWebCallHandler(FLUIScreenManagerInterface screenManager){
		this.screenManager = screenManager;
	}
	
	protected void handle(FLUIWebCall webCall, String method, String requestPath) throws Exception{
		if ("GET".equals(method)){
			handleGet(webCall, requestPath);
		} else if ("POST".equals(method)){
			handlePost(webCall);
		} else {
			throw new Exception("unknown request method: " + method);
		}
	}
	
	protected void handleUpload(FLUIWebCall webCall, String requestJSON, String uploadFilename, InputStream uploadFileInputStream) throws Exception{
		webCall.log("new upload request");
		try{
			webCall.log("handlePost: requestJSON = >>" + requestJSON + "<<");
			
			String replyJSONString = screenManager.onRequest(requestJSON, uploadFilename, uploadFileInputStream);
			webCall.log("handlePost: replyJSONString = >>" + replyJSONString + "<<");
			writeStringToStream(replyJSONString, webCall.getResponseOutputStream());
		} catch (Exception e){
			screenManager.getListener().onError(e);
		} finally {
			uploadFileInputStream.close();
		}
	}
	
	private void handleGet(FLUIWebCall webCall, String requestPath) throws Exception {
		webCall.log("new request");
		
		String contentType = "text/html;charset=utf-8";
		
		String usePathInfo = requestPath;
		if (usePathInfo.startsWith("/")){
			usePathInfo = usePathInfo.substring(1);
		}

		webCall.log("handle: usePathInfo = '" + usePathInfo + "'");
		
		String resultString = null;
		if (usePathInfo.startsWith(IMAGE_STREAM_REQUEST_PREFIX)){
			String imageStreamID = usePathInfo.substring(IMAGE_STREAM_REQUEST_PREFIX.length());
			webCall.log("request for image stream >>" + imageStreamID + "<<");
			FLUIImageStream imageStream = screenManager.getCustomImageStream(imageStreamID);
			if (imageStream != null){
				webCall.setReponseContentLength(imageStream.getLength());
				webCall.setResponseContentType("image/" + imageStream.getContentType());
				writeAllBytesToStream(imageStream.getInputStream(), webCall.getResponseOutputStream());
				webCall.log("send data for image stream >>" + imageStreamID + "<<. Length = " + imageStream.getLength() + ", content type = '" + imageStream.getContentType() + "'");
				imageStream.getInputStream().close();
			} else {
				new Exception("No image stream for ID '" + imageStreamID + "'").printStackTrace();
			}
		} else if (usePathInfo.startsWith(DOWNLOAD_REQUEST_PREFIX)){
			String imageStreamID = usePathInfo.substring(DOWNLOAD_REQUEST_PREFIX.length());
			FLUIFileStream fileStream = screenManager.getFileStream(imageStreamID);
			if (fileStream != null){
				if ((fileStream.getErrorMessage() != null) && (!fileStream.getErrorMessage().isEmpty())){
					webCall.setResponseContentType(contentType);
					writeStringToStream("<html><body><h1>Error: " + fileStream.getErrorMessage() + "</h1></body></html>", webCall.getResponseOutputStream());
				} else if (fileStream.getInputStream() != null){
					webCall.setResponseContentType("application/octet-stream"); 
					if (fileStream.getLength() != null){
						webCall.setReponseContentLength(fileStream.getLength());
					}
					if (fileStream.getFilename() != null){
						webCall.setReponseHeader("content-disposition", "attachment; filename=\"" + fileStream.getFilename() +"\"");
					}
					writeAllBytesToStream(fileStream.getInputStream(), webCall.getResponseOutputStream());
					try{
						fileStream.getInputStream().close(); 
					} catch (Exception inputStreamCloseExceptionIgnored){
					}
				}
			}
		} else if ((usePathInfo.endsWith(".png")) || (usePathInfo.endsWith(".jpg")) || (usePathInfo.endsWith(".jpeg")) || (usePathInfo.endsWith(".gif"))){
			int pos = usePathInfo.lastIndexOf(".");
			String ending = usePathInfo.substring(pos + 1);
			webCall.log("ending = >>" + ending + "<<");
			webCall.setResponseContentType("image/" + ending);
			try {
				screenManager.writeResource(usePathInfo, webCall.getResponseOutputStream());
			} catch (Exception e) {
				throw new Exception("Path '" + usePathInfo + "': Could not write resouce: " + e);
			}
		} else if (usePathInfo.endsWith(".html") || (usePathInfo.endsWith(".js")) || (usePathInfo.endsWith(".css")) || (usePathInfo.endsWith(".svg")) || (usePathInfo.isEmpty())){
			
			if (usePathInfo.endsWith(".css")){
				contentType = "text/css;charset=utf-8";
			} else if (usePathInfo.endsWith(".svg")){
				contentType = "image/svg+xml";
			}
			
			String cachedResultString = staticStringCache.get(usePathInfo);
			if (cachedResultString == null){
				if (usePathInfo.isEmpty()){
					resultString = screenManager.getStartWebPageAsString();
				} else {
					resultString = screenManager.getResourceAsString(usePathInfo);
				}
				if (resultString == null){
					webCall.log("WARNING: (#1) Path '" + usePathInfo + "': warning:  data could not be read");
				} else {
					webCall.log("INFO: Path '" + usePathInfo + "': " + resultString.length() + " characters");
				}
				if (staticStringCache.keySet().size() < MAX_STATIC_STRING_CACHE_SIZE){
					staticStringCache.put(usePathInfo, resultString);
				}
			} else {
				resultString = cachedResultString;
			}
			
			webCall.setResponseContentType(contentType);
			if (resultString != null){
				writeStringToStream(resultString, webCall.getResponseOutputStream());
			}
		} else {
			webCall.log("WARNING: Path '" + usePathInfo + "': This format is not handled yet");
		}
	}

	private void writeStringToStream(String string, OutputStream outputStream) throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(CHARSET));
		writeAllBytesToStream(inputStream, outputStream);
	}
	
	private void writeAllBytesToStream(InputStream inputStream, OutputStream outputStream) throws Exception {
		int readBytes;
		byte[] buffer = new byte[4096];
		while ((readBytes = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, readBytes);
		}
	}
	
	private void handlePost(FLUIWebCall webCall) throws IOException {
		webCall.log("new post request");
		try{
			String contentType = "application/json;charset=utf-8";
			webCall.setResponseContentType(contentType);
			webCall.log("handlePost: content length = "  + webCall.getRequestContentLength());
			InputStream inputStream = webCall.getRequestInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			writeAllBytesToStream(inputStream, buffer);
			String requestJSON = new String(buffer.toByteArray(), "UTF-8");
			webCall.log("handlePost: requestJSON = >>" + requestJSON + "<<");
			
			String replyJSONString = screenManager.onRequest(requestJSON, null, null);
			webCall.log("handlePost: replyJSONString = >>" + replyJSONString + "<<");
			writeStringToStream(replyJSONString, webCall.getResponseOutputStream());
		} catch (Exception e){
			screenManager.getListener().onError(e);
		}
	}



}
