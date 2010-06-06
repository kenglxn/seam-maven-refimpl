package no.knowit.openejb;

import java.util.Properties;

import javax.naming.InitialContext;

import org.apache.openejb.OpenEJB;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * @author LeifOO
 *
 */
public class BootstrapOpenEjbTest { 
  private InitialContext initialContext = null;
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
    assert BootStrapOpenEjb.isopenEjbAvailable() : "OpenEJB is not available. Check your classpath";
    assert OpenEJB.isInitialized() == false : "OpenEJB is already running";
	}

	/**
	 * Bootstrap the OpenEJB embedded container with property 
	 * <code>openejb.embedded.initialcontext.close=DESTROY</code> which makes it possible to restart
	 * the server between test scenarios. See: 
	 * <a href="http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html">
	 * Restarting the embedded OpenEJB container between each test</a>
	 * @throws Exception
	 */
	@Test
	public void shouldBootstrapAndDestroyContainer() throws Exception {
    Properties environment = new Properties();
    environment.put(BootStrapOpenEjb.OPENEJB_EMBEDDED_IC_CLOSE, "DESTROY");
    environment.put("log4j.category.no.knowit.openejb", "debug");
    
	  // We do not have a logger yet, using sysout
		System.out.println(String.format(
		    "*** Bootstrapping OpenEJB embedded container with property: \"%s=%s\"",
		    BootStrapOpenEjb.OPENEJB_EMBEDDED_IC_CLOSE, 
		    environment.get(BootStrapOpenEjb.OPENEJB_EMBEDDED_IC_CLOSE)));
		
		initialContext = BootStrapOpenEjb.bootstrap(environment);
		
		Assert.assertNotNull(initialContext, "InitialContext was null after bootstrap.");
    Assert.assertTrue(OpenEJB.isInitialized(), "OpenEJB is not running after bootstrap");
    
    BootStrapOpenEjb.closeInitialContext();
    Assert.assertFalse(OpenEJB.isInitialized(), "OpenEJB should not be initialized after close");
	}
}
