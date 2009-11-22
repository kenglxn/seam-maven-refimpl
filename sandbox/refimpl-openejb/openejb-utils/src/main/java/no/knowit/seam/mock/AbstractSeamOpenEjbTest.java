package no.knowit.seam.mock;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.knowit.openejb.BootStrapOpenEjb;

import org.apache.log4j.Logger;
import org.jboss.seam.mock.AbstractSeamTest;

public class AbstractSeamOpenEjbTest extends AbstractSeamTest {

    private static boolean started = false;

    protected static InitialContext initialContext = null;

    private static InitialContext securedInitialContext = null;
    
  	private Logger log = Logger.getLogger(this.getClass());


  	/**
  	 * Start embedde OpenEJB
  	 */
    @Override
    protected void startJbossEmbeddedIfNecessary()
            throws Exception {

        if (!started && openEjbAvailable()) {
            startOpenEJB();
            log.debug("OpenEJB started");

        }
        started = true;
    }

    protected void startOpenEJB() throws Exception {

			Properties properties = new Properties();
			
			properties.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}");
			
			properties.put("log4j.category.no.knowit.openejb.OpenEjbBootStrap", "debug");
			
			properties.put("log4j.category.org.superbiz", "warn"); 
			properties.put("log4j.category.org.superbiz.calculator.CalculatorTest", "debug"); 
		
    	initialContext = (InitialContext) BootStrapOpenEjb.bootstrap(properties);
    }

    private boolean openEjbAvailable() {

        try {
            Class.forName("org.apache.openejb.OpenEJB");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    protected InitialContext getInitialContext()
            throws NamingException {

        return getStaticInitialContext();
    }

    public static InitialContext getStaticInitialContext()
            throws NamingException {

        if (initialContext == null) {
            loadInitialContext();
        }
        return initialContext;
    }

    /**
     * Return a new InitialContext based on org.jnp.interfaces.LocalOnlyContextFactory,
     * setting the the default context.
     *
     * @return javax.naming.InitialContext
     * @throws Exception ex
     */
    public InitialContext newInitialContext()
            throws Exception {

        Hashtable<String, String> props = getInitialContextProperties();
        initialContext = new InitialContext(props);
        return initialContext;
    }

    /**
     * Return a new InitialContext based on org.jboss.security.jndi.JndiLoginInitialContextFactory,
     * setting the default context. Use the specified username and password to set the security context.
     *
     * @param pPrincipal  principal
     * @param pCredential credential
     * @return javax.naming.InitialContext
     * @throws Exception ex
     */
    public InitialContext newInitialContext(final String pPrincipal,
                                            final String pCredential)
            throws Exception {

        Hashtable<String, String> props =
                getInitialContextProperties(pPrincipal, pCredential);
        securedInitialContext = new InitialContext(props);
        return securedInitialContext;
    }

    /**
     * Return the default InitialContext based on org.jnp.interfaces.LocalOnlyContextFactory
     * if one is already instantiated, otherwise create a new InitialContext and set as the default.
     *
     * @return javax.naming.InitialContext
     * @throws javax.naming.NamingException ex
     */
    public static InitialContext loadInitialContext()
            throws NamingException {

        if (initialContext == null) {
            Hashtable<String, String> props = getInitialContextProperties();
            initialContext = new InitialContext(props);
        }
        return initialContext;
    }

    /**
     * Return the default InitialContext based on org.jboss.security.jndi.JndiLoginInitialContextFactory
     * if one is already instantiated, otherwise create a new InitialContext and set as the default.
     * Use the specified username and password to set the security context.
     *
     * @param pPrincipal  principal
     * @param pCredential credential
     * @return initialcontext
     * @throws javax.naming.NamingException ex
     */
    public InitialContext loadInitialContext(final String pPrincipal,
                                             final String pCredential)
            throws NamingException {

        if (securedInitialContext == null) {
            Hashtable<String, String> props =
                    getInitialContextProperties(pPrincipal, pCredential);
            securedInitialContext = new InitialContext(props);
        }
        return securedInitialContext;
    }

    private static Hashtable<String, String> getInitialContextProperties() {

        Hashtable<String, String> props = new Hashtable<String, String>(1);
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

        return props;
    }

    private Hashtable<String, String> getInitialContextProperties(final String pPrincipal,
                                                                  final String pCredential) {

        Hashtable<String, String> props = getInitialContextProperties();
        props.put(Context.SECURITY_PRINCIPAL, pPrincipal);
        props.put(Context.SECURITY_CREDENTIALS, pCredential);
        return props;
    }
}
