package no.knowit.openejb;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.ejb.stateless.HelloService;

import org.apache.openejb.api.LocalClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
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
public class HelloStatelessEjbTest extends OpenEjbTest {
  private static final String EXPECTED_GREET = "Hello 2 you";
  
  @EJB
  private HelloService helloService;
  
  @Override
  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.openejb", "debug");
    contextProperties.put("log4j.category.no.knowit.testsupport", "debug");
    super.beforeSuite();
  }
  
  @BeforeClass
  public void setup() throws Exception {
    // Inject client
    getInitialContext().bind("inject", this);
  }

  @Test
  public void shouldSayHello2You() throws Exception {
    assert helloService.greet().compareTo(EXPECTED_GREET) == 0 : "Expected: " + EXPECTED_GREET; 
  }
}
