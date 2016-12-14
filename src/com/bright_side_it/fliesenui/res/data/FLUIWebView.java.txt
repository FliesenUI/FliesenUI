package generated.fliesenui.core;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import netscape.javascript.JSObject;

public class FLUIWebView extends Region {
    final WebView webView = new WebView();
    final WebEngine webEngine = webView.getEngine();
    private int width;
    private int height;
    private static final String PROTOCOL_PREFIX_UNSAFE = "unsafe";
    private static final String PROTOCOL_NAME_IMAGE_STREAM = "imagestream";

    private String currentScreenID;
	private FLUIScreenManager screenManager;
	private boolean singlePageApp = false;
	private boolean externalURL = false;

    protected FLUIWebView(Window stage, int width, int height, FLUIScreenManager screenManager) {
        this.width = width;
        this.height = height;
        this.screenManager = screenManager;
        getStyleClass().add("browser");

        log("WebView version:" + webView.getEngine().getUserAgent());

        webView.setContextMenuEnabled(false);
        
        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			@Override
			public URLStreamHandler createURLStreamHandler(String protocol) {
				log("protocol = '" + protocol + "'");
				if ((PROTOCOL_NAME_IMAGE_STREAM.equals(protocol)) || (PROTOCOL_PREFIX_UNSAFE.equals(protocol))){
					return new ImageStreamProtocolHandler(protocol);
				}
				return null;
			}
		});
        

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
            	log("newState = " + newState + "; currentScreenID = " + currentScreenID);
            	
                if ((newState == State.SUCCEEDED) && (!externalURL)){
                	log("succeeded. Location = '" + webEngine.getLocation() + "'");
                	
                 	if (!singlePageApp){
                		String locationScreenID = readScreenIDFromLocation(webEngine.getLocation());
                		log("parsed screen ID from location= '" + locationScreenID + "'");
                		if (locationScreenID != null){
                			currentScreenID = locationScreenID;
                			log("new location loaded screen id = '" + currentScreenID + "'");
                		}
                	}
                	                	
                    JSObject win = (JSObject) webEngine.executeScript("window");
                    win.setMember("console", new Console());
                    win.setMember("webView", new WebViewCommandHandler(stage));
                    win.setMember("screenManager", screenManager);
                    if (currentScreenID != null) {
                    	log("calling executeOnLoadWhenControllerIsReady");
                    	webEngine.executeScript(currentScreenID + "$executeOnLoadWhenControllerIsReady();");
                    	log("finished calling executeOnLoadWhenControllerIsReady");
                    }
                }
            }

        });
        
        getChildren().add(webView);
    }

	private String readScreenIDFromLocation(String location) {
		int pos = location.lastIndexOf("/");
		if (pos < 0){
			return null;
		}
		String rest = location.substring(pos + 1);
		pos = rest.indexOf("_");
		if (pos < 0){
			return null;
		}
		return rest.substring(0, pos);
	}
    
    protected void openScreen(String screenID, String filename, String urlSuffix, boolean singlePageApp) {
//        currentScreen = screen;
        this.singlePageApp = singlePageApp;
        currentScreenID = screenID;
        String location = "/generated/fliesenui/web/" + filename;
        log("location = >>" + location + "<<");
        String url;
        try {
            url = FLUIWebView.class.getResource(location).toExternalForm();
            log("file url = >>" + url + "<<");
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not open resource '" + location + "'", e);
        }
        if (urlSuffix != null){
            url = url + urlSuffix;
        }
        log("loading url = >>" + url + "<<");
        webEngine.load(url);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(webView, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return width;
    }

    @Override
    protected double computePrefHeight(double width) {
        return height;
    }

    public String executeWithResultString(String command) throws FLUIScriptException {
        log("Executing command >>" + command + "<<");
        try {
            Object result = webEngine.executeScript(command);
            return (String) result;
        } catch (netscape.javascript.JSException e) {
        	log("Executing command failed: (" + currentScreenID + ") >>" + command + "<<: " + e);
            throw new FLUIScriptException(command, e);
        }
    }

    private void log(String message) {
        System.out.println("FLUIWebView> " + message);
    }

    public String escapeString(String string) {
        String result = string.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
        // log("Input  string >>" + string + "<<");
        // log("Result string >>" + result + "<<");

        return result;
    }
    
    public FLUIScreenManager getScreenManager() {
		return screenManager;
	}
    
    public class Console {
        public void log(String message) {
            System.out.println("Javascript console> " + message);
        }
    }

    public class WebViewCommandHandler {
    	private Window stage;

		public WebViewCommandHandler(Window stage) {
			this.stage = stage;
		}
		
		public double getAvailWidth(){
			 return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
		}

		public int getAvailHeight(){
			return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
		}
		
		public double getScreenWidth(){
			return java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		}
		
		public double getScreenHeight(){
			return java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		}
		
		public void openURL(String url, boolean newWindow) {
    		System.out.println("WebViewCommandHandler open url '" + url + "'");
    		
    		if (newWindow){
    			try {
    				Desktop.getDesktop().browse(new URI(url));
    			} catch (Exception e) {
    				if (screenManager != null){
    					screenManager.getListener().onError(new Exception("Could not open URL in new window '" + url + "'", e));
    				}
    			}
    		} else{
    			externalURL = true;
    			webEngine.load(url);
    		}
    	}
    	
    	public void fileUpload(String uploadRequestJSON, String uploadFinishedRequestJSON) {
    		if (screenManager != null){
    	    	FileChooser chooser = new FileChooser();
    	    	File selectedFile = chooser.showOpenDialog(stage);
    	    	if (selectedFile == null){
    	    		return;
    	    	}
    	    	
    	    	try (FileInputStream fileInputStream = new FileInputStream(selectedFile)){
    	    		screenManager.onRequest(uploadRequestJSON, selectedFile.getName(), fileInputStream);
    	    	} catch (Exception e){
    	    		screenManager.getListener().onError(new Exception("Error during file upload via WebView"));
    	    		return;
    	    	}
    	    	screenManager.onRequest(uploadFinishedRequestJSON, null, null);
    		}
    	}
    	
    	public void downloadFile(String fileStreamID){
    		log("downloadFile: '" + fileStreamID + "'");
    		if (screenManager != null){
    			FLUIFileStream fileStream = screenManager.getFileStream(fileStreamID);
    			if (fileStream != null){
    				if ((fileStream.getErrorMessage() != null) && (!fileStream.getErrorMessage().isEmpty())){
    					Alert alert = new Alert(AlertType.ERROR);
    					alert.setTitle("Download error");
    					alert.setHeaderText("Could not download file with ID '" + fileStreamID + "'");
    					alert.setContentText(fileStream.getErrorMessage());
    					alert.showAndWait();
    				} else if (fileStream.getInputStream() != null){
    	    	    	FileChooser chooser = new FileChooser();
    	    	    	if (fileStream.getFilename() != null){
    	    	    		chooser.setInitialFileName(fileStream.getFilename());
    	    	    	}
    	    	    	File selectedFile = chooser.showSaveDialog(stage);
    	    	    	
    	    	    	if (selectedFile == null){
    	    	    		return;
    	    	    	}
    	    	    	try (FileOutputStream outputStream = new FileOutputStream(selectedFile)){
    	    	    		writeAllBytesToStream(fileStream.getInputStream(), outputStream);
    	    	    	} catch (Exception e){
    	    	    		screenManager.getListener().onError(new Exception("Could not write file '" + selectedFile.getAbsolutePath() + "'", e));
    	    	    	} finally {
    	    	    		try{
    	    	    			fileStream.getInputStream().close();
    	    	    		} catch (Exception closingInputStreamExceptionIgnored){
    	    	    		}
    	    	    	}
    				}
    			} else {
    				log("downloadFile: fileStream is null");
    			}
    		}
    	}
    }
    
    private class ImageStreamProtocolHandler extends URLStreamHandler {
    	private String protocol;

		public ImageStreamProtocolHandler(String protocol) {
    		this.protocol = protocol;
		}

		@Override
    	protected URLConnection openConnection(URL url) throws IOException {
//    		log("opening connection with image stream protocol handler: '" + url + "' for protocol '" + protocol + "'");
    		if ((url != null) && (screenManager != null)){
    			log("openConnection: path = >>" + url.getPath() + "<<");
    			String path = url.getPath();
    			if (PROTOCOL_PREFIX_UNSAFE.equals(protocol)){
    				path = path.substring(PROTOCOL_NAME_IMAGE_STREAM.length() + 1);
    			}
    			return new ImageStreamURLConnection(url, screenManager, path);
    		}
    		return null;
    	}

    	private void log(String message) {
    		System.out.println("ImageStreamProtocolHandler> " + message);
    	}
    }
    
	private void writeAllBytesToStream(InputStream inputStream, OutputStream outputStream) throws Exception {
		int readBytes;
		byte[] buffer = new byte[4096];
		while ((readBytes = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, readBytes);
		}
	}

}
