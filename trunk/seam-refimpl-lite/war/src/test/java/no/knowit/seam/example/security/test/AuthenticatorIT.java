package no.knowit.seam.example.security.test;

import no.knowit.seam.example.test.AbstractUnitilisedJSFUnitTestCase;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.seam.Component;
import org.unitils.dbunit.annotation.DataSet;

import java.io.IOException;

@DataSet
public class AuthenticatorIT extends AbstractUnitilisedJSFUnitTestCase implements AuthenticatorITI {

    private static final String USERNAME_CLIENT_ID = "loginForm:username";
    private static final String PASSWORD_CLIENT_ID = "loginForm:password";
    private static final String SUBMIT_CLIENT_ID = "loginForm:submit";

    public void testFineLogin() throws Throwable {
        run(new TestComponentRunnable() {
            @Override
            public void run() throws Throwable {
                getTestComponent().testFineLogin();
            }
        });
    }

    private AuthenticatorITI getTestComponent() {
        return (AuthenticatorITI) Component.getInstance(AuthenticatorITI.COMPONENT_NAME);
    }

    private void simulateLogin(JSFSession jsfSession, String email, String password) throws IOException {
        JSFClientSession client = jsfSession.getJSFClientSession();
        client.setValue(USERNAME_CLIENT_ID, email);
        client.setValue(PASSWORD_CLIENT_ID, password);
        client.click(SUBMIT_CLIENT_ID);
    }

}