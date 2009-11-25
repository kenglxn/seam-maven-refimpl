package no.knowit.openejb;

import no.knowit.openejb.mock.ContextPropertiesForTest;
import no.knowit.openejb.mock.OpenEjbTest;

import org.apache.openejb.OpenEJB;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * 
 * @author LeifOO
 *
 */
public class BootstrapOpenEjbTest extends OpenEjbTest {
	
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		 
		// Normally we would have called super.beforeSuite() to bootstrap container, but here
		// one of the tests is to actually verify that we're able to bootstrap the container
	}

	@Override
	@AfterSuite
	protected void afterSuite() throws Exception {
		// Normally we would have called super.afterSuite() to shutdown container, but here
		// one of the tests is to actually verify that we're able to shutdown the container
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shallBootstrapOpenEjb() throws Exception {
		Assert.assertTrue(BootStrapOpenEjb.isopenEjbAvailable());
		
		initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
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
	public void shallShutdownContainer() throws Exception {
		initialContext = BootStrapOpenEjb.bootstrap(null);
		initialContext = BootStrapOpenEjb.shutdown();
		Assert.assertFalse(OpenEJB.isInitialized());
	}
}
