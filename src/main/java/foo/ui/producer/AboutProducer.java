package foo.ui.producer;

import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.abuscom.core.qualifier.ActualApplicationId;
import com.abuscom.core.qualifier.ApplicationSetup;
import foo.ui.view.AboutView;

public class AboutProducer {
	
	String ND = "NOT DEFINED";
	
	@Inject
	@ApplicationSetup
	private Properties applicationProperties;
	
	@Inject
	@ActualApplicationId
	private String applicationId;

	@Produces
	@Named("aboutView")
	@RequestScoped
	public AboutView createAbout() {
		AboutView aboutView = new AboutView();
		
		aboutView.setVersion(applicationProperties.containsKey("application.version") ? applicationProperties.getProperty("application.version") : ND);
		aboutView.setApplicationContactName(applicationProperties.containsKey("application.contact") ? applicationProperties.getProperty("application.contact") : ND);
		aboutView.setApplicationContactEmail(applicationProperties.containsKey("application.contact.email") ? applicationProperties.getProperty("application.contact.email") : ND);
		aboutView.setApplicationDescription(applicationProperties.containsKey("application.description") ? applicationProperties.getProperty("application.description") : ND);
		aboutView.setApplicationName(applicationProperties.containsKey("application.name") ? applicationProperties.getProperty("application.name") : ND);
		aboutView.setApplicationId(applicationId);
		return aboutView;
	}
	
	
}
