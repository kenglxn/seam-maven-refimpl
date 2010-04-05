package no.knowit.openejb;

import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.openejb.OpenEJB;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.loader.SystemInstance;


/**
 * @author <a href="http://seamframework.org/Community/UsingOpenEJBForIntegrationTesting">Using OpenEJB for integration testing</a>
 * @author LeifOO
 */
public class BootStrapOpenEjb {

  //private static final String OPENEJB_EMBEDDED_IC_CLOSE = "openejb.embedded.initialcontext.close";
  //private static final String OPENEJB_EMBEDDED_IC_CLOSE_DESTROY = "destroy";
  //private static final String OPENEJB_EMBEDDED_IC_CLOSE_CLOSE = "close";

  private static InitialContext context = null;

	/**
   * Bootstrap the OpenEJB embedded container
   * @return the initial context
	 * @throws Exception
	 */
	public static InitialContext bootstrap() throws Exception {
		return bootstrap(null);
	}

	/**
	 * Bootstrap the OpenEJB embedded container
	 * @param properties See: <a href="http://openejb.apache.org/3.0/embedded-configuration.html">
	 *   OpenEJB: Embedded Configuration </a>
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
        context = new InitialContext(p);
			}
			else {
        context = new InitialContext(); // Properties have no effect if OpenEJB is already running
			}
			return context;
		} 
		catch (Exception e) {
			// Since OpenEJB failed to start, we do not have a logger
			System.out.println("\n*******\nOpenEJB bootstrap failed: " + e + "\n*******");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Close the initial context
	 * If the container was started with <code>openejb.embedded.initialcontext.close=destroy</code> then 
	 * OpenEJB destroys the embedded container when closing the initial context, see:
	 * http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
	 * @return
	 */
	public static InitialContext closeInitialContext() {
		if(context != null) {
	    try {
        context.close();
        context = null;
	    } 
	    catch (Exception e) {
	      System.out.println("\n*******\nClosing OpenEJB context failed: " + e + "\n*******");
	      throw new RuntimeException(e);
	    }
		}
		return context;
	}

	/**
	 * Get initial context
	 * @return The InitialContext
	 */
	public static InitialContext getInitialContext() {
	  
    if (!OpenEJB.isInitialized()) {
      throw new IllegalStateException("Could not obtain OpenEJB InitialContext," +
      		" OpenEJB is not initialized");
    }
    if (context == null) {
      try {
        context = new InitialContext();
      } catch (Exception e) {
        throw new IllegalStateException("Could not obtain OpenEJB InitialContext", e);
      }
    }
	  return context;
	}
	
  /**
   * Checks if OpenEJB is in class path
   * @return <code>true</code> if OpenEJB is available in classpath
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
   * see: http://openejb.apache.org/faq.html
   * </p>
   * <p><b>Note</b>: Shuts down the server is OK, but I'm not able to restart server afterwards
   * Get a NullPointerException: 
   *    <tt>Cannot instantiate a LocalInitialContext. 
   *    Exception: java.lang.NullPointerException null</tt>.<br/>
   * Must be a bug somwhere.
   * Use Shurefire Fork instead, as mentioned here: 
   *    https://blogs.apache.org/openejb/entry/user_blog_restarting_the_embedded
   * </p>
   * @return
   */
  public static InitialContext shutdown() {
    if (OpenEJB.isInitialized()) {
      Assembler assembler = SystemInstance.get().getComponent(Assembler.class);
      try {
        for (AppInfo appInfo : assembler.getDeployedApplications()) {
            assembler.destroyApplication(appInfo.jarPath);
        }
        OpenEJB.destroy();
      } 
      catch (Exception e) {
        System.out.println("\n*******\nClosing OpenEJB context failed: " + e + "\n*******");
        throw new RuntimeException(e);
      }
    }
    context = null;
    return null;
  }
}
