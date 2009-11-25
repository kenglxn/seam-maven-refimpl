package no.knowit.seam.openejb.mock;

import java.util.Properties;

import javax.naming.InitialContext;

import no.knowit.openejb.BootStrapOpenEjb;

import org.jboss.seam.mock.AbstractSeamTest;

public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

	protected static InitialContext initialContext = null;
	protected static Properties contextProperties = BootStrapOpenEjb.getDefaultProperties(null);

	/**
	 * Start embedded OpenEJB container
	 */
	@Override
	protected void startJbossEmbeddedIfNecessary() throws Exception {

		System.out.println("AbstractSeamOpenEjbTest.startJbossEmbeddedIfNecessary");

		if (initialContext == null) {
			initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
		}
	}

	/**
	 * 
	 * @param properties
	 */
	protected void addContextProperties(Properties properties) {
		if (properties != null) {
			contextProperties.putAll(properties);
		}
	}

	/**
	 * 
	 * @param key
	 */
	protected void removeProperty(final String key) {
		if (contextProperties.containsKey(key)) {
			contextProperties.remove(key);
		}
	}

	/**
	 * Close the micro container
	 * If you need the micro container restart between different test scenarios, then
	 * you should bootstrap the container with the property 
	 * <code>"openejb.embedded.initialcontext.close=destroy"</code>
	 * see http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html 
	 */
	protected void closeInitialContext() {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}
}
