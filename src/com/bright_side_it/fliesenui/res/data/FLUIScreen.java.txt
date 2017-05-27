package generated.fliesenui.core;

import java.io.InputStream;

public interface FLUIScreen {

	String getID();
	
	FLUIAbstractReply onFLUIRequest(boolean recordMode, FLUIRequest request, String uploadedFileName, InputStream uploadedFileInputStream) throws Exception;
}
