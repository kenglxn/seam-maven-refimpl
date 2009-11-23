package no.knowit.seam.mock;

import java.util.Properties;

import javax.naming.InitialContext;

import no.knowit.openejb.BootStrapOpenEjb;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
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
	 * 
	 */
	protected void closeInitialContext() {
		initialContext = BootStrapOpenEjb.closeInitialContext();
	}

	protected static <T> T getContextComponent(final Class<T> pClass) {

		return (T) Component.getInstance(pClass);
	}

	protected static <T> T getContextComponent(final String pName) {

		return (T) Component.getInstance(pName);
	}

	protected static <T> T getContextComponent(final String pName, final ScopeType pScope) {

		return (T) Component.getInstance(pName, pScope);
	}

}
