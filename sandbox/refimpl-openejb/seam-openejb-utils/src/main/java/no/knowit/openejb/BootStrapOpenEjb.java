package no.knowit.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.openejb.OpenEJB;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.loader.SystemInstance;

public class BootStrapOpenEjb {

	private static InitialContext initialContext = null;

	public static InitialContext bootstrap() throws Exception {
		return bootstrap(null);
	}

	/**
	 * 
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	public static InitialContext bootstrap(final Properties properties) throws Exception {

		try {
			// Is OpenEJB is available
			Class.forName("org.apache.openejb.OpenEJB");

			if (initialContext != null) {
				initialContext.close();
			}

			Properties p = new Properties();

			// Set the initial context factory
			p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

			// Corresponds to JBoss JNDI lookup
			p.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}");

			// Overrides default properties in p if key match
			if (properties != null) {
				p.putAll(properties);
			}

			initialContext = new InitialContext(p);
			return initialContext;
		} 
		catch (Exception e) {
			// Since OpenEJB failed to start, we do not have a logger
			System.out.println("\n*******\nOpenEJB bootstrap failed: " + e + "\n*******");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get a set of default propertis to boot the OpenEJB embedded container
	 * Note: This is only a suggestion.  
	 * @param properties
	 * @return
	 */
	public static Properties getDefaultProperties(final Properties properties) {
		Properties p = new Properties();
		
		// see: http://openejb.apache.org/3.0/alternate-descriptors.html
		p.put("openejb.altdd.prefix", "openejb");
		
		// For multi module projects
		p.put("openejb.deployments.classpath.ear", "true");
		

		// Property that ensures that OpenEJB destroys the embedded container when closing the initial context
		// See: http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
		//p.put("openejb.embedded.initialcontext.close", "destroy"); 
		
		if (properties != null) {
			p.putAll(properties);
		}
		
		return p;
	}

	/**
	 * Close the initial context
	 * If the container was started with <code>openejb.embedded.initialcontext.close=close</code> then 
	 * OpenEJB destroys the embedded container when closing the initial context, see:
	 * http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
	 * @return
	 */
	public static InitialContext closeInitialContext() {
		try {
			initialContext.close();
			initialContext = null;
		} 
		catch (Exception e) {
			System.out.println("\n*******\nClosing OpenEJB context failed: " + e + "\n*******");
			throw new RuntimeException(e);
		}
		return null;
	}

	
	/**
	 * Perform a clean shutdown of this embedded instance
	 * This is an alternative to <code>initialContext.close()</code>
	 */
	public static void shutdown() {
		if (OpenEJB.isInitialized()) {
			Assembler assembler = SystemInstance.get().getComponent(Assembler.class);
			try {
				for (AppInfo appInfo : assembler.getDeployedApplications()) {
						assembler.destroyApplication(appInfo.jarPath);
				}
			} 
			catch (Exception e) {
				System.out.println("\n*******\nClosing OpenEJB context failed: " + e + "\n*******");
				throw new RuntimeException(e);
			}
			finally {
				OpenEJB.destroy();
			}
		}
	}

	/**
	 * 
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

}
