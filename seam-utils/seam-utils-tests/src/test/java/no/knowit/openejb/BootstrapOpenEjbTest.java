package no.knowit.openejb;

import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;

import org.apache.openejb.OpenEJB;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class BootstrapOpenEjbTest {
  
  private static final String CONTEXT_CLOSE = "openejb.embedded.initialcontext.close";
  private static final String DESTROY = "DESTROY";
  
	@BeforeSuite
	public void beforeSuite() {
    assert OpenEJB.isInitialized() == false : "OpenEJB container is already running";
	}

	@Test
	public void shouldBootstrapAndDestroyContainer() {

    // Bootstrap the OpenEJB embedded container with property 
    // <code>openejb.embedded.initialcontext.close=DESTROY</code> which makes it possible to 
    // restart the server between test scenarios. 
    // see: http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
    // see: http://openejb.apache.org/faq.html
    Properties p = new Properties();
    p.put(CONTEXT_CLOSE, DESTROY);
    
	  // Boot
	  EJBContainer container = EJBContainer.createEJBContainer(p);
    assert OpenEJB.isInitialized() : "OpenEJB container did not boot";

    // Should boot only once
    assert container == EJBContainer.createEJBContainer() : "OpenEJB container Should boot only once";
    
    // Close and destroy
    container.close();
    assert !OpenEJB.isInitialized() : "OpenEJB did not close";
	}
}
