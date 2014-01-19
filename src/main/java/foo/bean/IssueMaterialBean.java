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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import foo.domain.entity.Item;
import foo.domain.entity.ServiceOrder;
import foo.domain.entity.ServiceOrderMaterialRecord;

@ManagedBean(name = "issueMaterialBean")
@SessionScoped
public class IssueMaterialBean {

	private ServiceOrder selectedServiceOrder;

	@ManagedProperty("#{userBean}")
	private UserBean userBeanTracking;
	private String signedInUserName;

	private Connection con;
	private Statement smt;
	private PreparedStatement ps;

	private int addMaterialLineNumber;
	private int addItem;

	private BigDecimal selectedRecordQuanity;
	private BigDecimal addQuantity;

	private Item selectedAddItem;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	private ArrayList<ServiceOrderMaterialRecord> records;

	private ServiceOrderMaterialRecord selectedRecord; 

	private String issueMaterialEmployeeNumber;
	private String addMaterialEmployeeNumber;

	@PostConstruct
	public void init() throws SQLException {
		signedInUserName = userBeanTracking.getSignedInUser().getName(); // Success!

		// Test Service Order for quicker testing - enables you to go directly to the tracking page
		selectedServiceOrder.setOrderNumber("SV0000026");
		selectedServiceOrder.setActivity(10);
		selectedServiceOrder.setStatus(15);

		initializeServiceOrderMaterialRecords();
		issueMaterialEmployeeNumber = userBean.getSignedInUser().getEmployeeNumber();
		addMaterialEmployeeNumber = userBean.getSignedInUser().getEmployeeNumber();

	}

	public IssueMaterialBean() {
		super();
		this.selectedRecord = new ServiceOrderMaterialRecord();
		this.selectedRecord.setQuantity(new BigDecimal(0));
		this.selectedServiceOrder = new ServiceOrder();

	}

