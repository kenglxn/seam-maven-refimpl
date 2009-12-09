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
		p.put("openejb.altdd.prefix", "openejb");		
		
		// Database connection (same as *-ds.xml in JBoss)
		p.put("openejbDatabase = new://Resource?type", "DataSource");
		p.put("openejbDatabase.JdbcDriver ",           "org.hsqldb.jdbcDriver");
		p.put("openejbDatabase.JdbcUrl ",              "jdbc:hsqldb:mem:openejb_db");
		
    p.put("openejbDatabaseUnmanaged",              "new://Resource?type=DataSource");
    p.put("openejbDatabaseUnmanaged.JdbcDriver",   "org.hsqldb.jdbcDriver");
    p.put("openejbDatabaseUnmanaged.JdbcUrl",      "jdbc:hsqldb:mem:openejb_db");
    p.put("openejbDatabaseUnmanaged.JtaManaged",   "false");


		// override properties on "openejb-unit" persistence unit (src/main/resources/META-INF/persistence.xml)
		//p.put("openejb-test-unit.hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		
		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		p.put("log4j.category.org.jboss.seam.Component", "DEBUG");
		p.put("log4j.category.org.jboss.seam.transaction", "DEBUG");
		p.put("log4j.category.org.jboss.seam.mock", "DEBUG");
		p.put("log4j.category.no.knowit.seam.openejb.mock", "DEBUG");

		if (properties != null) {
			p.putAll(properties);
		}
		return p;
	}
}
