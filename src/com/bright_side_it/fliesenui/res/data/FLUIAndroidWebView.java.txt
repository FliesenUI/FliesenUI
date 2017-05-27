package generated.fliesenui.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FLUIAndroidWebView extends WebView {
	private FLUIScreenManagerAndroid screenManager;
	private boolean onCreateCalled = false;
	private Activity activity;
	private boolean singlePageApp;
	private String currentScreenID;

	public FLUIAndroidWebView(Context context) {
		super(context);
	}

	public FLUIAndroidWebView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	private void showInfoText(Canvas canvas, String text, int textColor, int backgroundColor, int outlineColor){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Rect r = new Rect();
		canvas.getClipBounds(r);
		int cHeight = r.height();
		int cWidth = r.width();
		paint.setColor(backgroundColor);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

		paint.setColor(textColor);
		paint.setTextSize(60);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
		paint.getTextBounds(text, 0, text.length(), r);
		float x = cWidth / 2f - r.width() / 2f - r.left;
		float y = cHeight / 2f + r.height() / 2f - r.bottom;
		canvas.drawText(text, x, y, paint);

		paint.setStrokeWidth(30);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(outlineColor);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
		return;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isInEditMode()){
			showInfoText(canvas, "Fliesen UI Web View", Color.argb(255, 128, 128, 255), Color.WHITE, Color.argb(255, 128, 255, 255));
			return;
		}
		if (!onCreateCalled){
			showInfoText(canvas, "onCreate method was not called", Color.RED, Color.WHITE, Color.RED);
			return;
		}

		super.onDraw(canvas);
	}

	public void openScreen(String screenID, String filename, String urlSuffix, boolean singlePageApp) {
		this.singlePageApp = singlePageApp;
		currentScreenID = screenID;
		String url = "file:///android_asset/generated/fliesenui/web/" + filename;
		screenManager.getListener().onLogDebug("openScreen: url = >>" + url + "<<");
		if (urlSuffix != null){
			url = url + urlSuffix;
		}
		screenManager.getListener().onLogDebug("loading url = >>" + url + "<<");
		loadUrl(url);
	}

	public void onCreate(Activity activity, final FLUIScreenManagerAndroid screenManager) {
		onCreateCalled = true;
		this.activity = activity;
		this.screenManager = screenManager;
		getSettings().setJavaScriptEnabled(true);
		final Context context = getContext();
		setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
//				activity.setProgress(progress * 1000);
			}
		});
		setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				screenManager.getListener().onError(new Exception(description));
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				String info = "?";
				if (error != null){
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						info = error.getErrorCode() + ": " + error.getDescription();
					}
				}
				screenManager.getListener().onError(new Exception("Error: " + info));
				super.onReceivedError(view, request, error);
			}
		});

		addJavascriptInterface(new Console(), "console");
		addJavascriptInterface(screenManager, "screenManager");
		addJavascriptInterface(new WebViewCommandHandler(), "webView");

		screenManager.setWebView(this);
		screenManager.openStartScreen();

//		if (currentScreenID != null) {
//			log("calling executeOnLoadWhenControllerIsReady");
//			webEngine.executeScript(currentScreenID + "$executeOnLoadWhenControllerIsReady();");
//			log("finished calling executeOnLoadWhenControllerIsReady");
//		}
	}

	public void executeWithResultString(final String command) throws FLUIScriptException {
		screenManager.getListener().onLogDebug("Executing command >>" + command + "<<");
		post(new Runnable() {
			@Override
			public void run() {
				try {
					evaluateJavascript(command, null);
				} catch (Exception e) {
					screenManager.getListener().onError(new Exception("Executing command failed: (" + currentScreenID + ") >>" + command + "<<: " + e));
					throw new FLUIScriptException(command, e);
				}
			}
		});
	}
	
	public void fireEventOnBackPressed() {
		executeWithResultString("backButtonPressed();");
	}
	

	public class Console {
		@JavascriptInterface
		public void log(String message) {
			screenManager.getListener().onWebViewConsoleLog(message);
		}
	}


	public class WebViewCommandHandler {

		@JavascriptInterface
		public double getAvailWidth(){
			return getWidth();
		}

		@JavascriptInterface
		public int getAvailHeight(){
			return getHeight();
		}

		@JavascriptInterface
		public double getScreenWidth(){
			Display display = activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}

		@JavascriptInterface
		public double getScreenHeight(){
			Display display = activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}

		@JavascriptInterface
		public void openURL(String url, boolean newWindow) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}

		@JavascriptInterface
		public void fileUpload(String uploadRequestJSON, String uploadFinishedRequestJSON) {
			toast("implement me: WebViewCommandHandler.fileUpload");
		}

		@JavascriptInterface
		public void downloadFile(String fileStreamID){
			toast("implement me: WebViewCommandHandler.downloadFile");
		}
	}

	private void toast(String message){
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

}
