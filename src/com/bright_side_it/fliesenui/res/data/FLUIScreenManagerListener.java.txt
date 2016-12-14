package generated.fliesenui.core;

public interface FLUIScreenManagerListener {
	
	void onError(Throwable error);
	
	void onWarning(Throwable warning);
	
	void onLogDebug(String message);
	
	void onWebViewConsoleLog(String message);
	
	/**
	 * called on each request, so that all requests can be logged or event saved and "played back" later by sending them to the manager again 
	 */
	void onRequest(FLUIRequest request);
	
	FLUIImageStream getCustomImageStream(String imageStreamID);

	FLUIFileStream getFileStream(String fileStreamID);
}
