package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;

import no.knowit.openejb.BootStrapOpenEjb;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BootstrapOpenEjbTest {
	
	protected Context initialContext;
	
	@BeforeClass
	public void setUp() throws Exception {
		Properties p = BootStrapOpenEjb.getDefaultProperties(null);

		// Override default property value
		p.put("openejb.deployments.classpath.ear", "false");
		
		// Database connection (*-ds.xml in JBoss)
		p.put("movieDatabase = new://Resource?type", "DataSource");
		p.put("movieDatabase.JdbcDriver ", "org.hsqldb.jdbcDriver");
		p.put("movieDatabase.JdbcUrl ", "jdbc:hsqldb:mem:moviedb");

		// override properties on your "movie-unit" persistence unit (persistence.xml)
		p.put("movie-unit.hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		
		
		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		p.put("log4j.category.org.jboss.seam.Component", "DEBUG");
		p.put("log4j.category.org.jboss.seam.mock", "DEBUG");
		p.put("log4j.category.no.knowit.openejb", "debug");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shallBootstrapOpenEjb() throws Exception {
		initialContext = BootStrapOpenEjb.bootstrap(null);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test(dependsOnMethods={ "shallBootstrapOpenEjb" })
	public void shallCloseInitialContext() throws Exception {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}
}
