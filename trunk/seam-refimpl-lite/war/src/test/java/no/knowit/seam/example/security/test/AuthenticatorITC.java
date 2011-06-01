package no.knowit.seam.example.security.test;

import junit.framework.Assert;
import no.knowit.seam.example.test.JSFUnitUtils;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.seam.annotations.Name;

import java.io.IOException;

@Name(AuthenticatorITI.COMPONENT_NAME)
public class AuthenticatorITC implements AuthenticatorITI {

    private static final String USERNAME_CLIENT_ID = "loginForm:username";
    private static final String PASSWORD_CLIENT_ID = "loginForm:password";
    private static final String SUBMIT_CLIENT_ID = "loginForm:submit";

    public void testFineLogin() throws Exception {
        JSFSession jsfSession = new JSFSession("/");

        JSFServerSession server = jsfSession.getJSFServerSession();
        JSFClientSession client = jsfSession.getJSFClientSession();

        Assert.assertTrue(!(Boolean) JSFUnitUtils.getELValue(jsfSession, "#{identity.loggedIn}"));

        /** Enter login page */
        client.click("menuLoginId");
        /** Fill the form */
        simulateLogin(jsfSession, "s4237@pjwstk.edu.pl", "aaaaa");

        JSFUnitUtils.logFacesMessages(jsfSession);
        /** Assert propper redirection */
        Assert.assertEquals("/home.xhtml", server.getCurrentViewID());
        /** Assert propper communicate is printed */
        JSFUnitUtils.assertGlobalMessage(jsfSession, "Welcome, s4237@pjwstk.edu.pl!");
        Assert.assertTrue((Boolean) JSFUnitUtils.getELValue(jsfSession, "#{identity.loggedIn}"));
    }

    private void simulateLogin(JSFSession jsfSession, String email, String password) throws IOException {
        JSFClientSession client = jsfSession.getJSFClientSession();
        client.setValue(USERNAME_CLIENT_ID, email);
        client.setValue(PASSWORD_CLIENT_ID, password);
        client.click(SUBMIT_CLIENT_ID);
    }

}