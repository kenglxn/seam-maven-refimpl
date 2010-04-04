package no.knowit.seam.openejb.mock;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.knowit.openejb.BootStrapOpenEjb;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.mock.AbstractSeamTest;
import org.testng.Assert;

/**
 * @author <a href="http://seamframework.org/Community/UsingOpenEJBForIntegrationTesting">
 *    Using OpenEJB for integration testing</a>
 */
public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

  protected static InitialContext initialContext = null;
  protected static Properties contextProperties = new Properties();

  /**
   * Start embedded OpenEJB container
   */
  @Override
  protected void startJbossEmbeddedIfNecessary() throws Exception {
    initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
    // do not call super
  }

  @Override
  protected InitialContext getInitialContext() throws NamingException {
    return initialContext;
    // do not call super
  }

  public static InitialContext getStaticInitialContext() throws NamingException {
    return initialContext;
  }

  protected void removeContextProperty(final String key) {
    if (contextProperties.containsKey(key)) {
      contextProperties.remove(key);
    }
  }

  /**
   * Close the embedded container If you need the embedded container to restart
   * between different test scenarios, then you should bootstrap the container
   * with the property
   * <code>"openejb.embedded.initialcontext.close=destroy"</code> see
   * http://blog
   * .jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
   */
  protected void closeInitialContext() {
    initialContext = BootStrapOpenEjb.closeInitialContext();
  }

  /**
   * Perform a clean shutdown of the embedded container
   * This is an alternative to <code>initialContext.close()</code>
   * @return
   */
  protected void shutdownOpenEJB() {
    initialContext = BootStrapOpenEjb.shutdown();
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
      return (T) instance;
    } catch (Exception e) {
      Assert.fail("Could not lookup Seam component: " + name, e);
    }
    return null;
  }

}
