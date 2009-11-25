package no.knowit.seam.openejb.mock;

import java.util.Properties;

import javax.naming.InitialContext;

import no.knowit.openejb.BootStrapOpenEjb;

import org.jboss.seam.mock.AbstractSeamTest;

public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

	protected static InitialContext initialContext = null;
	protected static Properties contextProperties = BootStrapOpenEjb.getDefaultContextProperties(null);

	/**
	 * Start embedded OpenEJB container
	 */
	@Override
	protected void startJbossEmbeddedIfNecessary() throws Exception {
		if (initialContext == null) {
			initialContext = BootStrapOpenEjb.bootstrap(contextProperties);
		}
	}

	/**
	 * 
	 * @param key
	 */
	protected void removeContextProperty(final String key) {
		if (contextProperties.containsKey(key)) {
			contextProperties.remove(key);
		}
	}

	/**
	 * Close the embedded container
	 * If you need the embedded container to restart between different test scenarios, then
	 * you should bootstrap the container with the property 
	 * <code>"openejb.embedded.initialcontext.close=destroy"</code>
	 * see http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html 
	 */
	protected void closeInitialContext() {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}
}
