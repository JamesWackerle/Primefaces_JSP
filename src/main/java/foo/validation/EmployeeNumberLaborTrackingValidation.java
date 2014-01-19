package foo.validation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;

import foo.bean.TrackingBean;
import foo.bean.UserBean;

/*
 * This is a custom validator that could be used on JSF components. It is not used in Service, however it is a good example of how one might be accomplished. 
 * Instead of using this validator on a component. Validation is done inside action methods for Backing beans.
 */

@FacesValidator(value = "employeeNumberValidator")
public class EmployeeNumberLaborTrackingValidation implements Validator {

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	@ManagedProperty(value = "#{trackingBean}")
	private TrackingBean trackingBeanValidation;

	private Connection con;
	private Statement smt;
	private PreparedStatement ps;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String employeeNumber = String.valueOf(value);

		if (!isValidEmployeeNumber(employeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Employee Number",
					"The employee number you entered is not valid.");
			throw new ValidatorException(message);
		} else if (!canStartNewRecordForActivity(employeeNumber)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Employee Number",
					"The employee number you entered already has a tracking record started for this Activity Line.");
			throw new ValidatorException(message);
		} else {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("dlg.hide()");
			requestContext.execute("addTime.hide()");
		}
	}

	public boolean isValidEmployeeNumber(String employeeNumber) {

		boolean valid = false;

		try {
			System.out.println("testing if isValidEmployeeNumber");

			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@brubln04:1521:lndb1", "baan", "baan");

			smt = con.createStatement();

			String query = "select * from ttsmdm140124 where t$emno='" + employeeNumber + "'";

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

	public boolean canStartNewRecordForActivity(String employeeNumber) { // stub
																			// Method

		return true;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public TrackingBean getTrackingBeanValidation() {
		return trackingBeanValidation;
	}

	public void setTrackingBeanValidation(TrackingBean trackingBean) {
		this.trackingBeanValidation = trackingBean;
	}
}