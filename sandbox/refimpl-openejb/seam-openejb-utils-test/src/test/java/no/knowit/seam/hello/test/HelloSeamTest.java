package no.knowit.seam.hello.test;

import org.jboss.seam.Component;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.hello.HelloSeam;
import no.knowit.seam.openejb.mock.SeamTest;

public class HelloSeamTest extends SeamTest {

	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		 contextProperties.put("log4j.category.no.knowit.seam.hello", "debug");
		 super.beforeSuite();
	}

	@Test(groups={ "seam" })
	public void sayHelloSeam() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				HelloSeam obj = getComponentInstance("helloSeam");
				Assert.assertNotNull(obj, "Component.getInstance(\"helloSeam\") returned null");
				Assert.assertEquals("Hello Seam", ((HelloSeam)obj).sayHello());
			}
		}.run();
	}
}
