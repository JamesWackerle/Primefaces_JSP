package foo.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import foo.domain.entity.ServiceLaborTrackingRecord;
import foo.domain.entity.ServiceOrder;
import foo.domain.entity.ServiceOrderActivityRecord;

@ManagedBean(name = "trackingBean")
@SessionScoped
public class TrackingBean {

	private ServiceOrder selectedServiceOrder = new ServiceOrder();;

	@ManagedProperty("#{userBean}")
	private UserBean userBeanTracking;
	private String signedInUserName;

	private Connection con;
	private Statement smt;
	private PreparedStatement ps;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	private ArrayList<ServiceOrderActivityRecord> records;
	private ArrayList<ServiceLaborTrackingRecord> inProgressLaborRecords;
	private ArrayList<ServiceLaborTrackingRecord> allLaborRecords;

	private ServiceOrderActivityRecord selectedRecord = new ServiceOrderActivityRecord();
	private ServiceLaborTrackingRecord selectedTimeRecord = new ServiceLaborTrackingRecord();
	private ServiceLaborTrackingRecord selectedModifyTimeRecord = new ServiceLaborTrackingRecord();

	private Date addStartDate;
	private Date addStopDate;
	private String startRecordEmployeeNumber;
	private String addRecordEmployeeNumber;

	private Date modifyStartDate = new Date(0);
	private Date modifyEndDate = new Date(0);

	private long utcTimeSeconds = 0;

	@PostConstruct
	public void init() throws SQLException {
		signedInUserName = userBeanTracking.getSignedInUser().getName(); // Success!

		// Default selected Service order for testing purposes
		selectedServiceOrder = new ServiceOrder();
		selectedServiceOrder.setOrderNumber("SV0000026");
		selectedServiceOrder.setActivity(10);

		initializeServiceOrderActivityRecords();
		startRecordEmployeeNumber = userBean.getSignedInUser().getEmployeeNumber();
		addRecordEmployeeNumber = userBean.getSignedInUser().getEmployeeNumber();

	}

	/**
	 * Grabs all the Activities (Tasks) associated with this Service Order.
	 * 
	 * @throws SQLException
	 */

