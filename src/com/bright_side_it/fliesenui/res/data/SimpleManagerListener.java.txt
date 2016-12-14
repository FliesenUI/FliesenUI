package generated.fliesenui.core;

public class SimpleManagerListener implements FLUIScreenManagerListener{
	@Override
	public void onWarning(Throwable warning) {
		System.out.println("FLUIManager warning: " + warning);
		if (warning != null){
			warning.printStackTrace();
		}
	}
	
	@Override
	public void onRequest(FLUIRequest request) {
		System.out.println("FLUIManager request: " + FLUIUtil.toActionLogString(request));	
	}
	
	@Override
	public void onError(Throwable error) {
		System.err.println("FLUIManager error: " + error);
		if (error != null){
			error.printStackTrace();
		}
	}

	@Override
	public FLUIImageStream getCustomImageStream(String imageStreamID) {
		System.out.println("FLUIManager requested custom image with id '" + imageStreamID + "', but returned null because it is handeled by the 'SimpleManagerListener'");	
		return null;
	}

	@Override
	public FLUIFileStream getFileStream(String fileStreamID) {
		System.out.println("FLUIManager requested file with id '" + fileStreamID + "', but returned null because it is handeled by the 'SimpleManagerListener'");	
		return null;
	}

	@Override
	public void onLogDebug(String message) {
		System.out.println("FLUIManager debug info>" + message);
	}

	@Override
	public void onWebViewConsoleLog(String message) {
		System.out.println("FLUIManager WebView JS Console>" + message);		
	}
}
