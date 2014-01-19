package foo.domain.entity;

import java.sql.Timestamp;

//@Entity
public class ServiceRequest {

	// @Id
	// @GeneratedValue
	private int id;
	private String machine; // t$sern
	private String problem; // Named Message in the call
	private String comments; // Named Description in the call
	private int commentRefNumber;
	private String addFiles;
	private int priority;
	private int department;
	private String user;
	private Timestamp date;
	private Priority priorityObject;
	private Department departmentObject;
	private String callNumber;
	private Status status;
	private Machine machineObject;
	private String serviceEngineer;
	private int emergency;
	private String approver;

	private Timestamp localDate;

	public ServiceRequest(String machine, String problem, String comments, int priority, int department, String user,
			Timestamp date, Priority priorityObject, Department departmentObject, String callNumber) {
		super();
		this.machine = machine;
		this.problem = problem;
		this.comments = comments;
		this.priority = priority;
		this.department = department;
		this.user = user;
		this.date = date;
		this.priorityObject = priorityObject;
		this.departmentObject = departmentObject;
		this.callNumber = callNumber;
	}

	public ServiceRequest(String machine, String problem, String comments, int priority, int department, String user,
			Timestamp date, String callNumber) {
		super();
		this.machine = machine;
		this.problem = problem;
		this.comments = comments;
		this.priority = priority;
		this.department = department;
		this.user = user;
		this.date = date;
		this.callNumber = callNumber;
	}

	public ServiceRequest(final String machine, final String problem, final String comments, final int priority,
			final int department) {
		setMachine(machine);
		setProblem(problem);
		setComments(comments);
		setPriority(priority);
		setDepartment(department);

	}

	public ServiceRequest(String machine, String problem, String comments, int priority, int department, String user,
			Timestamp date) {
		super();
		this.machine = machine;
		this.problem = problem;
		this.comments = comments;
		this.priority = priority;
		this.department = department;
		this.user = user;
		this.date = date;
	}

	public ServiceRequest(String machine, String problem, String comments, String user, Timestamp date,
			Priority priorityObject, Department departmentObject) {
		super();
		this.machine = machine;
		this.problem = problem;
		this.comments = comments;
		this.user = user;
		this.date = date;
		this.priorityObject = priorityObject;
		this.departmentObject = departmentObject;
	}

	public String getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}

	public ServiceRequest() {

	}

	@Override
	public String toString() {
		return "ServiceRequest [id=" + id + ", machine=" + machine + ", problem=" + problem + ", comments=" + comments
				+ ", addFiles=" + addFiles + ", priority=" + priority + ", department=" + department + ", user=" + user
				+ ", date=" + date + ", priorityObject=" + priorityObject + ", departmentObject=" + departmentObject
				+ ", callNumber=" + callNumber + "]";
	}

	public Priority getPriorityObject() {
		return priorityObject;
	}

	public void setPriorityObject(Priority priorityObject) {
		this.priorityObject = priorityObject;
	}

	public Department getDepartmentObject() {
		return departmentObject;
	}

	public void setDepartmentObject(Department departmentObject) {
		this.departmentObject = departmentObject;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getDepartment() {
		return department;
	}

	public void setDepartment(int department) {
		this.department = department;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Machine getMachineObject() {
		return machineObject;
	}

	public void setMachineObject(Machine machineObject) {
		this.machineObject = machineObject;
	}

	public int getCommentRefNumber() {
		return commentRefNumber;
	}

	public void setCommentRefNumber(int commentRefNumber) {
		this.commentRefNumber = commentRefNumber;
	}

	public String getServiceEngineer() {
		return serviceEngineer;
	}

	public void setServiceEngineer(String serviceEngineer) {
		this.serviceEngineer = serviceEngineer;
	}

	public int getEmergency() {
		return emergency;
	}

	public void setEmergency(int emergency) {
		this.emergency = emergency;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public Timestamp getLocalDate() {
		return localDate;
	}

	public void setLocalDate(Timestamp localDate) {
		this.localDate = localDate;
	}

}