	/**
	 * Grabs all the Material records for the selected Service order.
	 * 
	 * @throws SQLException
	 */
	public void initializeServiceOrderMaterialRecords() throws SQLException {

		int tmpCompany = userBean.getSignedInUser().getCompany();
		records = new ArrayList<ServiceOrderMaterialRecord>();

		try {

			System.out.println("getting Service Material Records " + tmpCompany);

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select t$orno, t$acln, t$lino, t$item, t$emno, t$qtdl, t$cuqs, (select t$dsca from ttcibd001"
					+ 110
					+ " tttdesc where ttrecord.t$item=tttdesc.t$item) as item_desc from ttssoc220"
					+ tmpCompany
					+ " ttrecord where t$orno='"
					+ selectedServiceOrder.getOrderNumber()
					+ "' and t$acln="
					+ selectedServiceOrder.getActivity();
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {
				ServiceOrderMaterialRecord tmpServiceOrderMaterialRecord = new ServiceOrderMaterialRecord(
						rs.getString("T$ORNO"), rs.getInt("T$ACLN"), rs.getInt("T$LINO"), rs.getString("T$ITEM"),
						rs.getString("ITEM_DESC"));
				tmpServiceOrderMaterialRecord.setActivityLineNumber("" + rs.getInt("T$ACLN") + rs.getInt("T$LINO"));
				tmpServiceOrderMaterialRecord.setEmployeeNumber(rs.getString("T$EMNO"));
				tmpServiceOrderMaterialRecord.setQuantity(rs.getBigDecimal("T$QTDL"));
				tmpServiceOrderMaterialRecord.setQuantityUnit(rs.getString("T$CUQS"));
				records.add(tmpServiceOrderMaterialRecord);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	/**
	 * 
	 * NOT USED
	 * 
	 * This completes an Item Name for a item part that would be used to
	 * complete service orders. However this was two taxing an operation to
	 * perform because all the item are in one table.
	 * 
	 * @param query
	 * @return
	 */

	public ArrayList<Item> completeItemName(String query) {
		ArrayList<Item> results = new ArrayList<Item>();
		System.out.println("Grabbing Auto complete for Item Name");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();
			String queryItem = "select t$item from ttcibd001110" + " where t$item like '%" + query
					+ "%' and rownum <= 10";
			ResultSet rs = smt.executeQuery(queryItem);

			while (rs.next()) {
				results.add(new Item(rs.getString("T$ITEM")));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Issues a material to a Service Order material record by adding it to the
	 * Intermediary table 920 to be picked up by AFS.
	 * 
	 * @throws SQLException
	 */
	public void issueMaterial() throws SQLException {
		System.out.println("Issue Material: " + selectedRecord.getItem());
		FacesContext context = FacesContext.getCurrentInstance();

		if (!isValidEmployeeNumber(issueMaterialEmployeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Employee Number",
					"The employee number you entered is not valid.");
			context.addMessage(null, message);
			return;
		} else {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("dlg.hide()");
		}

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryInsert = "insert into baan.ttssoc920"
					+ userBean.getSignedInUser().getCompany()
					+ " (t$orno, t$lino, t$acln, t$item, t$emno, t$qtdl, t$proc, t$refcntd, t$refcntu, t$eqan, t$adqt, t$date, t$matl) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?)";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();
			System.out.println("Signed in User Employee Number: " + userBean.getSignedInUser().getEmployeeNumber());
			System.out.println("Issue Material Employee Number: " + issueMaterialEmployeeNumber);

			// set variables
			ps.setString(1, selectedRecord.getOrderNumber());
			ps.setInt(2, selectedRecord.getLineNumber());
			ps.setInt(3, selectedRecord.getActivity());
			ps.setString(4, selectedRecord.getItem());
			ps.setString(5, issueMaterialEmployeeNumber);
			ps.setBigDecimal(6, selectedRecordQuanity);
			ps.setInt(7, 2); // not processed
			ps.setInt(8, 0); // refcntd
			ps.setInt(9, 0); // refcntu

			ps.setInt(10, 0);// t$eqan
			ps.setInt(11, 0);// t$adqt

			ps.setInt(12, insertedRecordMaterialCode());

			System.out.println("Query to insert isssue material: " + queryInsert);
			ps.executeUpdate();

			context.addMessage(null, new FacesMessage("Successful", ("Issued Material")));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * Add Material to an existing Service Order to an Intermediary table. AFS
	 * would then pick this up and process it to add to the 920 table.
	 * 
	 * @throws SQLException
	 */

	public void addMaterial() throws SQLException {
		System.out.println("Add Material: " + addItem);
		FacesContext context = FacesContext.getCurrentInstance();

		if (!isValidEmployeeNumber(addMaterialEmployeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Employee Number",
					"The employee number you entered is not valid.");
			context.addMessage(null, message);
			return;
		} else if (!isValidItem(addItem)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Item",
					"The Item you entered is not valid.");
			context.addMessage(null, message);
			return;
		} else {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("dlgAdd.hide()");
		}

		int tmpNextLine = nextLineNumber();

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");
			smt = con.createStatement();

			String queryInsert = "insert into baan.ttssoc920"
					+ userBean.getSignedInUser().getCompany()
					+ " (t$orno, t$lino, t$acln, t$item, t$emno, t$qtdl, t$proc, t$refcntd, t$refcntu, t$eqan, t$adqt, t$date, t$matl) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYS_EXTRACT_UTC(SYSTIMESTAMP), ?)";

			ps = con.prepareStatement(queryInsert);
			ps.clearParameters();
			System.out.println("Signed in User Employee Number: " + userBean.getSignedInUser().getEmployeeNumber());
			System.out.println("Issue Material Employee Number: " + addMaterialEmployeeNumber);

			// set Query Parameters
			ps.setString(1, selectedServiceOrder.getOrderNumber());
			ps.setInt(2, tmpNextLine);
			ps.setInt(3, selectedServiceOrder.getActivity());
			ps.setInt(4, addItem);
			ps.setString(5, addMaterialEmployeeNumber);
			ps.setBigDecimal(6, addQuantity);
			ps.setInt(7, 2); // not processed
			ps.setInt(8, 0); // refcntd
			ps.setInt(9, 0); // refcntu

			ps.setInt(10, 0);// t$eqan
			ps.setInt(11, 0);// t$adqt
			ps.setInt(12, insertedRecordMaterialCode());

			System.out.println("Query to insert add material: " + queryInsert);
			ps.executeUpdate();

			context.addMessage(null, new FacesMessage("Successful", ("Added Material")));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			con.close();

		}
	}

	/**
	 * Returns the enumerated field code that should be inserted for a record.
	 * Estimated for records that are in status free. Material for all others.
	 * 
	 * @return
	 */

	private int insertedRecordMaterialCode() {
		if (selectedServiceOrder.getStatus() == 5) {
			return 1; // Status for Free - This means that it is estimated for
						// completion of the service order.
		} else {
			return 2; // Status for other Add materials in Released and
						// Completed - this
						// means that it is actual material to be added to the
						// service order.
						//
		}
	}

	/**
	 * Returns the next available line number for a Material record for this
	 * Service order. It checks for the max value from both the Intermediary
	 * table 920 and the actual table.
	 * 
	 * @return
	 */
	public int nextLineNumber() {

		int nextAvailableLineNumber = 10; // Default Value
		int next220Table = 10;
		int next920Table = 10;
		try {
			System.out.println("testing if isValidEmployeeNumber in Issue Material Bean");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();

			// Get Max Line Number from 220
			String query220 = "select max(t$lino) as t$max from ttssoc220" + userBean.getSignedInUser().getCompany()
					+ " where t$orno=? and t$acln=?";

			ps = con.prepareStatement(query220);
			ps.clearParameters();

			// Set Query Parameters
			ps.setString(1, selectedServiceOrder.getOrderNumber());
			ps.setInt(2, selectedServiceOrder.getActivity());

			ResultSet rs = ps.executeQuery();
			ps.clearParameters();
			if (rs.next()) {
				next220Table = rs.getInt("T$MAX") + 10;
				System.out.println("next 220: " + next220Table);
			}

			// Get Max Line Number from 920
			String query920 = "select max(t$lino) as t$max from ttssoc920" + userBean.getSignedInUser().getCompany()
					+ " where t$orno=? and t$acln=?";

			ps = con.prepareStatement(query920);
			ps.clearParameters();

			// Set Query Parameters
			ps.setString(1, selectedServiceOrder.getOrderNumber());
			ps.setInt(2, selectedServiceOrder.getActivity());

			rs = ps.executeQuery();
			ps.clearParameters();
			if (rs.next()) {
				next920Table = rs.getInt("T$MAX") + 10;
				System.out.println("next 920: " + next920Table);
			}

			nextAvailableLineNumber = (next220Table > next920Table) ? next220Table : next920Table; // set
																									// to
																									// the
																									// highest
																									// line
																									// number

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				smt.close();
			} catch (SQLException e) {
			}

		}
		return nextAvailableLineNumber;
	}

	/**
	 * Checks to see if the Employee number entered is an actual valid Employee
	 * number from the database.
	 * 
	 * @param employeeNumber
	 * @return
	 */

	public boolean isValidEmployeeNumber(String employeeNumber) {

		boolean valid = false;

		try {
			System.out.println("testing if isValidEmployeeNumber in Issue Material Bean");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();

			String query = "select * from ttsmdm140" + userBean.getSignedInUser().getCompany() + " where t$emno='"
					+ employeeNumber + "'";

			ResultSet rs = smt.executeQuery(query);

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
	 * 
	 * Checks to see if an Item code is actually a valid item from the database.
	 * 
	 * @param item
	 * @return
	 */

	public boolean isValidItem(int item) {

		boolean valid = false;

		try {
			System.out.println("testing if isValidEmployeeNumber in Issue Material Bean");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();

			String query = "select * from  ttcibd001110 where t$item like '%" + item + "'";
			ResultSet rs = smt.executeQuery(query);

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

	public ArrayList<ServiceOrderMaterialRecord> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<ServiceOrderMaterialRecord> records) {
		this.records = records;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public ServiceOrderMaterialRecord getSelectedRecord() {
		return selectedRecord;
	}

	public void setSelectedRecord(ServiceOrderMaterialRecord selectedRecord) {
		this.selectedRecord = selectedRecord;
	}

	public int getAddMaterialLineNumber() {
		return addMaterialLineNumber;
	}

	public void setAddMaterialLineNumber(int addMaterialLineNumber) {
		this.addMaterialLineNumber = addMaterialLineNumber;
	}

	public int getAddItem() {
		return addItem;
	}

	public void setAddItem(int addItem) {
		this.addItem = addItem;
	}

	public Item getSelectedAddItem() {
		return selectedAddItem;
	}

	public void setSelectedAddItem(Item selectedAddItem) {
		this.selectedAddItem = selectedAddItem;
	}

	public BigDecimal getSelectedRecordQuanity() {
		return selectedRecordQuanity;
	}

	public void setSelectedRecordQuanity(BigDecimal selectedRecordQuanity) {
		this.selectedRecordQuanity = selectedRecordQuanity;
	}

	public String getIssueMaterialEmployeeNumber() {
		return issueMaterialEmployeeNumber;
	}

	public void setIssueMaterialEmployeeNumber(String issueMaterialEmployeeNumber) {
		this.issueMaterialEmployeeNumber = issueMaterialEmployeeNumber;
	}

	public String getAddMaterialEmployeeNumber() {
		return addMaterialEmployeeNumber;
	}

	public void setAddMaterialEmployeeNumber(String addMaterialEmployeeNumber) {
		this.addMaterialEmployeeNumber = addMaterialEmployeeNumber;
	}

	public BigDecimal getAddQuantity() {
		return addQuantity;
	}

	public void setAddQuantity(BigDecimal addQuantity) {
		this.addQuantity = addQuantity;
	}
}
