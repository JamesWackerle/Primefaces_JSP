package foo.producer;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.enterprise.inject.Produces;

import com.abuscom.core.qualifier.ApplicationSetup;

public class ConfigProducer {

	private static final String PROPERTIES_FILE_NAME = "application";

	@Produces
	@ApplicationSetup
	Properties readConfigProperities() {
		Properties configProperties = new Properties();
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIES_FILE_NAME);
			for (String key : bundle.keySet()) {
				configProperties.put(key, bundle.getString(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configProperties;
	}
}
