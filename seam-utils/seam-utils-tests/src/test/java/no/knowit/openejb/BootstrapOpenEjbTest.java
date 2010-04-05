package no.knowit.openejb;

import java.util.Properties;

import javax.naming.InitialContext;

import no.knowit.openejb.BootStrapOpenEjb;
import no.knowit.openejb.mock.OpenEjbTest;

import org.apache.log4j.Logger;
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
public class BootstrapOpenEjbTest { // extends OpenEjbTest {
  private static final String OPENEJB_EMBEDDED_IC_CLOSE = "openejb.embedded.initialcontext.close";
  
  private static Logger log = Logger.getLogger(OpenEjbTest.class);
  private static Properties contextProperties = new Properties();
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
    Assert.assertTrue(BootStrapOpenEjb.isopenEjbAvailable(), "OpenEJB is not available. Check your classpath");
    Assert.assertFalse(OpenEJB.isInitialized(), "OpenEJB already running");

    contextProperties.put(OPENEJB_EMBEDDED_IC_CLOSE, "destroy");
    contextProperties.put("log4j.category.no.knowit.openejb", "debug");
	}

	@AfterSuite
  public void afterSuite() throws Exception {
	  //BootStrapOpenEjb.shutdown();  // Will make next suite choke
	}
	
	
	@Test(enabled=true)
	public void shouldBootstrapWithDestroy() throws Exception {
	  // We do not have a logger yet
		System.out.println(String.format(
		    "*** Bootstrapping OpenEJB embedded container with property: \"%s=%s\"",
		    OPENEJB_EMBEDDED_IC_CLOSE, contextProperties.get(OPENEJB_EMBEDDED_IC_CLOSE)));
		
		InitialContext initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
		
		Assert.assertNotNull(initialContext, "InitialContext was null after bootstrap.");
    Assert.assertTrue(OpenEJB.isInitialized(), "OpenEJB is not running after bootstrap");
	}
	
	@Test(dependsOnMethods={ "shouldBootstrapWithDestroy" }, enabled=true)
	public void shouldCloseInitialContextAndDestroy() throws Exception {
	  log.debug(String.format("*** Closing initial context with property: \"%s=%s\"", 
	      OPENEJB_EMBEDDED_IC_CLOSE, contextProperties.get(OPENEJB_EMBEDDED_IC_CLOSE)));
	  
	  BootStrapOpenEjb.closeInitialContext();
    Assert.assertFalse(OpenEJB.isInitialized(), "OpenEJB should not be initialized after close");
	}

  @Test(dependsOnMethods={ "shouldCloseInitialContextAndDestroy" }, enabled=true)
  public void shouldBootstrapWithoutDestroy() throws Exception {
    // We do not have a logger yet
    System.out.println(String.format(
        "*** Bootstrapping OpenEJB embedded container without property: \"%s\"", 
        OPENEJB_EMBEDDED_IC_CLOSE));
    
    contextProperties.remove(OPENEJB_EMBEDDED_IC_CLOSE);
    InitialContext initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
    
    Assert.assertNotNull(initialContext, "InitialContext was null after bootstrap.");
    Assert.assertTrue(OpenEJB.isInitialized(), "OpenEJB is not running after bootstrap");
  }
	

  @Test(dependsOnMethods={ "shouldBootstrapWithoutDestroy" }, enabled=true)
  public void shouldCloseInitialContextWithoutDestroy() throws Exception {
    log.debug(String.format(
        "*** Closing initial context without property: \"%s\"", OPENEJB_EMBEDDED_IC_CLOSE));
    
    BootStrapOpenEjb.closeInitialContext();
    Assert.assertTrue(OpenEJB.isInitialized(), 
        "OpenEJB should be initialized after close");
  }
}
