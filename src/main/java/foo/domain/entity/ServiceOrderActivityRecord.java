package foo.domain.entity;

public class ServiceOrderActivityRecord {

	private String orderNumber;
	private int activity;
	private int lineNumber;
	private String task;
	private String taskDescription;
	private String activityLineNumber;
	private String employeeNumber;

	public ServiceOrderActivityRecord() {
		super();
	}

	public ServiceOrderActivityRecord(String orderNumber, int activity, int lineNumber, String task,
			String taskDescription) {
		super();
		this.orderNumber = orderNumber;
		this.activity = activity;
		this.lineNumber = lineNumber;
		this.task = task;
		this.taskDescription = taskDescription;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getActivityLineNumber() {
		return activityLineNumber;
	}

	public void setActivityLineNumber(String activityLineNumber) {
		this.activityLineNumber = activityLineNumber;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		if (employeeNumber == null || employeeNumber.equals("")) {
			this.employeeNumber = " ";
		} else {
			this.employeeNumber = employeeNumber;
		}
	}

}
