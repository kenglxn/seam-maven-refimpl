package no.knowit.openejb.mock;

import java.util.Properties;

import javax.naming.InitialContext;

import no.knowit.openejb.BootStrapOpenEjb;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Provides Base OpenEJB test functionality for TestNG tests.
 * This is a copy of the <code>org.jboss.seam.mock.SeamTest</code> class.
 * Use the <code>no.knowit.seam.openejb.mock.SeamTest</code> class to run Seam specific tests.
 */
public class OpenEjbTest {
	
	protected static InitialContext initialContext = null;
	protected static Properties contextProperties = BootStrapOpenEjb.getDefaultContextProperties(null);

	
	@BeforeMethod
	public void begin() {
	}

	@AfterMethod
	public void end() {
	}

	@BeforeClass
	public void setupClass() throws Exception {
	}

	@AfterClass
	public void cleanupClass() throws Exception {
	}

	@BeforeSuite
	public void beforeSuite() throws Exception {
		startOpenEjbEmbeddedIfNecessary();
	}

	@AfterSuite
	protected void afterSuite() throws Exception {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}
	
	/**
	 * Start embedded OpenEJB container
	 */
	protected void startOpenEjbEmbeddedIfNecessary() throws Exception {
		if (initialContext == null) {
			initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
		}
	}
	
	/**
	 * 
	 * @param key
	 */
	protected void removeContextProperty(final String key) {
		if (contextProperties.containsKey(key)) {
			contextProperties.remove(key);
		}
	}

	/**
	 * Close the embedded container
	 * If you need the embedded container to restart between different test scenarios, then
	 * you should bootstrap the container with the property 
	 * <code>"openejb.embedded.initialcontext.close=destroy"</code>
	 * see http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html 
	 */
	protected void closeInitialContext() {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}
}
