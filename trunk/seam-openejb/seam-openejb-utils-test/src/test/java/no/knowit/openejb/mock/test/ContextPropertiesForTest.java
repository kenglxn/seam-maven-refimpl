package no.knowit.openejb.mock.test;

import java.util.Properties;

public class ContextPropertiesForTest {
	
	/**
	 * Get a set of default propertis to boot the OpenEJB embedded container
	 * Note: This is only a suggestion.  
	 * @param properties
	 * @return
	 */
	public static Properties getDefaultContextProperties(final Properties properties) {
		Properties p = new Properties();

		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		p.put("log4j.category.org.jboss.seam.Component", "DEBUG");
		p.put("log4j.category.org.jboss.seam.transaction", "DEBUG");
		p.put("log4j.category.org.jboss.seam.mock", "DEBUG");
		p.put("log4j.category.no.knowit.seam.openejb.mock", "DEBUG");
		

		// Override properties from jndi.properties, e.g.
		//p.put("openejb.deployments.classpath.ear", "true");
		//p.put("openejb.altdd.prefix", "openejb");		
		
		if (properties != null) {
			p.putAll(properties);
		}
		return p;
	}
}
