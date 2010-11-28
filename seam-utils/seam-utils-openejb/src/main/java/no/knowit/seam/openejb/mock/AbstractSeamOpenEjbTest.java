package no.knowit.seam.openejb.mock;

import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.mock.AbstractSeamTest;

/**
 * @author <a href="http://seamframework.org/Community/UsingOpenEJBForIntegrationTesting">
 *    Using OpenEJB for integration testing</a>
 */
public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

  private static final Logger log = Logger.getLogger(AbstractSeamOpenEjbTest.class);

  protected static EJBContainer container;
  protected static final String JNDI_PATTERN = "%s/Local";
  protected static Properties contextProperties = new Properties();
  
  /**
   * Start embedded OpenEJB container
   */
  @Override
  protected void startJbossEmbeddedIfNecessary() throws Exception {
    // Start OpenEJB embedded container. Do not call super!!
    container = EJBContainer.createEJBContainer(contextProperties);
  }

  @Override
  protected InitialContext getInitialContext() throws NamingException {
    // do not call super!!
    return (InitialContext)container.getContext();
  }

  @SuppressWarnings("unchecked")
  protected <T> T doJndiLookup(final String name) {
    try {
      InitialContext initialContext = getInitialContext();
      Object instance = initialContext.lookup(String.format(JNDI_PATTERN, name));
      if(instance == null) {
        throw new IllegalArgumentException(
            String.format("InitialContext.lookup(%s): returned null", name));
      }
      return (T)instance;
    } 
    catch (NamingException e) {
      log.error("JNDI lookup failed. Reason: " + e);
      throw new IllegalArgumentException("JNDI lookup failed. Reason: " + e, e);
    }
  }

  /**
   * Type safe version of Component.getInstance(name)
   * 
   * @param <T>
   * @param name
   * @return
   */
  @SuppressWarnings("unchecked")
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
  @SuppressWarnings("unchecked")
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
  @SuppressWarnings("unchecked")
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
  @SuppressWarnings("unchecked")
  protected static <T> T getComponentInstanceWithAsserts(final String name, Class<?> clazz) {
    try {
      Object instance = Component.getInstance(name);
      if(instance == null) {
        throw new IllegalArgumentException(
            String.format("Component.getInstance(%s): returned null", name));
      }
      
      if(!(instance.getClass() instanceof Class)) {
        throw new IllegalArgumentException(
            String.format("Component.getInstance(%s): returned incorrect type", name));
      }
      
      return (T)instance;
    } 
    catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Could not lookup Seam component: %s", name), e);

    }
  }
}
