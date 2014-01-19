package foo.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import foo.domain.entity.Department;
import foo.domain.entity.Item;
import foo.domain.entity.Machine;
import foo.domain.entity.Priority;
import foo.domain.entity.User;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean {

	private ArrayList<String> userDropdown;
	private Connection con;
	private Statement smt;
	private PreparedStatement ps;
	private String selectedUserName;
	private User signedInUser;

	private boolean levelOneApprovalOff;
	private boolean levelTwoApprovalOff;

	private BigDecimal localTimeDifference = new BigDecimal(0);

	private ArrayList<Department> departmentDropdownObject = new ArrayList<Department>();
	private ArrayList<Priority> priorityDropdownObject = new ArrayList<Priority>();

	private ArrayList<SelectItem> priorityOptions = new ArrayList<SelectItem>();
	private ArrayList<SelectItem> departmentOptions = new ArrayList<SelectItem>();

	public UserBean() throws SQLException {
		super();
	}

	@PostConstruct
	public void init() throws SQLException {
		initializeUserDropdown();
		System.out.println("******** In User constructer");
		signedInUser = new User("rciavagl", "aherkend", 1, 1, 1, 1, 1, 1);
		signedInUser.setServiceRequestApprovalPermissionTOrF(true);
		signedInUser.setCompany(124);
		signedInUser.setEmployeeNumber("10000");
		signedInUser.setIsMaintananceSupervisor(2);

		// Initialization for Department and Priority Dropdowns
		initializeDepartmentDropdownObject();
		initializePriorityDropdownObject();
		initializeOptions();

		initializeMachineToDesc();

	}

	/**
	 * Takes the selected User and grabs all the permissions for them and signs
	 * them in by setting signedInUser.
	 */

	public void selectUser() throws SQLException {
		System.out.println("Selecting User");

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select t$logn, t$supv, t$sreq, t$apps, t$rreq, t$vord, t$omat, t$imat, t$mant, (select t$emno from ttsmdm150124 emno where userp.t$logn=emno.t$logn) as t$emno from ttsclm902124 userp where t$logn='"
					+ selectedUserName + "'";
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				signedInUser = new User(rs.getString("T$LOGN"), rs.getString("T$SUPV"), rs.getInt("T$SREQ"),
						rs.getInt("T$APPS"), rs.getInt("T$RREQ"), rs.getInt("T$VORD"), rs.getInt("T$OMAT"),
						rs.getInt("T$IMAT"));
				boolean tmpAccessAssigned = (rs.getInt("T$APPS") == 2) ? false : true;
				signedInUser.setServiceRequestApprovalPermissionTOrF(tmpAccessAssigned);
				signedInUser.setEmployeeNumber(rs.getString("T$EMNO"));
				signedInUser.setIsMaintananceSupervisor(rs.getInt("T$MANT"));

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
	 * grabs all the department and department descriptions for a company and
	 * puts them as objects into a Arraylist.
	 * 
	 * @throws SQLException
	 */
	private void initializeDepartmentDropdownObject() throws SQLException {
		try {
			System.out.println("getting department dropdown objects");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select distinct t$desc, t$cgrp from baan.ttsclm050" + signedInUser.getCompany()
					+ " where t$belt=1";

			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				int tmpDepartment;
				try {
					tmpDepartment = Integer.parseInt(rs.getString("T$CGRP"));
				} catch (NumberFormatException e) {
					tmpDepartment = 0;
				}
				departmentDropdownObject.add(new Department(rs.getString("T$DESC"), tmpDepartment));
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
	 * grabs all the priorities and priority descriptions for a company and puts
	 * them as objects into a Arraylist.
	 * 
	 * @throws SQLException
	 */
	private void initializePriorityDropdownObject() throws SQLException {
		try {
			System.out.println("getting priority dropdown");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select distinct t$dsca, t$prio from baan.ttcmcs070700";
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				priorityDropdownObject.add(new Priority(rs.getString("T$DSCA"), rs.getInt("T$PRIO")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			con.close();
			smt.close();
		}
	}

	/**
	 * This takes all the department objects and priority objects and adds them
	 * as Select items to be used for the dropdown menus.
	 * 
	 */
	private void initializeOptions() {

		departmentOptions.add(new SelectItem("", "Select"));
		for (Department d : departmentDropdownObject) {
			departmentOptions.add(new SelectItem(d.getDepartmentLabel(), d.getDepartmentLabel()));
		}

		priorityOptions.add(new SelectItem("", "Select"));
		for (Priority p : priorityDropdownObject) {
			priorityOptions.add(new SelectItem(p.getPriorityLabel(), p.getPriorityLabel()));
		}
	}

	/**
	 * Adds all the machine codes and descriptions from the ttscfg200 table to a
	 * hashmap. This is used in Autocomplete and when a Machine object is
	 * created.
	 */
	public void initializeMachineToDesc() throws SQLException {
		try {
			Connection con_init;
			Statement smt_init;

			System.out.println("getting Machine Descriptions");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con_init = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt_init = con_init.createStatement();
			String query = "select t$sern, t$desc from ttscfg200" + signedInUser.getCompany();
			ResultSet rs = smt_init.executeQuery(query);

			while (rs.next()) {
				Machine.MACHINE_TO_DESC.put(rs.getString("T$SERN"), rs.getString("T$DESC"));
			}
			con_init.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}

	}

	/**
	 * NOT USED
	 * 
	 * This is not used but puts all the items that could be used to complete
	 * Service Orders into a hashmap. These are paired with the description for
	 * the item.
	 * 
	 * @throws SQLException
	 */

	public void initializeItemToDesc() throws SQLException {
		try {
			Connection con_init;
			Statement smt_init;

			System.out.println("getting Item Descriptions");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con_init = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt_init = con_init.createStatement();
			String query = "select distinct t$item, t$dsca from ttcibd001110 where rownum <= 10";
			ResultSet rs = smt_init.executeQuery(query);

			while (rs.next()) {
				Item.ITEM_TO_DESC.put(rs.getString("T$ITEM"), rs.getString("T$DSCA"));
				System.out.println("Item in Hashmap: " + Item.ITEM_TO_DESC.get(rs.getString("T$ITEM")));
			}

			con_init.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}

	}

	public void initializeUserDropdown() throws SQLException {

		userDropdown = new ArrayList<String>();
		userDropdown.add("Select");
		try {
			System.out.println("getting User dropdown");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select distinct t$logn from baan.ttsclm902124";
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				userDropdown.add(rs.getString("T$LOGN"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
			smt.close();
		}
	}

	/**
	 * Initializes the Level 1 and level 2 approvals on or off. This is set at
	 * an application level.
	 * 
	 * @throws SQLException
	 */
	public void initializeLevelApproval() throws SQLException {

		try {
			System.out.println("getting Level Approval");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select * from baan.ttsclm900124";
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				if (rs.getInt("T$LVL1") == 2) {
					levelOneApprovalOff = true;
				} else {
					levelOneApprovalOff = false;
				}

				if (rs.getInt("T$LVL2") == 2) {
					levelOneApprovalOff = true;
				} else {
					levelOneApprovalOff = false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	public ArrayList<String> getUserDropdown() {
		return userDropdown;
	}

	public void setUserDropdown(ArrayList<String> userDropdown) {
		this.userDropdown = userDropdown;
	}

	public String getSelectedUserName() {
		return selectedUserName;
	}

	public void setSelectedUserName(String userName) {
		this.selectedUserName = userName;
	}

	public User getSignedInUser() {
		return signedInUser;
	}

	public void setSignedInUser(User signedInUser) {
		this.signedInUser = signedInUser;
	}

	public boolean isLevelOneApprovalOff() {
		return levelOneApprovalOff;
	}

	public void setLevelOneApprovalOff(boolean levelOneApprovalOff) {
		this.levelOneApprovalOff = levelOneApprovalOff;
	}

	public boolean isLevelTwoApprovalOff() {
		return levelTwoApprovalOff;
	}

	public void setLevelTwoApprovalOff(boolean levelTwoApprovalOff) {
		this.levelTwoApprovalOff = levelTwoApprovalOff;
	}

	public ArrayList<Department> getDepartmentDropdownObject() {
		return departmentDropdownObject;
	}

	public void setDepartmentDropdownObject(ArrayList<Department> departmentDropdownObject) {
		this.departmentDropdownObject = departmentDropdownObject;
	}

	public ArrayList<Priority> getPriorityDropdownObject() {
		return priorityDropdownObject;
	}

	public void setPriorityDropdownObject(ArrayList<Priority> priorityDropdownObject) {
		this.priorityDropdownObject = priorityDropdownObject;
	}

	public ArrayList<SelectItem> getPriorityOptions() {
		return priorityOptions;
	}

	public void setPriorityOptions(ArrayList<SelectItem> priorityOptions) {
		this.priorityOptions = priorityOptions;
	}

	public ArrayList<SelectItem> getDepartmentOptions() {
		return departmentOptions;
	}

	public void setDepartmentOptions(ArrayList<SelectItem> departmentOptions) {
		this.departmentOptions = departmentOptions;
	}

	public BigDecimal getLocalTimeDifference() {
		return localTimeDifference;
	}

	public void setLocalTimeDifference(BigDecimal localTimeDifference) {
		this.localTimeDifference = localTimeDifference;
	}

}
