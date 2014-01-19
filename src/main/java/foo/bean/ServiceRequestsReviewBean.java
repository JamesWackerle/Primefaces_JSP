package foo.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import foo.domain.entity.Department;
import foo.domain.entity.Machine;
import foo.domain.entity.Priority;
import foo.domain.entity.ServiceRequest;
import foo.domain.entity.Status;

@ManagedBean(name = "serviceRequestsReviewBean")
@ViewScoped
public class ServiceRequestsReviewBean {

	private ArrayList<ServiceRequest> requests = new ArrayList<ServiceRequest>();

	private ServiceRequest selectedServiceRequest;

	private Connection con;
	private Statement smt;

	private String signedInUserName;

	@ManagedProperty("#{userBean}")
	private UserBean userBeanReview;

	public ServiceRequestsReviewBean() {
		super();
	}

	@PostConstruct
	public void init() throws SQLException {
		signedInUserName = userBeanReview.getSignedInUser().getName(); // Success!
		initializeReviewRequest();
	}

	/**
	 * Grabs all the Service orders that have been requested by the user.
	 * 
	 * @throws SQLException
	 */
	public void initializeReviewRequest() throws SQLException {

		int tmpCompany = userBeanReview.getSignedInUser().getCompany();

		try {

			System.out.println("getting Review Service Requests");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select t$sern, t$shpd, t$cgrp, t$prpr, t$clna, t$user, t$rpct, t$ccll, t$stat, (select ttcmcs070700.t$dsca from ttcmcs070700 where ttreq.t$prpr=ttcmcs070700.t$prio) as desc_priority, (select ttdep.t$desc from ttsclm050"
					+ tmpCompany
					+ " ttdep where ttreq.t$cgrp=ttdep.t$cgrp) as desc_department from baan.ttsclm100"
					+ tmpCompany
					+ " ttreq"
					+ " where t$user='"
					+ signedInUserName
					+ "'"
					+ " or t$clna='"
					+ signedInUserName
					+ "'"
					+ " and t$rpct in (select max(t$rpct) from ttsclm100"
					+ tmpCompany
					+ " ttreq2 where ttreq.t$ccll=ttreq2.t$ccll)";
			System.out.println("User: " + signedInUserName);
			System.out.println("Query: " + query);

			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				int tmpDepartment;
				int tmpPriority;
				String tmpRequester = rs.getString("T$USER").equalsIgnoreCase(signedInUserName) ? rs
						.getString("T$USER") : rs.getString("T$CLNA");
				try {
					tmpDepartment = Integer.parseInt(rs.getString("T$CGRP"));
					tmpPriority = Integer.parseInt(rs.getString("T$PRPR"));
				} catch (NumberFormatException e) {
					tmpDepartment = 100;
					tmpPriority = 25;

				}

				Timestamp tmpDate = new Timestamp(rs.getTimestamp("T$RPCT").getTime()
						- userBeanReview.getLocalTimeDifference().intValue());

				ServiceRequest tmpServiceRequest = new ServiceRequest(rs.getString("T$SERN"), rs.getString("T$SHPD"),
						"comments ", tmpPriority, tmpDepartment, tmpRequester, rs.getTimestamp("T$RPCT"), new Priority(
								rs.getString("desc_priority"), tmpPriority), new Department(
								rs.getString("desc_department"), tmpDepartment), rs.getString("T$CCLL"));
				tmpServiceRequest.setStatus(new Status(rs.getInt("T$STAT")));
				tmpServiceRequest.setMachineObject(new Machine(rs.getString("T$SERN")));

				// Local Date time value
				tmpServiceRequest.setLocalDate(tmpDate);

				requests.add(tmpServiceRequest);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
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

	public UserBean getUserBeanReview() {
		return userBeanReview;
	}

	public void setUserBeanReview(UserBean userBean) {
		this.userBeanReview = userBean;
	}

}
