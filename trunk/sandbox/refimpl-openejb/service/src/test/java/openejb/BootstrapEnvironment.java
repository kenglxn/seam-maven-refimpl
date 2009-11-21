package openejb;

import no.knowit.openejb.OpenEjbBootStrap;

import org.jboss.seam.mock.AbstractSeamTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;

import javax.servlet.ServletContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;

public class BootstrapEnvironment
        extends AbstractSeamTest {

    private static boolean started = false;

    private ServletContext servletContext;

    private static InitialContext initialContext = null;

    private static InitialContext securedInitialContext = null;

    /**
     * ********* Setup ***********
     */
    @BeforeSuite
    // needs a WEB-INF/web.xml in resources!!!
    public void beforeSuite() throws Exception {
        startSeam();
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        setupClass();
    }

    @BeforeMethod
    @Override
    public void begin() {
        super.begin();
        //TestLifecycle.beginTest (servletContext, new ServletSessionMap (getSession ()));
    }

    @AfterMethod
    @Override
    public void end() {
        //TestLifecycle.endTest ();
        super.end();
    }

    @AfterClass
    public void afterClass() throws Exception {
        cleanupClass();
    }

    @AfterSuite
    public void afterSuite() throws Exception {
        stopSeam();
    }

    /**
     * ********* Methods ***********
     */

    @Override
    protected void startJbossEmbeddedIfNecessary()
            throws Exception {

        if (!started && openEjbAvailable()) {
            startOpenEJB();
        }
        started = true;
    }

    protected void startOpenEJB() throws Exception {

        OpenEjbBootStrap.bootstrap();
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
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.openejb.client.LocalInitialContextFactory");

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
