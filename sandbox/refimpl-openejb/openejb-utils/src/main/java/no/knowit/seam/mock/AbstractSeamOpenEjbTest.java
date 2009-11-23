package no.knowit.seam.mock;

import java.util.Properties;

import javax.naming.InitialContext;

import no.knowit.openejb.BootStrapOpenEjb;

import org.jboss.seam.mock.AbstractSeamTest;

public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

	protected static InitialContext initialContext = null;
	protected static Properties contextProperties = new Properties();  

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
		if(properties != null) {
			contextProperties.putAll(properties);
		}
	}
	
	/**
	 * 
	 * @param key
	 */
	protected void removeProperty(final String key) {
		if(contextProperties.containsKey(key)) {
			contextProperties.remove(key);
		}
	}
	
	/**
	 * 
	 */
	protected void closeInitialContext() {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}
}
