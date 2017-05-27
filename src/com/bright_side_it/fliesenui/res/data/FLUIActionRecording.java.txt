package generated.fliesenui.core;

import java.util.List;

public class FLUIActionRecording {
	private List<FLUIRequest> requests;
	private List<FLUIReplyDTO> replies;

	public synchronized List<FLUIRequest> getRequests() {
		return requests;
	}
	public synchronized void setRequests(List<FLUIRequest> requests) {
		this.requests = requests;
	}
	public synchronized List<FLUIReplyDTO> getReplies() {
		return replies;
	}
	public synchronized void setReplies(List<FLUIReplyDTO> replies) {
		this.replies = replies;
	}
	
	
}
