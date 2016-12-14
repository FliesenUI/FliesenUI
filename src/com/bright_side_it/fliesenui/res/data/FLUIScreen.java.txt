package generated.fliesenui.core;

import java.io.InputStream;

public interface FLUIScreen {

	String getID();
	
	String onFLUIRequest(FLUIRequest request, String uploadedFileName, InputStream uploadedFileInputStream);
}
