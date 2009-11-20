package no.knowit.test.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

public class OpenEjbBootStrap {

	private static Logger logger = Logger.getLogger(OpenEjbBootStrap.class);

	public static Context bootstrap() throws Exception {
		return bootstrap(null);
	}

	public static Context bootstrap(final Properties properties) throws Exception {
		try {
			// Check that OpenEJB is available
			Class.forName("org.apache.openejb.OpenEJB");

			// Initial properties are spesified in src/test/resources/jndi.properties
			return new InitialContext(properties);
		} 
		catch (Exception e) {
			logger.error("OpenEJB bootstrap failed", e);
			throw new RuntimeException(e);
		}
	}
}
