package foo.bean;

import java.util.Date;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.abuscom.core.qualifier.ActualApplicationId;
import com.abuscom.core.qualifier.ActualApplicationVersion;
import com.abuscom.core.qualifier.ActualUser;
import com.abuscom.core.qualifier.ApplicationSetup;

@Stateful
@SessionScoped
@Named("applicationContext")
public class TestContext {

	@Inject
	@ApplicationSetup
	private Properties configProperties;

	private static final String APPLICATION_ID = "START";

	@Resource
	private SessionContext sessionContext;

	@Produces
	@ActualUser
	public String getLoggedinUserName() {
		return sessionContext.getCallerPrincipal().getName();
	}

	public boolean isLoggedInUser() {
		return sessionContext.getCallerPrincipal().getName().equalsIgnoreCase("ANONYMOUS");
	}

	@Produces
	@ActualApplicationId
	public String getApplicationId() {
		return APPLICATION_ID;
	}

	@Produces
	@ActualApplicationVersion
	public String getApplicationVersion() {
		if (configProperties.containsKey("application.version")) {
			return configProperties.getProperty("application.version");
		} else {
			return "Not Defined";
		}

	}

	public Date getActualDate() {
		return new Date();
	}
}
