package foo.domain.entity;

public class User {

	private String name;
	private String employeeNumber;
	private String supervisorName;
	private int serviceRequestEntryPermission; // 1 = yes, 2 = no
	private int serviceRequestApprovalPermission;
	private int serviceRequestReviewPermission;
	private int orderListViewPermission;
	private int orderMatterialPermission;
	private int issueMaterialPermission;
	private int isMaintananceSupervisor;
	private boolean serviceRequestApprovalPermissionTOrF = false;

	private int company;

	public User(String name, String supervisorName, int serviceRequestEntryPermission,
			int serviceRequestApprovalPermission, int serviceRequestReviewPermission, int orderListViewPermission,
			int orderMatterialPermission, int issueMaterialPermission) {
		super();
		this.name = name;
		this.supervisorName = supervisorName;
		this.serviceRequestEntryPermission = serviceRequestEntryPermission;
		this.serviceRequestApprovalPermission = serviceRequestApprovalPermission;
		this.serviceRequestReviewPermission = serviceRequestReviewPermission;
		this.orderListViewPermission = orderListViewPermission;
		this.orderMatterialPermission = orderMatterialPermission;
		this.issueMaterialPermission = issueMaterialPermission;
		this.company = 124;
	}

	public boolean isSupervisor() {
		if (supervisorName == null || supervisorName.trim().equalsIgnoreCase(" ")
				|| supervisorName.trim().equalsIgnoreCase("")) {
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public int getServiceRequestEntryPermission() {
		return serviceRequestEntryPermission;
	}

	public void setServiceRequestEntryPermission(int serviceRequestEntryPermission) {
		this.serviceRequestEntryPermission = serviceRequestEntryPermission;
	}

	public int getServiceRequestApprovalPermission() {
		return serviceRequestApprovalPermission;
	}

	public void setServiceRequestApprovalPermission(int serviceRequestApprovalPermission) {
		this.serviceRequestApprovalPermission = serviceRequestApprovalPermission;
	}

	public int getServiceRequestReviewPermission() {
		return serviceRequestReviewPermission;
	}

	public void setServiceRequestReviewPermission(int serviceRequestReviewPermission) {
		this.serviceRequestReviewPermission = serviceRequestReviewPermission;
	}

	public int getOrderListViewPermission() {
		return orderListViewPermission;
	}

	public void setOrderListViewPermission(int orderListViewPermission) {
		this.orderListViewPermission = orderListViewPermission;
	}

	public int getOrderMatterialPermission() {
		return orderMatterialPermission;
	}

	public void setOrderMatterialPermission(int orderMatterialPermission) {
		this.orderMatterialPermission = orderMatterialPermission;
	}

	public int getIssueMaterialPermission() {
		return issueMaterialPermission;
	}

	public void setIssueMaterialPermission(int issueMaterialPermission) {
		this.issueMaterialPermission = issueMaterialPermission;
	}

	public boolean isServiceRequestApprovalPermissionTOrF() {
		return serviceRequestApprovalPermissionTOrF;
	}

	public void setServiceRequestApprovalPermissionTOrF(boolean serviceRequestApprovalPermissionTOrF) {
		this.serviceRequestApprovalPermissionTOrF = serviceRequestApprovalPermissionTOrF;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public int getCompany() {
		return company;
	}

	public void setCompany(int company) {
		this.company = company;
	}

	public int getIsMaintananceSupervisor() {
		return isMaintananceSupervisor;
	}

	public void setIsMaintananceSupervisor(int isMaintananceSupervisor) {
		this.isMaintananceSupervisor = isMaintananceSupervisor;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", supervisorName=" + supervisorName + ", serviceRequestEntryPermission="
				+ serviceRequestEntryPermission + ", serviceRequestApprovalPermission="
				+ serviceRequestApprovalPermission + ", serviceRequestReviewPermission="
				+ serviceRequestReviewPermission + ", orderListViewPermission=" + orderListViewPermission
				+ ", orderMatterialPermission=" + orderMatterialPermission + ", issueMaterialPermission="
				+ issueMaterialPermission + "]";
	}

}
