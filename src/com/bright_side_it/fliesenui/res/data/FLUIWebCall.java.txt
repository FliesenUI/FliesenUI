package generated.fliesenui.core;

import java.io.InputStream;
import java.io.OutputStream;

public interface FLUIWebCall {
	InputStream getRequestInputStream() throws Exception;
	long getRequestContentLength();
	void log(String message);
	void setReponseContentLength(long length);
	void setResponseContentType(String contentType);
	OutputStream getResponseOutputStream() throws Exception;
	void setReponseHeader(String name, String value);
}
