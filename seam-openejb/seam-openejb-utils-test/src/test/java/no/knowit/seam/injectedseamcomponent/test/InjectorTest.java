package no.knowit.seam.injectedseamcomponent.test;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.injectedseamcomponent.Injector;
import no.knowit.seam.openejb.mock.SeamTest;

public class InjectorTest extends SeamTest {
	
  private static final LogProvider log = Logging.getLogProvider(InjectorTest.class);

	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		 contextProperties.put("log4j.category.no.knowit.seam.injectedseamcomponent", "DEBUG");
		 super.beforeSuite();
	}

	@Test(groups={ "seam", "unit-test" })
	public void sayHelloInjectedSeamComponent() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				Injector injector = getComponentInstanceWithAsserts("injector", Injector.class);
				
				Assert.assertNotNull(injector.getInjectedSeamComponent(), "The injected Seam component was NULL!");
				Assert.assertEquals(injector.getInjectedSeamComponent().sayHello(), "Hello Seam");
				
				Assert.assertNotNull(injector.getNoInterfaceInjectedSeamComponent(), "The injected Seam component was NULL!");
				Assert.assertEquals(injector.getNoInterfaceInjectedSeamComponent().sayHello(), "Hello Seam - No Interface");
				
				log.debug("*** The @In(jected) Seam components says Hello :-)");
			}
		}.run();
	}
}
