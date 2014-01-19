package foo.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import foo.domain.entity.Department;
import foo.domain.entity.Machine;
import foo.domain.entity.Priority;
import foo.domain.entity.RejectionReason;
import foo.domain.entity.ServiceRequest;
import foo.domain.entity.Status;

@ManagedBean(name = "statusRequestsBean")
@ViewScoped
public class StatusRequestsBean {

	private ArrayList<ServiceRequest> requests = new ArrayList<ServiceRequest>();

	private ServiceRequest selectedServiceRequest = new ServiceRequest();

	private Connection con;
	private Statement smt;
	private PreparedStatement ps;

	private String assignedTo;
	private ArrayList<String> assignedToDropdown;
	private String rejectionReason;
	private ArrayList<RejectionReason> rejectionReasonDropdown = new ArrayList<RejectionReason>();

	@ManagedProperty("#{userBean}")
	private UserBean userBeanStatus;

	@ManagedProperty("#{requestBean}")
	private RequestBean requestBean;

	private int idCount;

	public StatusRequestsBean() {
		super();
	}

	@PostConstruct
	public void init() throws SQLException {
		initializeAssignedToDropdown();
		initializeRejectionReasonDropdown();
		initializeRequest();
	}

	/**
	 * Initializes the Rejection Reason drop down from the database.
	 * 
	 * @throws SQLException
	 */
	private void initializeRejectionReasonDropdown() throws SQLException {

		try {
			System.out.println("getting Rejection Reason dropdown objects");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select distinct t$cdis, t$reas from baan.ttsclm903"
					+ userBeanStatus.getSignedInUser().getCompany();

			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				rejectionReasonDropdown.add(new RejectionReason(rs.getString("T$REAS"), rs.getString("T$CDIS")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			con.close();
			smt.close();
		}

	}

	/**
	 * 
	 * Grabs all the requests that this user can approve or reject at the
	 * current application wide level approval settings.
	 * 
	 * @throws SQLException
	 */

	public void initializeRequest() throws SQLException {

		try {

			int tmpCompany = userBeanStatus.getSignedInUser().getCompany();

			System.out.println("getting Service Requests for Company: " + tmpCompany);

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select t$sern, t$shpd, t$cgrp, t$prpr, t$user, t$date, t$ccll, t$stat, t$txta, t$seng, t$emer, t$appr, (select ttpr.t$dsca from ttcmcs070700 ttpr where ttreq.t$prpr=ttpr.t$prio) as desc_priority, (select ttdep.t$desc from ttsclm050"
					+ tmpCompany
					+ " ttdep where ttreq.t$cgrp=ttdep.t$cgrp) as desc_department from baan.ttsclm901"
					+ tmpCompany
					+ " ttreq"
					+ " where t$stat="
					+ levelStatusInitialize()
					+ " and t$proc=1 and t$erro=2"
					+ " and t$ccll not in (select t$ccll from ttsclm901"
					+ tmpCompany
					+ " ttreq where ttreq.t$stat!=1 and t$stat!="
					+ levelStatusInitialize()
					+ ")"
					+ " and t$date in (select max(t$date) from ttsclm901"
					+ tmpCompany
					+ " t2 where t2.t$ccll=ttreq.t$ccll)";
			if (userBeanStatus.getSignedInUser().getIsMaintananceSupervisor() == 2) {
				query += " and t$user in (select t$logn from ttsclm902" + tmpCompany + " where t$supv='"
						+ userBeanStatus.getSignedInUser().getName() + "')";
			}
			ResultSet rs = smt.executeQuery(query);

			System.out.println("***** Query: " + query);

			while (rs.next()) {
				int tmpDepartment;
				int tmpPriority;
				String tmpComments = "";
				int tmpCommentRefNumber = rs.getInt("T$TXTA");

				// if does actually have a comment string together individual
				// records into a tmpComments
				if (tmpCommentRefNumber != 0) {
					tmpComments = getComment(tmpCommentRefNumber);
				}

				try {
					tmpDepartment = Integer.parseInt(rs.getString("T$CGRP"));
					tmpPriority = Integer.parseInt(rs.getString("T$PRPR"));
				} catch (NumberFormatException e) {
					tmpDepartment = 100;
					tmpPriority = 25;

				}
				Timestamp tmpDate = new Timestamp(rs.getTimestamp("T$DATE").getTime()
						- userBeanStatus.getLocalTimeDifference().intValue());

				ServiceRequest tmpServiceRequest = new ServiceRequest(rs.getString("T$SERN"), rs.getString("T$SHPD"),
						tmpComments, tmpPriority, tmpDepartment, rs.getString("T$USER"), rs.getTimestamp("T$DATE"),
						new Priority(rs.getString("desc_priority"), tmpPriority), new Department(
								rs.getString("desc_department"), tmpDepartment), rs.getString("T$CCLL"));
				tmpServiceRequest.setStatus(new Status(rs.getInt("T$STAT")));
				tmpServiceRequest.setMachineObject(new Machine(rs.getString("T$SERN")));
				tmpServiceRequest.setCommentRefNumber(tmpCommentRefNumber);
				tmpServiceRequest.setServiceEngineer(rs.getString("T$SENG"));
				tmpServiceRequest.setEmergency(rs.getInt("T$EMER"));
				tmpServiceRequest.setApprover(rs.getString("T$APPR"));

				// Local Date time value
				tmpServiceRequest.setLocalDate(tmpDate);

				requests.add(tmpServiceRequest);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			// con.close();
			// ps.close();
			// smt.close();
		}
	}

	/**
	 * grabs all the text records for a Service Request and compiles them all
	 * into one string.
	 * 
	 * @param textRefNum
	 * @return
	 * @throws SQLException
	 */
	public String getComment(int textRefNum) throws SQLException {

		System.out.println("Get Comment");
		String comment = "";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();
			String queryGrabComment = "select * from ttttxt010" + userBeanStatus.getSignedInUser().getCompany()
					+ " where t$ctxt='" + textRefNum + "'";

			System.out.println(queryGrabComment);
			ResultSet rs = smt.executeQuery(queryGrabComment);

			while (rs.next()) {
				comment += rs.getString("T$TEXT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return comment;

	}

	private int levelStatusInitialize() {

		if (userBeanStatus.getSignedInUser().getIsMaintananceSupervisor() == 1) {
			return RequestBean.ASSIGNED;

		} else if (userBeanStatus.getSignedInUser().isSupervisor()) {
			return RequestBean.REGISTERED;

		} else if (userBeanStatus.isLevelOneApprovalOff()) {

			return RequestBean.REGISTERED;

		} else {

			return RequestBean.REGISTERED;
		}

	}

	private int levelStatusApprove() {

		if (userBeanStatus.getSignedInUser().getIsMaintananceSupervisor() == 1) {
			return RequestBean.TRANSFERRED;

		} else if (userBeanStatus.isLevelOneApprovalOff()) {
			return RequestBean.TRANSFERRED;

		} else {

			return RequestBean.ASSIGNED;
		}

	}

	private int levelStatusReject() {

		if (userBeanStatus.getSignedInUser().getIsMaintananceSupervisor() == 1
				|| userBeanStatus.isLevelOneApprovalOff()) {
			return RequestBean.ASSIGNED_WITH_WAIT;

		} else {

			return RequestBean.REGISTERED_WITH_WAIT;
		}

	}

	private int valueApproveProc() {

		if (userBeanStatus.getSignedInUser().getIsMaintananceSupervisor() == 1
				|| userBeanStatus.isLevelOneApprovalOff() || userBeanStatus.isLevelTwoApprovalOff()) {
			return RequestBean.DEFAULT_PROC;

		} else {

			return 1; // value for processed
		}

	}

	/**
	 * 
	 * Approves a Service Request by inputing a new record into the intermediary
	 * table 920 with a new status based on the level approval settings and who
	 * is approving the request.
	 * 
	 * @throws SQLException
	 */

	public void approve() throws SQLException {
		System.out.println("*******  Approve Service Request: " + selectedServiceRequest.toString());

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			int tmpCommentRef = requestBean.insertComment(selectedServiceRequest.getComments());

			String queryInsert = "insert into baan.ttsclm901"
					+ userBeanStatus.getSignedInUser().getCompany()
					+ " (t$ccll, t$user, t$date, t$stat, t$wait, t$cdis, t$sern, t$refcntd, t$refcntu, t$cgrp, t$prpr, t$shpd, t$proc, t$erro, t$seng, t$txta, t$updt, t$emer, t$appr) "
					+ "values (?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();

			// set variables
			ps.setString(1, selectedServiceRequest.getCallNumber());
			ps.setString(2, selectedServiceRequest.getUser());
			ps.setInt(3, levelStatusApprove()); //
			ps.setInt(4, RequestBean.WAIT_NO);
			ps.setString(5, " ");
			ps.setString(6, selectedServiceRequest.getMachineObject().getMachine());
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, selectedServiceRequest.getDepartment());
			ps.setInt(10, selectedServiceRequest.getPriority());
			ps.setString(11, selectedServiceRequest.getProblem());
			ps.setInt(12, valueApproveProc());
			ps.setInt(13, RequestBean.DEFAULT_ERRO);
			ps.setString(14, selectedServiceRequest.getServiceEngineer());
			ps.setInt(15, tmpCommentRef);
			ps.setInt(16, 2); // 2 = not an update of an existing record
			ps.setInt(17, selectedServiceRequest.getEmergency());
			ps.setString(18, userBeanStatus.getSignedInUser().getName());

			ps.executeUpdate();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(
					null,
					new FacesMessage(
							"Successful",
							(" Service Request Approved for Machine: "
									+ selectedServiceRequest.getMachineObject().getMachine() + " Problem: " + selectedServiceRequest
									.getProblem())));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	public void saveServiceRequestChanges() throws SQLException {
		System.out.println("*******  Updated Service Request: " + selectedServiceRequest.toString());

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			int tmpCommentRef = requestBean.insertComment(selectedServiceRequest.getComments());

			String queryInsert = "insert into baan.ttsclm901"
					+ userBeanStatus.getSignedInUser().getCompany()
					+ " (t$ccll, t$user, t$date, t$stat, t$wait, t$cdis, t$sern, t$refcntd, t$refcntu, t$cgrp, t$prpr, t$shpd, t$proc, t$erro, t$seng, t$txta, t$updt, t$emer, t$appr) "
					+ "values (?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();

			// set variables
			ps.setString(1, selectedServiceRequest.getCallNumber());
			ps.setString(2, selectedServiceRequest.getUser());
			ps.setInt(3, selectedServiceRequest.getStatus().getStatus()); //
			ps.setInt(4, RequestBean.WAIT_NO);
			ps.setString(5, " ");
			ps.setString(6, selectedServiceRequest.getMachineObject().getMachine());
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, selectedServiceRequest.getDepartment());
			ps.setInt(10, selectedServiceRequest.getPriority());
			ps.setString(11, selectedServiceRequest.getProblem());
			ps.setInt(12, 1);
			ps.setInt(13, RequestBean.DEFAULT_ERRO);
			ps.setString(14, selectedServiceRequest.getServiceEngineer());
			ps.setInt(15, tmpCommentRef);
			ps.setInt(16, 1); // 1 = an update of an existing record
			ps.setInt(17, selectedServiceRequest.getEmergency());
			ps.setString(18, selectedServiceRequest.getApprover());

			ps.executeUpdate();
			idCount++;

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(
					null,
					new FacesMessage(
							"Successful",
							(" Service Request Updated for Machine: "
									+ selectedServiceRequest.getMachineObject().getMachine() + " Problem: " + selectedServiceRequest
									.getProblem())));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * Rejects a Service Request by inputing a new record into the intermediary
	 * table 920 with a new status based on the level approval settings and who
	 * is Rejecting the request.
	 */
	public void reject() throws SQLException {
		System.out.println("*********** Reject Service Request: " + selectedServiceRequest.toString());

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			int tmpCommentRef = requestBean.insertComment(selectedServiceRequest.getComments());

			String queryInsert = "insert into baan.ttsclm901"
					+ userBeanStatus.getSignedInUser().getCompany()
					+ " (t$ccll, t$user, t$date, t$stat, t$wait, t$cdis, t$sern, t$refcntd, t$refcntu, t$cgrp, t$prpr, t$shpd, t$proc, t$erro, t$seng, t$txta, t$updt, t$emer, t$appr) "
					+ "values (?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();

			// set variables
			// ps.setString(1, "baan.ttsclm901124");
			ps.setString(1, selectedServiceRequest.getCallNumber());
			ps.setString(2, selectedServiceRequest.getUser());
			//
			ps.setInt(3, levelStatusReject()); //
			ps.setInt(4, RequestBean.WAIT_YES);
			ps.setString(5, rejectionReason);
			ps.setString(6, selectedServiceRequest.getMachineObject().getMachine());
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, selectedServiceRequest.getDepartment());
			ps.setInt(10, selectedServiceRequest.getPriority());
			ps.setString(11, selectedServiceRequest.getProblem());
			ps.setInt(12, RequestBean.DEFAULT_PROC);
			ps.setInt(13, RequestBean.DEFAULT_ERRO);
			ps.setString(14, " ");
			ps.setInt(15, tmpCommentRef);
			ps.setInt(16, 2); // 2 = not an update of an existing record
			ps.setInt(17, selectedServiceRequest.getEmergency());
			ps.setString(18, userBeanStatus.getSignedInUser().getName());

			ps.executeUpdate();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage("Successful", (" Service Request Rejected for Machine: "
							+ selectedServiceRequest.getMachineObject().getMachine() + " Problem: "
							+ selectedServiceRequest.getProblem() + " REJECTED")));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			ps.close();
			con.close();

		}
	}

	/**
	 * 
	 * Generates the assigned to drop down from employees in the database.
	 * 
	 * @throws SQLException
	 */
	public void initializeAssignedToDropdown() throws SQLException {

		assignedToDropdown = new ArrayList<String>();
		assignedToDropdown.add(" ");
		try {
			System.out.println("getting Assigned dropdown");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select distinct t$emno from ttsmdm140" + userBeanStatus.getSignedInUser().getCompany();
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				assignedToDropdown.add(rs.getString("T$EMNO"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}

	}

	public ArrayList<String> getAssignedToDropdown() {

		return assignedToDropdown;

	}

	public void setAssignedToDropdown(ArrayList<String> assignedToDropdown) {
		this.assignedToDropdown = assignedToDropdown;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public ArrayList<ServiceRequest> getRequests() {
		return requests;
	}

	public void setRequests(ArrayList<ServiceRequest> requests) {
		this.requests = requests;
	}

	public ServiceRequest getSelectedServiceRequest() {
		return selectedServiceRequest;
	}

	public void setSelectedServiceRequest(ServiceRequest selectedServiceRequest) {
		this.selectedServiceRequest = selectedServiceRequest;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public ArrayList<RejectionReason> getRejectionReasonDropdown() {
		return rejectionReasonDropdown;
	}

	public void setRejectionReasonDropdown(ArrayList<RejectionReason> rejectionReasonDropdown) {
		this.rejectionReasonDropdown = rejectionReasonDropdown;
	}

	public UserBean getUserBeanStatus() {
		return userBeanStatus;
	}

	public void setUserBeanStatus(UserBean userBeanStatus) {
		this.userBeanStatus = userBeanStatus;
	}

	public RequestBean getRequestBean() {
		return requestBean;
	}

	public void setRequestBean(RequestBean requestBean) {
		this.requestBean = requestBean;
	}

}
