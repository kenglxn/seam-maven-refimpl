package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class BootStrapOpenEjb {

	public static Context bootstrap() throws Exception {
		return bootstrap(null);
	}

	public static Context bootstrap(final Properties properties) throws Exception {
		try {
			// Check that OpenEJB is available
			Class.forName("org.apache.openejb.OpenEJB");

			// Set some default values
			Properties p = new Properties();
			p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
			p.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}"); // Corresponds to JBoss JNDI lookup
			p.put("openejb.deployments.classpath.ear", "true"); //for multi module projects

			// Overrides default properties if key match
			if(properties != null) {
				p.putAll(properties);
			}
			return new InitialContext(p);
		} 
		catch (Exception e) {
			// Since OpenEJB failed to start, we do not have a logger
			System.out.println("\n*******\nOpenEJB bootstrap failed: " + e + "\n*******");
			throw new RuntimeException(e);
		}
	}
}
