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
public class BootstrapOpenEjbTest { 
  private static final String OPENEJB_EMBEDDED_IC_CLOSE = "openejb.embedded.initialcontext.close";
  
  private static Logger log = Logger.getLogger(OpenEjbTest.class);
  private InitialContext initialContext = null;
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
	  //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
	  
    assert BootStrapOpenEjb.isopenEjbAvailable() : "OpenEJB is not available. Check your classpath";
    assert OpenEJB.isInitialized() == false : "OpenEJB is already running";
	}

	@AfterSuite
  public void afterSuite() throws Exception {
	  //BootStrapOpenEjb.shutdown();  // Will make next test suite choke
	}
	
	
	@Test(enabled=true)
	public void shouldBootstrapWithDestroy() throws Exception {
    Properties environment = new Properties();
    environment.put(OPENEJB_EMBEDDED_IC_CLOSE, "destroy");
    environment.put("log4j.category.no.knowit.openejb", "debug");
    
	  // We do not have a logger yet, so using sysout
		System.out.println(String.format(
		    "*** Bootstrapping OpenEJB embedded container with property: \"%s=%s\"",
		    OPENEJB_EMBEDDED_IC_CLOSE, environment.get(OPENEJB_EMBEDDED_IC_CLOSE)));
		
		initialContext = BootStrapOpenEjb.bootstrap(environment);
		
		Assert.assertNotNull(initialContext, "InitialContext was null after bootstrap.");
    Assert.assertTrue(OpenEJB.isInitialized(), "OpenEJB is not running after bootstrap");
	}
	
  @Test(dependsOnMethods={ "shouldBootstrapWithDestroy" }, enabled=true)
	public void shouldCloseInitialContextAndDestroy() throws Exception {

	  log.debug(String.format("*** Closing initial context with property: \"%s=%s\"", 
	      OPENEJB_EMBEDDED_IC_CLOSE, "destroy"));
	  
	  BootStrapOpenEjb.closeInitialContext(initialContext);
    Assert.assertFalse(OpenEJB.isInitialized(), "OpenEJB should not be initialized after close");
	}

  @Test(dependsOnMethods={ "shouldCloseInitialContextAndDestroy" }, enabled=true)
  public void shouldBootstrapWithoutDestroy() throws Exception {
    // We do not have a logger yet
    System.out.println(String.format(
        "*** Bootstrapping OpenEJB embedded container without property: \"%s\"", 
        OPENEJB_EMBEDDED_IC_CLOSE));
    
    Properties environment = new Properties();
    environment.put("log4j.category.no.knowit.openejb", "debug");
    InitialContext initialContext = BootStrapOpenEjb.bootstrap(environment);
    
    Assert.assertNotNull(initialContext, "InitialContext was null after bootstrap.");
    Assert.assertTrue(OpenEJB.isInitialized(), "OpenEJB is not running after bootstrap");
  }
	

  @Test(dependsOnMethods={ "shouldBootstrapWithoutDestroy" }, enabled=true)
  public void shouldCloseInitialContextWithoutDestroy() throws Exception {
    log.debug(String.format(
        "*** Closing initial context WITHOUT property: \"%s\"", OPENEJB_EMBEDDED_IC_CLOSE));
    
    InitialContext ic = new InitialContext();
    ic.close();
    Assert.assertTrue(OpenEJB.isInitialized(), "OpenEJB should be initialized after close");
  }
}
