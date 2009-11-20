package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

public class OpenEjbBootStrap {

	protected static Logger log = Logger.getLogger(OpenEjbBootStrap.class);

	public static Context bootstrap() throws Exception {
		return bootstrap(null);
	}

	public static Context bootstrap(final Properties properties) throws Exception {
		try {
			// Initial properties are specified in src/main/resources/jndi.properties
			Context context = new InitialContext(properties);
			
			// Check that OpenEJB is available
			Class.forName("org.apache.openejb.OpenEJB");
			
			return context;
		} 
		catch (Exception e) {
			log.fatal("OpenEJB bootstrap failed.", e);
			throw new RuntimeException(e);
		}
	}
}
