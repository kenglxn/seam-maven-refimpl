package no.knowit.openejb.mock;

import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Provides Base EJB test functionality for TestNG integration tests running in the Apache
 * OpenEJB embedded container. Use the {@link no.knowit.seam.openejb.mock.SeamOpenEjbTest} class 
 * to run Seam specific tests in OpenEJB.
 */
public class OpenEjbTest {

  private static final Logger log = Logger.getLogger(OpenEjbTest.class);
  
  protected static final String JNDI_PATTERN = "%s/Local";
  protected static Properties contextProperties = new Properties();
  protected static EJBContainer container;

  
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
    // Start OpenEJB embedded container
    container = EJBContainer.createEJBContainer(contextProperties);
  }

  @AfterSuite
  protected void afterSuite() throws Exception {
    // Do we need this??
    //container.close();
  }

  protected InitialContext getInitialContext() throws NamingException {
    return (InitialContext)container.getContext();
  }
  
  @SuppressWarnings("unchecked")
  protected <T> T doJndiLookup(final String name) {
    try {
      Context context = container.getContext();
      Object instance = context.lookup(String.format(JNDI_PATTERN, name));
      if(instance == null) {
        throw new IllegalArgumentException(
            String.format("InitialContext.lookup(%s): returned null", name));
      }
      return (T) instance;
    } 
    catch (NamingException e) {
      log.error("JNDI lookup failed. Reason: " + e);
      throw new IllegalArgumentException("JNDI lookup failed. Reason: " + e, e);
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
