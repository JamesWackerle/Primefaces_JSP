package foo.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.PieChartModel;

import foo.domain.entity.ActivityStatus;
import foo.domain.entity.Machine;
import foo.domain.entity.ServiceOrder;

@ManagedBean(name = "reportingBean")
@ViewScoped
public class ReportingBean implements Serializable {

	private MeterGaugeChartModel meterGaugeModel;

	private PieChartModel pieModel;

	private CartesianChartModel categoryModel;
	private CartesianChartModel linearModel;
	private String openPurchaseOrders = "9";
	private String oldestOrder = "51";

	private Connection con;
	private Statement smt;
	private PreparedStatement ps;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	private ArrayList<ServiceOrder> orders = new ArrayList<ServiceOrder>();
	private ServiceOrder selectedServiceOrder;

	@PostConstruct
	public void init() throws SQLException {
		createMeterGaugeModel();
		createPieModel();
		createCategoryModel();
		createLinearModel();

		ActivityStatus.initializeStatusToText();
		initializeOrders();
	}

	public ReportingBean() {
		super();
	}

	/**
	 * Grabs all the Service orders for the users company.
	 * 
	 * @throws SQLException
	 */

	public void initializeOrders() throws SQLException {

		int tmpCompany = userBean.getSignedInUser().getCompany();

		try {

			System.out.println("getting Service Orders");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();
			String query = "select t$orno, t$acln, t$item, t$sern, t$cstp, t$desc, t$asta, (select ttso2.t$ordt from ttssoc200"
					+ tmpCompany
					+ " ttso2 where ttso.t$orno=ttso2.t$orno) as t$date, (select t$desc from ttsmdm030"
					+ tmpCompany
					+ " ttstdesc where ttstdesc.t$cstp = ttso.t$cstp) as service_type_desc from baan.ttssoc210"
					+ tmpCompany + " ttso";
			ResultSet rs = smt.executeQuery(query);

			while (rs.next()) {

				Timestamp tmpDate = new Timestamp(rs.getTimestamp("T$DATE").getTime()
						- userBean.getLocalTimeDifference().intValue());

				ServiceOrder tmpServiceOrder = new ServiceOrder(rs.getString("T$ORNO"), rs.getInt("T$ACLN"),
						rs.getString("T$ITEM"), rs.getString("T$SERN"), rs.getString("T$CSTP"), rs.getString("T$DESC"),
						rs.getInt("T$ASTA"), rs.getTimestamp("T$DATE"));
				tmpServiceOrder.setMachineObject(new Machine(rs.getString("T$SERN")));
				tmpServiceOrder.setServiceTypeDescription(rs.getString("SERVICE_TYPE_DESC"));
				tmpServiceOrder.setStatusObject(new ActivityStatus(rs.getInt("T$ASTA")));

				tmpServiceOrder.setLocalDate(tmpDate);

				orders.add(tmpServiceOrder);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			// con.close();
		}
	}

	public CartesianChartModel getCategoryModel() {
		return categoryModel;
	}

	public CartesianChartModel getLinearModel() {
		return linearModel;
	}

	private void createCategoryModel() {
		categoryModel = new CartesianChartModel();

		ChartSeries boys = new ChartSeries();
		boys.setLabel("Bob Jones");

		boys.set("2004", 120);
		boys.set("2005", 100);
		boys.set("2006", 44);
		boys.set("2007", 150);
		boys.set("2008", 25);

		ChartSeries girls = new ChartSeries();
		girls.setLabel("Tom Hanks");

		girls.set("2004", 52);
		girls.set("2005", 60);
		girls.set("2006", 110);
		girls.set("2007", 135);
		girls.set("2008", 120);

		categoryModel.addSeries(boys);
		categoryModel.addSeries(girls);
	}

	private void createLinearModel() {
		linearModel = new CartesianChartModel();

		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel("Series 1");

		series1.set(1, 2);
		series1.set(2, 1);
		series1.set(3, 3);
		series1.set(4, 6);
		series1.set(5, 8);

		LineChartSeries series2 = new LineChartSeries();
		series2.setLabel("Series 2");
		series2.setMarkerStyle("diamond");

		series2.set(1, 6);
		series2.set(2, 3);
		series2.set(3, 2);
		series2.set(4, 7);
		series2.set(5, 9);

		linearModel.addSeries(series1);
		linearModel.addSeries(series2);
	}

	private void createMeterGaugeModel() {

		List<Number> intervals = new ArrayList<Number>() {
			{
				add(50);
				add(120);
				add(220);
			}
		};
		meterGaugeModel = new MeterGaugeChartModel("70%", 140, intervals);
	}

	public PieChartModel getPieModel() {
		return pieModel;
	}

	private void createPieModel() {
		pieModel = new PieChartModel();

		pieModel.set("Robert C", 200);
		pieModel.set("Bob Jones", 400);
		pieModel.set("Mark Wahlberg", 400);
	}

	public MeterGaugeChartModel getMeterGaugeModel() {
		return meterGaugeModel;
	}

	public String getOpenPurchaseOrders() {
		return openPurchaseOrders;
	}

	public void setOpenPurchaseOrders(String openPurchaseOrders) {
		this.openPurchaseOrders = openPurchaseOrders;
	}

	public String getOldestOrder() {
		return oldestOrder;
	}

	public void setOldestOrder(String oldestOrder) {
		this.oldestOrder = oldestOrder;
	}

	public ServiceOrder getSelectedServiceOrder() {
		return selectedServiceOrder;
	}

	public void setSelectedServiceOrder(ServiceOrder selectedServiceOrder) {
		this.selectedServiceOrder = selectedServiceOrder;
	}

	public ArrayList<ServiceOrder> getOrders() {
		return orders;
	}

	public void setRequests(ArrayList<ServiceOrder> orders) {
		this.orders = orders;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

}
