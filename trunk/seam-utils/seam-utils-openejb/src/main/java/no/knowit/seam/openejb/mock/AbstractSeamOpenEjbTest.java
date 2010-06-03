package no.knowit.seam.openejb.mock;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.knowit.openejb.BootStrapOpenEjb;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.mock.AbstractSeamTest;
import org.testng.Assert;

/**
 * @author <a href="http://seamframework.org/Community/UsingOpenEJBForIntegrationTesting">
 *    Using OpenEJB for integration testing</a>
 */
public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

  protected static final String JNDI_PATTERN = "%s/Local";
  
  protected static Logger log = Logger.getLogger(AbstractSeamOpenEjbTest.class);
  protected static Properties contextProperties = new Properties();
  protected static InitialContext initialContext = null;

  /**
   * Start embedded OpenEJB container
   */
  @Override
  protected void startJbossEmbeddedIfNecessary() throws Exception {
    initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
    // do not call super!!
  }

  @Override
  protected InitialContext getInitialContext() throws NamingException {
    if(initialContext == null) {
      initialContext = BootStrapOpenEjb.getInitialContext();
    }
    return initialContext;
    // do not call super
  }

  /**
   * Close the embedded container If you need the embedded container to restart
   * between different test scenarios, then you should bootstrap the container
   * with the property <code>"openejb.embedded.initialcontext.close=destroy"</code>, see
   * <a href="http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html">
   *  http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
   * </a>
   */
  protected void closeInitialContext() {
    BootStrapOpenEjb.closeInitialContext(initialContext);
  }

  protected <T> T doJndiLookup(final String name) throws Exception {
    try {
      if(initialContext == null) {
        initialContext = BootStrapOpenEjb.getInitialContext();
      }
      Object instance = initialContext.lookup(String.format(JNDI_PATTERN, name));
      Assert.assertNotNull(instance, String.format("InitialContext.lookup(%s): returned null", name));
      return (T)instance;
    } 
    catch (NamingException e) {
      log.error(e);
      throw (e);
    }
  }

  /**
   * Type safe version of Component.getInstance(name)
   * 
   * @param <T>
   * @param name
   * @return
   */
  protected static <T> T getComponentInstance(final String name) {
    return (T) Component.getInstance(name);
  }

  /**
   * Type safe version of Component.getInstance(class)
   * 
   * @param <T>
   * @param clazz
   * @return
   */
  protected static <T> T getComponentInstance(final Class<T> clazz) {
    return (T) Component.getInstance(clazz);
  }

  /**
   * Type safe version of Component.getInstance(name, scope)
   * 
   * @param <T>
   * @param name
   * @param scope
   * @return
   */
  protected static <T> T getComponentInstance(final String name, final ScopeType scope) {
    return (T) Component.getInstance(name, scope);
  }

  /**
   * 
   * @param <T>
   * @param name
   * @param clazz
   * @return
   * @throws Exception
   */
  protected static <T> T getComponentInstanceWithAsserts(final String name, Class<?> clazz)
      throws Exception {
    try {
      Object instance = Component.getInstance(name);
      Assert.assertNotNull(instance, "Component.getInstance(\"" + name + "\") returned null");
      Assert.assertTrue(instance.getClass() instanceof Class,
          "Component.getInstance(\"name\") returned incorrect type");
      return (T)instance;
    } 
    catch (Exception e) {
      Assert.fail("Could not lookup Seam component: " + name, e);
    }
    return null;
  }

}
