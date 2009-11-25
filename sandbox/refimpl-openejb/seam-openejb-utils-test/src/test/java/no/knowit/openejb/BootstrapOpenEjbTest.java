package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;

import no.knowit.openejb.BootStrapOpenEjb;

import org.apache.openejb.OpenEJB;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BootstrapOpenEjbTest {
	
	protected Context initialContext;
	
	@BeforeClass
	public void setUp() throws Exception {
		Properties p = BootStrapOpenEjb.getDefaultProperties(null);

		// Override default property values
		p.put("openejb.deployments.classpath.ear", "false");
		
		// Database connection (same as *-ds.xml in JBoss)
		p.put("openejbTestDatabase = new://Resource?type", "DataSource");
		p.put("openejbTestDatabase.JdbcDriver ", "org.hsqldb.jdbcDriver");
		p.put("openejbTestDatabase.JdbcUrl ", "jdbc:hsqldb:mem:moviedb");

		// override properties on "openejb-test-unit" persistence unit (src/main/resources/META-INF/persistence.xml)
		//p.put("openejb-test-unit.hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		
		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		p.put("log4j.category.no.knowit.openejb.BootstrapOpenEjbTest", "debug");
		p.put("log4j.category.org.jboss.seam.Component", "DEBUG");
		p.put("log4j.category.org.jboss.seam.mock", "DEBUG");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shallBootstrapOpenEjb() throws Exception {
		
		
		initialContext = BootStrapOpenEjb.bootstrap(null);
		Assert.assertNotNull(initialContext);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test(dependsOnMethods={ "shallBootstrapOpenEjb" })
	public void shallCloseInitialContext() throws Exception {
		initialContext = BootStrapOpenEjb.closeInitialContext();
		Assert.assertNull(initialContext);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test(dependsOnMethods={ "shallCloseInitialContext" })
	public void shallShutdown() throws Exception {
		initialContext = BootStrapOpenEjb.bootstrap(null);
		initialContext = BootStrapOpenEjb.shutdown();
		Assert.assertFalse(OpenEJB.isInitialized());
	}
}
