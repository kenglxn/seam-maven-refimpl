package no.knowit.example.security.action;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;


public class SimpleTest {
    
    @Test
    public void hello() throws NamingException {
        EJBContainer ejbC = EJBContainer.createEJBContainer();
        Context ctx = ejbC.getContext();

        SimpleService simpleService = (SimpleService) ctx.lookup("java:global/classes/Authenticator");
        assertNotNull(simpleService);

        String me = "Ken";

        String hello = simpleService.sayHello(me);
        assertEquals("hello " + me, hello);

        ejbC.close();
    }
}
