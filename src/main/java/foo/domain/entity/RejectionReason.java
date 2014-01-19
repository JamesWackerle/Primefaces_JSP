package foo.domain.entity;

public class RejectionReason {

	public RejectionReason(String rejectionReasonLabel, String rejectionReasonValue) {
		super();
		this.rejectionReasonLabel = rejectionReasonLabel;
		this.rejectionReasonValue = rejectionReasonValue;
	}

	private String rejectionReasonLabel;
	private String rejectionReasonValue;

	public String getRejectionReasonLabel() {
		return rejectionReasonLabel;
	}

	public void setRejectionReasonLabel(String rejectionReasonLabel) {
		this.rejectionReasonLabel = rejectionReasonLabel;
	}

	public String getRejectionReasonValue() {
		return rejectionReasonValue;
	}

	public void setRejectionReasonValue(String rejectionReasonValue) {
		this.rejectionReasonValue = rejectionReasonValue;
	}

}
