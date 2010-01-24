package no.knowit.openejbtest.seam.hello;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import no.knowit.openejbtest.seam.hello.HelloSeam;
import no.knowit.openejbtest.seam.hello.HelloSeamNoInterface;
import no.knowit.seam.openejb.mock.SeamOpenEjbTest;

public class HelloSeamTest extends SeamOpenEjbTest {
	
  private static final LogProvider log = Logging.getLogProvider(HelloSeamTest.class);

	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		//contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		//contextProperties.put("openejb.deployments.classpath.ear", "true");
		contextProperties.put("log4j.category.no.knowit.seam.hello", "debug");
		super.beforeSuite();
	}

	@Test(groups={ "seam", "unit-test" })
	public void shouldLookupSeamComponentWithNoInterface() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				HelloSeamNoInterface seamComponent = getComponentInstanceWithAsserts("helloSeamNoInterface", HelloSeamNoInterface.class);
				Assert.assertEquals(seamComponent.sayHello(), "Hello Seam - No Interface");
				log.debug("*** Seam with no interface says Hello :-)");
			}
		}.run();
	}
	
	@Test(groups={ "seam", "unit-test" }, dependsOnMethods={ "shouldLookupSeamComponentWithNoInterface" })
	public void shouldLookupSeamComponentWithLocalInterface() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				HelloSeam seamComponent = getComponentInstanceWithAsserts("helloSeam", HelloSeam.class);
				Assert.assertEquals(seamComponent.sayHello(), "Hello Seam");
				log.debug("*** Seam with local interface says Hello :-)");
			}
		}.run();
	}

}
