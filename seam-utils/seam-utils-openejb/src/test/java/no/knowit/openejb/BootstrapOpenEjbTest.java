package no.knowit.openejb;

import no.knowit.openejb.BootStrapOpenEjb;
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
    contextProperties.put("log4j.category.no.knowit.openejb", "debug");
    
		// Normally we would have called super.beforeSuite() to bootstrap container, but 
		// in this test case we want to verify that we're able to bootstrap the container
	}

	@Override
	@AfterSuite
	protected void afterSuite() throws Exception {
		// Normally we would have called super.afterSuite() to shutdown container, but 
    // in this test case we want to verify that we're able to shutdown the container
	}
	
	@Test(groups={ "openejb", "unit-test" }, enabled=true)
	public void bootstrapOpenEjb() throws Exception {
		Assert.assertTrue(BootStrapOpenEjb.isopenEjbAvailable());
		
		initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
		Assert.assertNotNull(initialContext);
	}
	
	@Test(groups={ "openejb", "unit-test" }, dependsOnMethods={ "bootstrapOpenEjb" }, enabled=true)
	public void closeOpenEjbInitialContext() throws Exception {
		initialContext = BootStrapOpenEjb.closeInitialContext();
		Assert.assertNull(initialContext);
	}
	
	@Test(groups={ "openejb", "unit-test" }, dependsOnMethods={ "closeOpenEjbInitialContext" }, enabled=true)
	public void shutdownopenEjb() throws Exception {
		initialContext = BootStrapOpenEjb.bootstrap(null);
		initialContext = BootStrapOpenEjb.shutdown();
		Assert.assertFalse(OpenEJB.isInitialized());
	}
}
