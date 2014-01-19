package foo.domain.entity;

import java.util.HashMap;

public class Status {

	private int status;
	private String statusPicture;
	private String statusText;

	// Hash map that stores Status to text for the tsclm901 table
	public static final HashMap<Integer, String> STATUS_TO_TEXT = new HashMap<Integer, String>();
	// Hash map that stores Status to text for the tsclm100 table
	public static final HashMap<Integer, String> STATUS_TO_TEXT_100 = new HashMap<Integer, String>();

	// Hash map that stores Status to picture for the tsclm901 table
	public static final HashMap<Integer, String> STATUS_TO_PICTURE = new HashMap<Integer, String>();
	// Hash map that stores Status to picture for the tsclm100 table
	public static final HashMap<Integer, String> STATUS_TO_PICTURE_100 = new HashMap<Integer, String>();

	public Status(int status) {
		super();
		this.status = status;
		initializeStatusToText();
		initializeStatusToPicture();
		initializeStatusToText100();
		initializeStatusToPicture100();

		this.statusText = STATUS_TO_TEXT_100.get(status);
		this.statusPicture = STATUS_TO_PICTURE_100.get(status);

	}

	public void initializeStatusToText() {
		STATUS_TO_TEXT.put(1, "REGISTERED");
		STATUS_TO_TEXT.put(2, "REGISTERED_WITH_WAIT");
		STATUS_TO_TEXT.put(3, "ASSIGNED");
		STATUS_TO_TEXT.put(4, "ASSIGNED_WITH_WAIT");
		STATUS_TO_TEXT.put(5, "TRANSFERED");
		STATUS_TO_TEXT.put(6, "SOLVED");
		STATUS_TO_TEXT.put(7, "ACCEPTED");
		STATUS_TO_TEXT.put(8, "INSTANT_TRANSFERED");
	}

	public void initializeStatusToPicture() {
		STATUS_TO_PICTURE.put(1, "button-record.png");
		STATUS_TO_PICTURE.put(2, "button-subtract.png");
		STATUS_TO_PICTURE.put(3, "button-add.png");
		STATUS_TO_PICTURE.put(4, "button-cross.png");
		STATUS_TO_PICTURE.put(5, "button-tick.png");
		STATUS_TO_PICTURE.put(6, "fave.png");
		STATUS_TO_PICTURE.put(7, "button-alert.png");
		STATUS_TO_PICTURE.put(8, "button-tick-alt.png");
	}

	public void initializeStatusToText100() {
		STATUS_TO_TEXT_100.put(5, "REGISTERED");
		STATUS_TO_TEXT_100.put(10, "ASSIGNED");
		STATUS_TO_TEXT_100.put(15, "IN_PROCESS");
		STATUS_TO_TEXT_100.put(20, "TRANSFERED");
		STATUS_TO_TEXT_100.put(25, "SOLVED");
		STATUS_TO_TEXT_100.put(30, "ACCEPTED");
	}

	public void initializeStatusToPicture100() {
		STATUS_TO_PICTURE_100.put(5, "button-record.png");
		STATUS_TO_PICTURE_100.put(10, "button-add.png");
		STATUS_TO_PICTURE_100.put(15, "button-play.png");
		STATUS_TO_PICTURE_100.put(20, "button-tick.png");
		STATUS_TO_PICTURE_100.put(25, "fave.png");
		STATUS_TO_PICTURE_100.put(30, "button-alert.png");
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

	public static HashMap<Integer, String> getStatusToText100() {
		return STATUS_TO_TEXT_100;
	}

	public static HashMap<Integer, String> getStatusToPicture100() {
		return STATUS_TO_PICTURE_100;
	}

	@Override
	public String toString() {
		return "Status [statusText=" + statusText + ", status=" + status + ", statusPicture=" + statusPicture + "]";
	}
}
