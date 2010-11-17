package no.knowit.openejb;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.ejb.stateless.HelloService;

import org.testng.annotations.Test;

/**
 * Uses the {@link OpenEjbTest} class as a base class for tests. The {@link OpenEjbTest} class
 * bootstraps the embedded container.
 */
public class HelloStatelessEjbTest extends OpenEjbTest {
  private static final String EXPECTED_GREET = "Hello 2 you";
  
  @Test
  public void shouldSayHello2You() throws Exception {
    HelloService helloService = doJndiLookup("HelloService");
    assert helloService.greet().compareTo(EXPECTED_GREET) == 0 : "Expected: " + EXPECTED_GREET; 
  }
}
