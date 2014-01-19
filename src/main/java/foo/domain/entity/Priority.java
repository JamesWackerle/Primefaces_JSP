package foo.domain.entity;

public class Priority {

	private int priorityValue;
	private String priorityLabel;

	public Priority(String priorityLabel, int priorityValue) {
		super();
		this.priorityLabel = priorityLabel;
		this.priorityValue = priorityValue;
	}

	public String getPriorityLabel() {
		return priorityLabel;
	}

	public void setPriorityLable(String priorityLabel) {
		this.priorityLabel = priorityLabel;
	}

	public int getPriorityValue() {
		return priorityValue;
	}

	public void setPriorityValue(int priorityValue) {
		this.priorityValue = priorityValue;
	}

}
