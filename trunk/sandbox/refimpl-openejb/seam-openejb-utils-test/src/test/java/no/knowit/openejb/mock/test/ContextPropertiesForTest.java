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

		// Override default property values
		p.put("openejb.deployments.classpath.ear", "false");
		
		// Database connection (same as *-ds.xml in JBoss)
		p.put("openejbTestDatabase = new://Resource?type", "DataSource");
		p.put("openejbTestDatabase.JdbcDriver ", "org.hsqldb.jdbcDriver");
		p.put("openejbTestDatabase.JdbcUrl ", "jdbc:hsqldb:mem:moviedb");

		// override properties on "openejb-test-unit" persistence unit (src/main/resources/META-INF/persistence.xml)
		//p.put("openejb-test-unit.hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		
		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		p.put("log4j.category.org.jboss.seam.Component", "DEBUG");
		p.put("log4j.category.org.jboss.seam.mock", "DEBUG");

		if (properties != null) {
			p.putAll(properties);
		}
		return p;
	}


}
