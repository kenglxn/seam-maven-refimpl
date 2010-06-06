package no.knowit.testsupport.ejb.stateless;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.openejb.api.LocalClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests Local client injection as described in
 * <a href="http://openejb.apache.org/3.0/local-client-injection.html">OpenEJB: Local Client Injection</a>
 * Indirectly tests that a service is injected into another service. The 
 * <code<>HelloService</code> injects the <code>CalculatorService</code> using
 * the <code>@EJB</code> annotation.
 * @author LeifOO
 *
 */
@LocalClient
public class HelloStatelessEjbTest {
  private static final String EXPECTED_GREET = "Hello 2 you";
  
  @EJB
  private HelloService helloService;
  
  private InitialContext initialContext;
  
  @BeforeClass
  public void setup() throws Exception {
    Properties p = new Properties();
    p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
    p.put("openejb.embedded.initialcontext.close", "DESTROY");

    initialContext = new InitialContext(p);
    initialContext.bind("inject", this);
  }

  @AfterClass
  protected void tearDown() throws Exception {
    initialContext.close();
  }

  @Test
  public void shouldSayHello2You() throws Exception {
    assert helloService.greet().compareTo(EXPECTED_GREET) == 0 : "Expected: " + EXPECTED_GREET; 
  }
}