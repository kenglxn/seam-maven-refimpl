package no.knowit.openejb.mock;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.knowit.openejb.BootStrapOpenEjb;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Provides Base OpenEJB test functionality for TestNG tests. This is a copy of
 * the <code>org.jboss.seam.mock.SeamTest</code> class.<br/>
 * Use the <code>no.knowit.seam.openejb.mock.SeamOpenEjbTest</code> class to run
 * Seam specific tests in OpenEJB.
 */
public class OpenEjbTest {
  protected static final String JNDI_PATTERN = "%s/Local";

  protected static Logger log = Logger.getLogger(OpenEjbTest.class);
  protected static Properties contextProperties = new Properties();

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
    closeInitialContext();
  }

  /**
   * Start embedded OpenEJB container
   */
  protected void startOpenEjbEmbeddedIfNecessary() throws Exception {
    BootStrapOpenEjb.bootstrap(contextProperties);
  }

  protected InitialContext getInitialContext() {
    return BootStrapOpenEjb.getInitialContext();
  }

  /**
   * Close the embedded container. If you need the embedded container to restart
   * between different test scenarios, then you should bootstrap the container
   * with the property <code>"openejb.embedded.initialcontext.close=DESTROY"</code>, see
   * http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
   */
  protected void closeInitialContext() {
    BootStrapOpenEjb.closeInitialContext();
  }

  
  @SuppressWarnings("unchecked")
  protected <T> T doJndiLookup(final String name) throws Exception {
    try {
      InitialContext initialContext = getInitialContext();
      Object instance = initialContext.lookup(String.format(JNDI_PATTERN, name));
      Assert.assertNotNull(instance, String.format("InitialContext.lookup(\"%s\"): returned null", name));
      return (T) instance;
    } 
    catch (NamingException e) {
      log.error("JNDI lookup failed.", e);
      throw (e);
    }
  }

  /*
   * TODO: tbd/tbt
   */
  
//  @SuppressWarnings("unchecked")
//  protected <T> T lookup(final Class<?> clazz) throws Exception {
//
//    String name = null;
//    Stateless slsb = clazz.getAnnotation(Stateless.class);
//    if (slsb != null) {
//      name = slsb.name();
//    }
//    if (name == null) {
//      Stateful sfsb = clazz.getAnnotation(Stateful.class);
//      name = sfsb.name();
//    }
//    if (name == null || name.length() < 1) {
//      name = clazz.getSimpleName();
//    }
//    try {
//      T instance = (T) initialContext.lookup(String.format(JNDI_PATTERN, name));
//      Assert.assertNotNull(instance, String
//          .format("initialContext.lookup(%s): returned null", name));
//      return instance;
//    } 
//    catch (NamingException e) {
//      log.error(e);
//      throw (e);
//    }
//  }
}
