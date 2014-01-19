package foo.domain.entity;

import java.sql.Timestamp;

public class ServiceOrder {

	private String orderNumber;
	private int activity;
	private String item;
	private String machine;
	private String serviceType;
	private String problem; // Field: Description .desc
	private int status;
	private Timestamp date;
	private Machine machineObject;
	private String serviceTypeDescription;
	private ActivityStatus statusObject;

	private Timestamp localDate;

	public ServiceOrder() {
		super();
	}

	public ServiceOrder(String orderNumber, int activity, String item, String machine, String serviceType,
			String problem, int status, Timestamp date) {
		super();
		this.orderNumber = orderNumber;
		this.activity = activity;
		this.item = item;
		this.machine = machine;
		this.serviceType = serviceType;
		this.problem = problem;
		this.status = status;
		this.date = date;
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

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Machine getMachineObject() {
		return machineObject;
	}

	public void setMachineObject(Machine machineObject) {
		this.machineObject = machineObject;
	}

	public String getServiceTypeDescription() {
		return serviceTypeDescription;
	}

	public void setServiceTypeDescription(String serviceTypeDescription) {
		this.serviceTypeDescription = serviceTypeDescription;
	}

	public ActivityStatus getStatusObject() {
		return statusObject;
	}

	public void setStatusObject(ActivityStatus statusObject) {
		this.statusObject = statusObject;
	}

	public Timestamp getLocalDate() {
		return localDate;
	}

	public void setLocalDate(Timestamp localDate) {
		this.localDate = localDate;
	}

}
