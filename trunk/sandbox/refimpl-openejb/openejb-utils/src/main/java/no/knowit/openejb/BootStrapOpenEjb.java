package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class BootStrapOpenEjb {

	//protected static Logger log = Logger.getLogger(OpenEjbBootStrap.class);

	public static Context bootstrap() throws Exception {
		return bootstrap(null);
	}

	public static Context bootstrap(final Properties properties) throws Exception {
		try {
			// Check that OpenEJB is available
			Class.forName("org.apache.openejb.OpenEJB");
			
			// Initial properties are specified in src/main/resources-openejb/jndi.properties
			// Properties defined in properties param will override initial properties  
			return new InitialContext(properties);
		} 
		catch (Exception e) {
			// Since OpenEJB failed to start, we do not have a logger
			System.out.println("\n*******\nOpenEJB bootstrap failed: " + e + "\n*******");
			throw new RuntimeException(e);
		}
	}
}
