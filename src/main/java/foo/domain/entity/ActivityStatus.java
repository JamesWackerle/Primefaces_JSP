package foo.domain.entity;

import java.util.HashMap;

public class ActivityStatus {

	private int status;
	private String statusPicture;
	private String statusText;

	// Hash Map that stores Status to Text pairs
	public static final HashMap<Integer, String> STATUS_TO_TEXT = new HashMap<Integer, String>();

	public ActivityStatus(int status) {
		super();
		this.status = status;
		initializeStatusToText();

		this.statusText = STATUS_TO_TEXT.get(status);

	}

	public static void initializeStatusToText() {
		STATUS_TO_TEXT.put(5, "Free");
		STATUS_TO_TEXT.put(10, "Planned");
		STATUS_TO_TEXT.put(15, "Released");
		STATUS_TO_TEXT.put(20, "Completed");
		STATUS_TO_TEXT.put(25, "Costed");
		STATUS_TO_TEXT.put(30, "Closed");
		STATUS_TO_TEXT.put(35, "Canceled");
		STATUS_TO_TEXT.put(40, "Rejected");
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusPicture() {
		return statusPicture;
	}

	public void setStatusPicture(String statusPicture) {
		this.statusPicture = statusPicture;
	}

	@Override
	public String toString() {
		return "Status [statusText=" + statusText + ", status=" + status + ", statusPicture=" + statusPicture + "]";
	}
}
