package foo.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.primefaces.model.UploadedFile;

import foo.domain.entity.Machine;

@ManagedBean(name = "requestBean")
@ViewScoped
public class RequestBean {

	private String user = "user test ";
	private String machine;
	private String problem;
	private String comments;
	private int priority;
	private int department;
	private boolean emergency;
	private UploadedFile file;

	private Machine selectedMachine;

	private ArrayList<String> departmentDropdown = new ArrayList<String>();
	private ArrayList<Integer> priorityDropdown = new ArrayList<Integer>();

	private int idCount = 20;

	private Connection con;
	private Statement smt;
	private PreparedStatement ps;

	public final static int REGISTERED = 1;
	public final static int REGISTERED_WITH_WAIT = 2;
	public final static int ASSIGNED = 3;
	public final static int ASSIGNED_WITH_WAIT = 4;
	public final static int TRANSFERRED = 5;
	public final static int SOLVED = 6;
	public final static int ACCEPTED = 7;
	public final static int INSTANT_TRANSFERRED = 8;
	public final static int DEFAULT_PROC = 2;
	public final static int DEFAULT_ERRO = 2;

	public final static int REJECTED = 2;

	public final static int WAIT_NO = 2;
	public final static int WAIT_YES = 1;

	// TO DO NOT USED - this is for JPA Persistence of records.
	private EntityManagerFactory emf;
	private EntityManager em;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	@PostConstruct
	public void init() throws SQLException {

	}

	public RequestBean() throws SQLException {
		super();
	}

	/**
	 * 
	 * Enter a Service request in the intermediary 901 table.
	 * 
	 * @param actionEvent
	 * @throws SQLException
	 */
	public void submit(ActionEvent actionEvent) throws SQLException {

		System.out.println("Got to Submit");

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			System.out.println("************** GOT TO SUBMIT BEFORE INSERTION ************");

			String queryInsert = "insert into baan.ttsclm901"
					+ userBean.getSignedInUser().getCompany()
					+ " (t$ccll, t$user, t$date, t$stat, t$wait, t$cdis, t$sern, t$refcntd, t$refcntu, t$cgrp, t$prpr, t$shpd, t$proc, t$erro, t$seng, t$txta, t$updt, t$emer, t$appr)"
					+ "values (?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			// String queryInsert =
			// "insert into baan.ttsclm901124 (t$ccll, t$user, t$date, t$stat, t$wait, t$cdis, t$sern, t$refcntd, t$refcntu, t$cgrp, t$prpr, t$shpd) values (1, 'bob', CURRENT_DATE, '1', '2', 'test', 'PM1', '0', '0', '100', '50', 'test 3')";
			int tmpCommentRef = insertComment(getComments());
			int tmpEmergency = getEmergency() ? 1 : 2;

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();

			// set variables
			// ps.setString(1, "baan.ttsclm901124");
			ps.setString(1, "CA" + idCount + "TEST");
			ps.setString(2, userBean.getSignedInUser().getName());
			System.out.println("Name: " + userBean.getSignedInUser().getName());
			//
			ps.setInt(3, getStatusToInsert()); //
			ps.setInt(4, 2);
			ps.setString(5, " ");
			ps.setString(6, getSelectedMachine().getMachine());
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, getDepartment());
			ps.setInt(10, getPriority());
			ps.setString(11, getProblem());
			ps.setInt(12, DEFAULT_PROC);
			ps.setInt(13, DEFAULT_ERRO);
			ps.setString(14, " ");
			ps.setInt(15, tmpCommentRef);
			ps.setInt(16, 2); // 2 = not an update of an existing record
			ps.setInt(17, tmpEmergency);
			ps.setString(18, " ");