	public void initializeServiceOrderActivityRecords() throws SQLException {

		int tmpCompany = userBean.getSignedInUser().getCompany();
		String tmpOrderNumber = selectedServiceOrder.getOrderNumber();
		int tmpActivity = selectedServiceOrder.getActivity();
		System.out.println("TmpCompany: " + tmpCompany + " tmpOrderNumber: " + tmpOrderNumber + " tmpActivity: "
				+ tmpActivity);

		records = new ArrayList<ServiceOrderActivityRecord>();

		try {

			System.out.println("getting Service Activity Records " + tmpCompany);

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select t$orno, t$acln, t$lino, t$ctsk, t$emno, (select t$desc from ttsmdm015" + tmpCompany
					+ " tttdesc where ttrecord.t$ctsk=tttdesc.t$ctsk) as task_desc from ttssoc230" + tmpCompany
					+ " ttrecord where t$orno='" + tmpOrderNumber + "' and t$acln=" + tmpActivity;
			System.out.println("Query for Records: " + query);
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				ServiceOrderActivityRecord tmpServiceOrderActivityRecord = new ServiceOrderActivityRecord(
						rs.getString("T$ORNO"), rs.getInt("T$ACLN"), rs.getInt("T$LINO"), rs.getString("T$CTSK"),
						rs.getString("TASK_DESC"));
				tmpServiceOrderActivityRecord.setActivityLineNumber("" + rs.getInt("T$ACLN") + rs.getInt("T$LINO"));
				tmpServiceOrderActivityRecord.setEmployeeNumber(rs.getString("T$EMNO"));
				records.add(tmpServiceOrderActivityRecord);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	/**
	 * Grabs all the time records already started for this task that do not have
	 * an end time.
	 * 
	 * @throws SQLException
	 */

	public void initializeInProgressTimeRecords() throws SQLException {
		System.out.println("Initialize In Progress Time Records");
		inProgressLaborRecords = new ArrayList<ServiceLaborTrackingRecord>();

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryGetInProgressTimeRecords = "select * from baan.ttssoc930"
					+ userBean.getSignedInUser().getCompany()
					+ " where t$orno = ? and t$lino = ?  and t$acln = ? and t$endt = ?";

			ps = con.prepareStatement(queryGetInProgressTimeRecords);
			ps.clearParameters();

			// set variables
			ps.setString(1, selectedRecord.getOrderNumber());
			ps.setInt(2, selectedRecord.getLineNumber());
			ps.setInt(3, selectedRecord.getActivity());
			ps.setTimestamp(4, new Timestamp(0));

			System.out.println("Query to get in progress time records: " + queryGetInProgressTimeRecords);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				// local time adjusted start / end dates
				Timestamp tmpStartDate = new Timestamp(rs.getTimestamp("T$STDT").getTime()
						- userBean.getLocalTimeDifference().intValue());
				Timestamp tmpEndDate = new Timestamp(rs.getTimestamp("T$ENDT").getTime()
						- userBean.getLocalTimeDifference().intValue());

				ServiceLaborTrackingRecord tmpInterruptRecords = new ServiceLaborTrackingRecord(rs.getString("T$ORNO"),
						rs.getInt("T$LINO"), rs.getInt("T$ACLN"), rs.getString("T$EMNO"), rs.getTimestamp("T$STDT"),
						rs.getTimestamp("T$ENDT"), rs.getInt("T$PROC"), rs.getInt("T$READ"));
				tmpInterruptRecords.setTransactionDate(rs.getTimestamp("T$HRDT"));

				// Local Time adjusted start and end dates
				tmpInterruptRecords.setLocalTimeStartDate(tmpStartDate);
				tmpInterruptRecords.setLocalTimeEndDate(tmpEndDate);

				tmpInterruptRecords.setStartModifyDate(new Date(rs.getTimestamp("T$STDT").getTime()));
				tmpInterruptRecords.setEndModifyDate(new Date(rs.getTimestamp("T$ENDT").getTime()));

				inProgressLaborRecords.add(tmpInterruptRecords);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * NOT USED
	 * 
	 * Grabs all the time record associated with this particular task. It is
	 * used for the modify time record dialog box that is not currently
	 * activated because it was beyond the specification.
	 * 
	 * @throws SQLException
	 */
	public void initializeAllTimeRecords() throws SQLException {
		System.out.println("Initialize All Time Records");
		allLaborRecords = new ArrayList<ServiceLaborTrackingRecord>();

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryGetInProgressTimeRecords = "select * from baan.ttssoc930"
					+ userBean.getSignedInUser().getCompany() + " where t$orno = ? and t$lino = ?  and t$acln = ?";

			ps = con.prepareStatement(queryGetInProgressTimeRecords);
			ps.clearParameters();

			// set variables
			ps.setString(1, selectedRecord.getOrderNumber());
			ps.setInt(2, selectedRecord.getLineNumber());
			ps.setInt(3, selectedRecord.getActivity());

			System.out.println("Query to get all time records: " + queryGetInProgressTimeRecords);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ServiceLaborTrackingRecord tmpInterruptRecords = new ServiceLaborTrackingRecord(rs.getString("T$ORNO"),
						rs.getInt("T$LINO"), rs.getInt("T$ACLN"), rs.getString("T$EMNO"), rs.getTimestamp("T$STDT"),
						rs.getTimestamp("T$ENDT"), rs.getInt("T$PROC"), rs.getInt("T$READ"));
				tmpInterruptRecords.setTransactionDate(rs.getTimestamp("T$HRDT"));
				tmpInterruptRecords.setStartModifyDate(new Date(rs.getTimestamp("T$STDT").getTime()));
				tmpInterruptRecords.setEndModifyDate(new Date(rs.getTimestamp("T$ENDT").getTime()));

				allLaborRecords.add(tmpInterruptRecords);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * First validates Employee number to make sure it actually exists in the
	 * employee table. Then adds the time record with a dummy end value of 0
	 * (January 1st 12:00 AM 1970).
	 * 
	 * @throws SQLException
	 */

	public void submitStartTimeRecord() throws SQLException {
		System.out.println("submitStartTime");
		FacesContext context = FacesContext.getCurrentInstance();

		if (!isValidEmployeeNumber(startRecordEmployeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Employee Number",
					"The employee number you entered is not valid.");
			context.addMessage(null, message);
			return;
		} else if (!canStartNewRecordForActivity(startRecordEmployeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Already Open Labor Record for Employee",
					"The employee number you entered already has a tracking record started for this Activity Line.");
			context.addMessage(null, message);
			return;
		}

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryInsert = "insert into baan.ttssoc930"
					+ userBean.getSignedInUser().getCompany()
					+ " (t$orno, t$lino, t$acln, t$emno, t$stdt, t$endt, t$proc, t$read, t$refcntd, t$refcntu, t$hrdt) "
					+ "values (?, ?, ?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?, ?, ?, ?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP))";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();
			System.out.println("Signed in User Employee Number: " + userBean.getSignedInUser().getEmployeeNumber());
			System.out.println("Start Add Number Employee Number: " + startRecordEmployeeNumber);

			// set variables
			ps.setString(1, selectedRecord.getOrderNumber());
			ps.setInt(2, selectedRecord.getLineNumber());
			ps.setInt(3, selectedRecord.getActivity());
			ps.setString(4, startRecordEmployeeNumber);
			ps.setTimestamp(5, new Timestamp(0));
			ps.setInt(6, 2); // processed
			ps.setInt(7, 2); // ready
			ps.setInt(8, 0); // refcntd
			ps.setInt(9, 0); // refcntu

			System.out.println("Query to insert time: " + ps.toString());
			ps.executeUpdate();

			context.addMessage(null, new FacesMessage("Successful", ("Started Tracking time: ")));
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("dlg.hide()");

		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "System Error",
					"Please see your System Adminstrator for more information.");
			context.addMessage(null, message);

		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * Stops a already started Time record by updating the time record with an
	 * end date.
	 * 
	 * @throws SQLException
	 */
	public void interruptTimeRecord() throws SQLException {
		System.out.println("Interrupt Time Record");
		System.out.println("Employee: " + selectedTimeRecord.getEmployeeNumber() + "\nLine Number: "
				+ selectedTimeRecord.getActivity());

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryUpdate = "update baan.ttssoc930"
					+ userBean.getSignedInUser().getCompany()
					+ " set T$endt=SYS_EXTRACT_UTC(SYSTIMESTAMP), t$proc=?, t$read=? where t$orno=? and t$lino=? and t$acln=? and t$emno=? and t$stdt=?";

			ps = con.prepareStatement(queryUpdate);
			ps.clearParameters();

			// set variables
			ps.setInt(1, 2); // unprocessed
			ps.setInt(2, 1); // ready
			ps.setString(3, selectedTimeRecord.getOrderNumber());
			ps.setInt(4, selectedTimeRecord.getLineNumber());
			ps.setInt(5, selectedTimeRecord.getActivity());
			ps.setString(6, selectedTimeRecord.getEmployeeNumber());
			ps.setTimestamp(7, selectedTimeRecord.getStartDate());

			System.out.println("Query to update time record: " + queryUpdate);
			ps.executeUpdate();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Successful", ("Stopped time Recording: ")));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * First validates inputs from the user on the confirm add time dialog box.
	 * Then inserts that time record into the tsclm930 table.
	 * 
	 * @throws SQLException
	 */
	public void submitAddTimeRecord() throws SQLException {
		System.out.println("submitAddTime");

		System.out.println("Time Difference: " + userBean.getLocalTimeDifference());

		FacesContext context = FacesContext.getCurrentInstance();

		if (!isValidEmployeeNumber(addRecordEmployeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Employee Number",
					"The employee number you entered is not valid.");
			context.addMessage(null, message);
			return;
		} else {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("addTime.hide()");
		}

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryInsert = "insert into baan.ttssoc930"
					+ userBean.getSignedInUser().getCompany()
					+ " (t$orno, t$lino, t$acln, t$emno, t$stdt, t$endt, t$proc, t$read, t$refcntd, t$refcntu, t$hrdt) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP))";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();
			System.out.println("Signed In User Employee Number: " + userBean.getSignedInUser().getEmployeeNumber());

			// set variables
			ps.setString(1, selectedRecord.getOrderNumber());
			ps.setInt(2, selectedRecord.getLineNumber());
			ps.setInt(3, selectedRecord.getActivity());
			ps.setString(4, addRecordEmployeeNumber);
			ps.setTimestamp(5,
					new Timestamp(this.addStartDate.getTime() + userBean.getLocalTimeDifference().intValue()));
			ps.setTimestamp(6, new Timestamp(this.addStopDate.getTime() + userBean.getLocalTimeDifference().intValue()));
			ps.setInt(7, 2); // unprocessed
			ps.setInt(8, 1); // ready
			ps.setInt(9, 0); // refcntd
			ps.setInt(10, 0); // refcntu

			System.out.println("Query to Add time record: " + ps.toString());
			ps.executeUpdate();

			context.addMessage(null, new FacesMessage("Successful", ("Added Time Record: ")));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * checks if the entered employee number is a valid employee from the
	 * tsmdm140 table.
	 * 
	 * @param employeeNumber
	 * @return
	 */

	public boolean isValidEmployeeNumber(String employeeNumber) {

		boolean valid = false;

		try {
			System.out.println("testing if isValidEmployeeNumber in Tracking Bean");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();

			String query = "select * from ttsmdm140" + userBean.getSignedInUser().getCompany() + " where t$emno='"
					+ employeeNumber + "'";

			ResultSet rs = smt.executeQuery(query);

			// If there is records from the employee table with the employee
			// number than it is an valid employee.
			if (rs.next()) {
				valid = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				con.close();
				smt.close();
			} catch (SQLException e) {
				return false;
			}

		}

		return valid;
	}

	/**
	 * Checks to make sure that the Employee does not have a time record started
	 * already for this record.
	 * 
	 * @param employeeNumber
	 * @return
	 */
	public boolean canStartNewRecordForActivity(String employeeNumber) {

		boolean valid = false;

		System.out.println("check to see if user can Start a new record, EMNO: " + employeeNumber);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String query = "select * from baan.ttssoc930" + userBean.getSignedInUser().getCompany()
					+ " where t$orno=? and t$lino=? and t$acln=? and t$emno=? and t$endt=?";

			ps = con.prepareStatement(query);
			ps.clearParameters();

			// set variables
			ps.setString(1, selectedRecord.getOrderNumber());
			ps.setInt(2, selectedRecord.getLineNumber());
			ps.setInt(3, selectedRecord.getActivity());
			ps.setString(4, employeeNumber);
			ps.setTimestamp(5, new Timestamp(0));

			System.out.println("Query to check if a user can add a new start record: " + query);
			ResultSet rs = ps.executeQuery();

			if (!rs.next()) {
				valid = true;
			}
			System.out.println("Valid does not have any records started for this emno: " + valid);

		} catch (Exception e) {
			e.printStackTrace();
			return valid;
		} finally {
			try {
				con.close();
				smt.close();
			} catch (SQLException e) {
				return valid;
			}

		}

		return valid;
	}

	/**
	 * NOT USED
	 * 
	 * I created a dialog box for an administrator to modify all the time
	 * records for a particular task. This however is not added to the page as
	 * it is beyond the specification.
	 * 
	 * @throws SQLException
	 */
	public void saveTimeRecord() throws SQLException {
		System.out.println("Save Time Record");
		System.out.println("Employee: " + selectedModifyTimeRecord.getEmployeeNumber() + "\nLine Number: "
				+ selectedModifyTimeRecord.getActivity());

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryUpdate = "update baan.ttssoc930"
					+ userBean.getSignedInUser().getCompany()
					+ " set t$stdt=?, t$endt=?, t$proc=?, t$read=? where t$orno=? and t$lino=? and t$acln=? and t$emno=? and t$hrdt=?";

			ps = con.prepareStatement(queryUpdate);
			ps.clearParameters();

			// set variables
			ps.setTimestamp(1, new Timestamp(selectedModifyTimeRecord.getStartModifyDate().getTime()));
			ps.setTimestamp(2, new Timestamp(selectedModifyTimeRecord.getEndModifyDate().getTime()));
			ps.setInt(3, 2); // unprocessed
			ps.setInt(4, 1); // ready
			ps.setString(5, selectedModifyTimeRecord.getOrderNumber());
			ps.setInt(6, selectedModifyTimeRecord.getLineNumber());
			ps.setInt(7, selectedModifyTimeRecord.getActivity());
			ps.setString(8, selectedModifyTimeRecord.getEmployeeNumber());
			ps.setTimestamp(9, selectedModifyTimeRecord.getTransactionDate());

			System.out.println("Query to save time record: " + queryUpdate);
			ps.executeUpdate();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Successful", ("Saved time Record: ")));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	public ServiceOrder getSelectedServiceOrder() {
		return selectedServiceOrder;
	}

	public void setSelectedServiceOrder(ServiceOrder selectedServiceOrder) {
		this.selectedServiceOrder = selectedServiceOrder;
	}

	public UserBean getUserBeanTracking() {
		return userBeanTracking;
	}

	public void setUserBeanTracking(UserBean userBeanTracking) {
		this.userBeanTracking = userBeanTracking;
	}

	public String getSignedInUserName() {
		return signedInUserName;
	}

	public void setSignedInUserName(String signedInUserName) {
		this.signedInUserName = signedInUserName;
	}

	public ArrayList<ServiceOrderActivityRecord> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<ServiceOrderActivityRecord> records) {
		this.records = records;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public ServiceOrderActivityRecord getSelectedRecord() {
		return selectedRecord;
	}

	public ArrayList<ServiceLaborTrackingRecord> getAllLaborRecords() {
		return allLaborRecords;
	}

	public void setAllLaborRecords(ArrayList<ServiceLaborTrackingRecord> allLaborRecords) {
		this.allLaborRecords = allLaborRecords;
	}

	public void setSelectedRecord(ServiceOrderActivityRecord selectedRecord) {
		this.selectedRecord = selectedRecord;
	}

	public ArrayList<ServiceLaborTrackingRecord> getInProgressLaborRecords() {
		return inProgressLaborRecords;
	}

	public void setInProgressLaborRecords(ArrayList<ServiceLaborTrackingRecord> inProgressLaborRecords) {
		this.inProgressLaborRecords = inProgressLaborRecords;
	}

	public ServiceLaborTrackingRecord getSelectedTimeRecord() {
		return selectedTimeRecord;
	}

	public void setSelectedTimeRecord(ServiceLaborTrackingRecord selectedTimeRecord) {
		this.selectedTimeRecord = selectedTimeRecord;
	}

	public Date getAddStartDate() {
		return addStartDate;
	}

	public void setAddStartDate(Date addStartDate) {
		this.addStartDate = addStartDate;
	}

	public Date getAddStopDate() {
		return addStopDate;
	}

	public void setAddStopDate(Date addStopDate) {
		this.addStopDate = addStopDate;
	}

	public String getStartRecordEmployeeNumber() {
		return startRecordEmployeeNumber;
	}

	public void setStartRecordEmployeeNumber(String startRecordEmployeeNumber) {
		this.startRecordEmployeeNumber = startRecordEmployeeNumber;
	}

	public String getAddRecordEmployeeNumber() {
		return addRecordEmployeeNumber;
	}

	public void setAddRecordEmployeeNumber(String addRecordEmployeeNumber) {
		this.addRecordEmployeeNumber = addRecordEmployeeNumber;
	}

	public Date getModifyStartDate() {
		return modifyStartDate;
	}

	public void setModifyStartDate(Date modifyStartDate) {
		this.modifyStartDate = modifyStartDate;
	}

	public Date getModifyEndDate() {
		return modifyEndDate;
	}

	public void setModifyEndDate(Date modifyEndDate) {
		this.modifyEndDate = modifyEndDate;
	}

	public ServiceLaborTrackingRecord getSelectedModifyTimeRecord() {
		return selectedModifyTimeRecord;
	}

	public void setSelectedModifyTimeRecord(ServiceLaborTrackingRecord selectedModifyTimeRecord) {
		this.selectedModifyTimeRecord = selectedModifyTimeRecord;
	}

	public long getUtcTimeSeconds() {
		return utcTimeSeconds;
	}

	public void setUtcTimeSeconds(long utcTimeSeconds) {
		this.utcTimeSeconds = utcTimeSeconds;
	}

}
