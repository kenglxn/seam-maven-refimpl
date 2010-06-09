package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.openejb.OpenEJB;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.loader.SystemInstance;


/**
 * @author <a href="http://seamframework.org/Community/UsingOpenEJBForIntegrationTesting">
 *            Using OpenEJB for integration testing</a>
 * @author LeifOO
 */
public class BootStrapOpenEjb {
  public static final String OPENEJB_EMBEDDED_IC_CLOSE = "openejb.embedded.initialcontext.close";
  public static final String DESTROY = "DESTROY";
  public static final String CLOSE = "CLOSE";
  
  /**
   * We need to keep a reference to the opened initial context if we open the 
   * context with the property openejb.embedded.initialcontext.close = DESTROY. 
   * IntilalContext.close will not shut down the container unless we're calling 
   * the close method on the same instance as the instance created with the
   * openejb.embedded.initialcontext.close property.  
   */
  private static InitialContext initialContext = null;

  
  /**
   * Bootstrap the OpenEJB embedded container
   * @param properties See: 
   *   <a href="http://openejb.apache.org/3.0/embedded-configuration.html">
   *     OpenEJB: Embedded Configuration 
   *   </a>
   * @return the initial context
   * @throws Exception
   */
  public static InitialContext bootstrap(final Properties properties) throws Exception {
    
    if(!isopenEjbAvailable()) {
      throw new IllegalStateException("OpenEJB is not available. Check your classpath!");
    }

    try {
      if (!OpenEJB.isInitialized()) {
        Properties p = new Properties();
        
        // Set the initial context factory
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
        
        // Overrides default properties in p if key match
        if (properties != null) {
          p.putAll(properties);
        }
        initialContext = new InitialContext(p);
        return initialContext;
      }
      else {
        // Properties have no effect if OpenEJB is already running
        if(initialContext == null) {
          initialContext = new InitialContext();
        }
        return initialContext; 
      }
    } catch (Exception e) {
      throw new RuntimeException("Could not bootstrap OpenEJB.", e);
    }
  }
  
  /**
   * Get initial context.  
   * @throws IllegalStateException if the OpenEJB embedded container is not initialized.
   * @return The InitialContext
   */
  public static InitialContext getInitialContext() {
    if (!OpenEJB.isInitialized()) {
      throw new IllegalStateException(
          "Could not obtain initial context: OpenEJB is not initialized.");
    }
    if(initialContext == null) {
      try {
        initialContext = new InitialContext();
      } catch (Exception e) {
        throw new RuntimeException("Could not obtain OpenEJB initial context.", e);
      }
    }
    return initialContext;
  }

	/**
	 * Close the initial context.
	 * If the container was started with the property 
	 * <code>openejb.embedded.initialcontext.close=DESTROY</code> then 
	 * OpenEJB destroys the embedded container when closing the initial context, see:
	 * <ul>
	 * <li>
	 *   <a href="http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html">
	 *   Restarting the embedded OpenEJB container between each test</a>
	 * </li>
	 * <li>
	 *   <a href="http://openejb.apache.org/faq.html">http://openejb.apache.org/faq.html</a>
	 * </li>
	 * </ul>
	 * 
	 * @return
	 */
	public static void closeInitialContext() {
		if(initialContext != null) {
	    try {
        initialContext.close();
        initialContext = null;
	    } 
	    catch (Exception e) {
	      throw new RuntimeException("Closing OpenEJB initial context failed.", e);
	    }
		}
	}

  /**
   * Checks if OpenEJB is in class path
   * @return <code>true</code> if OpenEJB is available in class path
   */
  public static Boolean isopenEjbAvailable() {
    try {
      Class.forName("org.apache.openejb.OpenEJB");
    } 
    catch (ClassNotFoundException e) {
      return false;
    }
    return true;
  }

  /**
   * <p>Perform a clean shutdown of this embedded instance
   * This is an alternative to <code>initialContext.close()</code> with the property
   * <tt>openejb.embedded.initialcontext.close=destroy</tt>
   * see: <a href="http://openejb.apache.org/faq.html">http://openejb.apache.org/faq.html</a>
   * </p>
   * <p><b>Note</b>: Shut down works as expected, but I'm not able to restart server afterwards. 
   * Get a NullPointerException: "<tt>Cannot instantiate a LocalInitialContext. 
   * Exception: java.lang.NullPointerException null</tt>".<br/>
   * Must be a bug somewhere. As an alternative use Surefire Forking to achieve a similar result that 
   * will also clear out any embedded database state as well, as mentioned here: 
   * <a href="https://blogs.apache.org/openejb/entry/user_blog_restarting_the_embedded">
   * User Blog: Restarting the embedded OpenEJB container between each test</a>
   * </p>
   */
  public static void shutdown() {
    if (OpenEJB.isInitialized()) {
      Assembler assembler = SystemInstance.get().getComponent(Assembler.class);
      try {
        for (AppInfo appInfo : assembler.getDeployedApplications()) {
          assembler.destroyApplication(appInfo.jarPath);
        }
        OpenEJB.destroy();
        initialContext = null;
      } 
      catch (Exception e) {
        throw new RuntimeException("OpenEJB shutdown failed.", e);
      }
    }
  }
}