			ps.executeUpdate();
			idCount++;
			clearFields();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Successful", ""));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// ps.close();
			// smt.close();
			// con.close();

		}

	}

	/**
	 * 
	 * Returns the correct status that should be given to new request based on
	 * who entering the request, if the requester is a maintenance supervisor,
	 * if the Emergency flag is set, and the application wide approval level
	 * settings.
	 * 
	 * @return
	 */

	private int getStatusToInsert() {

		if (userBean.getSignedInUser().getIsMaintananceSupervisor() == 1) {
			return RequestBean.INSTANT_TRANSFERRED;

		} else if (userBean.isLevelOneApprovalOff() && userBean.isLevelTwoApprovalOff()) {
			return RequestBean.INSTANT_TRANSFERRED;

		} else if (getEmergency() == true) {
			return RequestBean.ASSIGNED;

		} else if (userBean.getSignedInUser().isSupervisor()) {
			return RequestBean.REGISTERED;

		} else if (userBean.isLevelOneApprovalOff()) {
			return RequestBean.REGISTERED;

		} else {
			return RequestBean.REGISTERED;
		}

	}

	/**
	 * 
	 * Inserts users comments into a the text tables by splitting them into
	 * smaller strings and inserted individual records.
	 * 
	 * @param comment
	 * @return
	 * @throws SQLException
	 */

	public int insertComment(String comment) throws SQLException {

		if (comment == null || comment.equals("") || comment.equals(" ")) {
			return 0;
		}

		int textRefNum = getNextTextId();
		int languageCode = 2; // English is 2
		int currentLineNum = 1;
		int numLines = ((comment.length() / 240) + 1);
		String currentTextLine = "";
		System.out.println("************** GOT TO TXTA CREATION BEFORE INSERTION ************");
		System.out.println("textRefNum: " + textRefNum + " numLines: " + numLines);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryInsertText010 = "insert into baan.ttttxt010" + userBean.getSignedInUser().getCompany()
					+ " (t$ctxt, t$clan, t$seqe, t$text, t$refcntd, t$refcntu) " + "values (?, ?, ?, ?, ?, ?)";

			String queryInsertText001 = "insert into baan.ttttxt001" + userBean.getSignedInUser().getCompany()
					+ " (t$ctxt, t$opwd, t$txtg, t$desc, t$refcntd, t$refcntu) " + "values (?, ?, ?, ?, ?, ?)";

			String queryInsertText002 = "insert into baan.ttttxt002"
					+ userBean.getSignedInUser().getCompany()
					+ " (t$ctxt, t$clan, t$kwd1, t$kwd2, t$kwd3, t$kwd4, t$ludt, t$user, t$nlin, t$refcntd, t$refcntu) "
					+ "values (?, ?, ?, ?, ?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?, ?, ?, ?)";

			ps = con.prepareStatement(queryInsertText010);
			// ps.clearParameters();
			System.out.println(queryInsertText010);

			for (int x = 0; x < comment.length(); x += 240) {

				if (x + 240 >= comment.length()) {
					currentTextLine = comment.substring(x);
				} else {
					currentTextLine = comment.substring(x, x + 240);
				}

				ps.setInt(1, textRefNum);
				ps.setInt(2, languageCode);
				ps.setInt(3, currentLineNum);
				ps.setString(4, currentTextLine);
				ps.setInt(5, 0);
				ps.setInt(6, 0);
				ps.executeUpdate();
				System.out.println("Inserted " + textRefNum + "line: " + currentTextLine + " into table txt010");

				ps.clearParameters();
				currentLineNum++;
			}

			// INSERT RECORD INTO TXT001
			System.out.println("Inserted Record: " + textRefNum + " into table txt001");
			ps = con.prepareStatement(queryInsertText001);
			ps.clearParameters();

			ps.setInt(1, textRefNum);
			ps.setString(2, "text");
			ps.setString(3, "text");
			ps.setString(4, " ");
			ps.setInt(5, 0);
			ps.setInt(6, 0);

			ps.executeUpdate();
			ps.clearParameters();

			// INSERT RECORD INTO TXT002
			System.out.println("Inserted Record: " + textRefNum + " into table txt002");
			ps = con.prepareStatement(queryInsertText002);

			ps.setInt(1, textRefNum);
			ps.setInt(2, languageCode);
			ps.setString(3, "Request Problem");
			ps.setString(4, " ");
			ps.setString(5, "Machine: ");
			ps.setString(6, " ");
			ps.setString(7, userBean.getSignedInUser().getName());
			ps.setInt(8, numLines);
			ps.setInt(9, 0);
			ps.setInt(10, 1);

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			return textRefNum;

		} finally {

			// ps.close();
			// con.close();
		}
		return textRefNum;

	}

	/**
	 * 
	 * Gets the next available text ID.
	 * 
	 * @return
	 */
	public int getNextTextId() {
		int nextAvailableTextId = 0;
		System.out.println("Grabbing next Text ID");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();
			String queryMachine = "select max (t$ctxt) as t$count from ttttxt010"
					+ userBean.getSignedInUser().getCompany();
			ResultSet rs = smt.executeQuery(queryMachine);

			rs.next();
			return (rs.getInt("T$COUNT") + 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return nextAvailableTextId;
	}

	/**
	 * 
	 * Clears all the data fields entered for a Service Request.
	 */
	private void clearFields() {
		this.user = "";
		this.machine = "";
		this.selectedMachine = null;
		this.problem = "";
		this.comments = "";
		this.priority = 25;
		this.department = 0;
		this.emergency = false;
	}

	/**
	 * 
	 * Auto completes a Machine Code.
	 * 
	 * @param query
	 * @return
	 */

	public ArrayList<Machine> completeMachineName(String query) {
		ArrayList<Machine> results = new ArrayList<Machine>();
		System.out.println("Grabbing Auto complete for Machine Name");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();
			String queryMachine = "select distinct t$sern from baan.ttscfg200"
					+ userBean.getSignedInUser().getCompany() + " where t$sern like '%" + query + "%' and rownum <= 10";
			ResultSet rs = smt.executeQuery(queryMachine);

			while (rs.next()) {
				results.add(new Machine(rs.getString("T$SERN")));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ArrayList<String> getDepartmentDropdown() {

		return departmentDropdown;
	}

	public void setDepartmentDropdown(ArrayList<String> departmentDropdown) {
		this.departmentDropdown = departmentDropdown;
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

	public boolean getEmergency() {
		return emergency;
	}

	public void setEmergency(boolean emergency) {
		this.emergency = emergency;
	}

	public String getFile() {
		return file.getFileName();
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public ArrayList<Integer> getPriorityDropdown() {
		return priorityDropdown;
	}

	public void setPriorityDropdown(ArrayList<Integer> priorityDropdown) {
		this.priorityDropdown = priorityDropdown;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public Machine getSelectedMachine() {
		return selectedMachine;
	}

	public void setSelectedMachine(Machine selectedMachine) {
		this.selectedMachine = selectedMachine;
	}

}
