package no.knowit.openejb;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import no.knowit.testsupport.ejb.stateless.HelloService;

import org.apache.openejb.api.LocalClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Uses Local client injection as described in
 * <a href="http://openejb.apache.org/3.0/local-client-injection.html">OpenEJB: Local Client Injection</a>
 * Indirectly tests that a service is injected into another service. The 
 * <code<>HelloService</code> injects the <code>CalculatorService</code> using
 * the <code>@EJB</code> annotation.
 */
@LocalClient
public class HelloStatelessEjbLocalClientInjectionTest {

  private static final String EXPECTED_GREET = "Hello 2 you";
  
  private EJBContainer container;
  
  @EJB
  private HelloService helloService;
  
  @BeforeSuite
  public void before() {
    // Bootstrap
    container = EJBContainer.createEJBContainer();
    
  }
  
  @BeforeClass
  public void setup() throws Exception {
    // Inject client
    container.getContext().bind("inject", this);
  }
  
  @Test
  public void shouldSayHello2You() throws Exception {
    assert helloService.greet().compareTo(EXPECTED_GREET) == 0 : "Expected: " + EXPECTED_GREET; 
  }
}
