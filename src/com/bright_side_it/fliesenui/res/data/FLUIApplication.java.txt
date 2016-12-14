package generated.fliesenui.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class FLUIApplication extends Application {
    private Scene scene;
    private FLUIWebView webView;
    private int height;
    private String title;
    private int width;
    private FLUIScreenManager screenManager;
	private Image icon;
	private Stage stage;
	
	public FLUIApplication(String title, int width, int height, FLUIScreenManager screenManager) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.screenManager = screenManager;
	}

    public void setIcon(Image icon) {
		this.icon = icon;
		if (stage != null){
			stage.getIcons().add(icon);
		}
	}
    
    @Override
    public void start(Stage stage) {
    	this.stage = stage;
        stage.setTitle(title);
        if (icon != null){
        	stage.getIcons().add(icon);
        }
        webView = new FLUIWebView(stage, width, height, screenManager);
        screenManager.setWebView(webView);
        scene = new Scene(webView, width, height, Color.web("#666970"));
        stage.setScene(scene);
        onStart(stage);
        stage.show();
		screenManager.openStartScreen();
    }

    public abstract void onStart(Stage stage);


    protected FLUIWebView getWebView() {
        return webView;
    }

    public FLUIScreenManager getScreenManager() {
        return screenManager;
    }
}
