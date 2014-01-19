package foo.ui.view;

import java.io.Serializable;

public class AboutView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String version;
	
	private String applicationName;
	
	private String applicationId;
	
	private String applicationContactName;
	
	private String applicationContactEmail;
	
	private String applicationDescription;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationContactName() {
		return applicationContactName;
	}

	public void setApplicationContactName(String applicationContactName) {
		this.applicationContactName = applicationContactName;
	}

	public String getApplicationContactEmail() {
		return applicationContactEmail;
	}

	public void setApplicationContactEmail(String applicationContactEmail) {
		this.applicationContactEmail = applicationContactEmail;
	}

	public String getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

}
