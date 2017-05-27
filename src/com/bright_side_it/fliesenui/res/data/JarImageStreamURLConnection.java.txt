package generated.fliesenui.core;

import generated.fliesenui.core.FLUIScreenManagerListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class JarImageStreamURLConnection extends URLConnection {
    private String pathInJar;
    private String contentType = "";
	private FLUIScreenManagerListener screenManagerListener;
	private long length = 0;
	private InputStream imageInputStream;
    
    public JarImageStreamURLConnection(URL url, String pathInJar, FLUIScreenManagerListener screenManagerListener) {
        super(url);
        this.pathInJar = pathInJar;
		this.screenManagerListener = screenManagerListener;
        int pos = pathInJar.lastIndexOf(".");
        if (pos >= 0){
        	contentType = pathInJar.substring(pos + 1);
        }
    }
    
	@Override
    public void connect() throws IOException {
        if (connected) {
            return;
        }
        
        InputStream inputStream = null;
        try{
        	inputStream = getResourceInputStream(pathInJar);
        	length = readLength(inputStream);
        	imageInputStream = getResourceInputStream(pathInJar);
        } catch (Exception e){
        	screenManagerListener.onError(new Exception("Could not open image", e));
        	throw new IOException(e);
        } finally {
        	if (inputStream != null){
        		inputStream.close();
        	}
        }
        
        connected = true;
    }
    
    
	private long readLength(InputStream inputStream) throws IOException {
		long length = 0;
        int readBytes;
        byte[] buffer = new byte[4096];
        while ((readBytes = inputStream.read(buffer)) > 0) {
            length += readBytes;
        }
        return length;
    }

    
    private InputStream getResourceInputStream(String location) throws Exception{
        try {
        	return this.getClass().getResourceAsStream(location);
        } catch (Exception e) {
            throw new Exception("Could access location '" + location + "'.", e);
        }
    }
    
    @Override
    public String getHeaderField(String name) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            return getContentType();
        } else if ("Content-Length".equalsIgnoreCase(name)) {
            return "" + getContentLength();
        }
        return null;
    }
    @Override
    public String getContentType() {
        return "image/" + contentType;
    }
    @Override
    public int getContentLength() {
        return (int)length;
    }
    public long getContentLengthLong() {
        return length;
    }
    @Override
    public boolean getDoInput() {
        return true;
    }
    @Override
    public InputStream getInputStream() throws IOException {
        connect();
        if (imageInputStream == null){
            return new ByteArrayInputStream(new byte[0]);
        }
        return imageInputStream;
    }
    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }
    @Override
    public java.security.Permission getPermission() throws IOException {
        return null;
    }
}
