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
		 super.beforeSuite();
	}

	@Test(groups={ "seam" })
	public void sayHelloSeam() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				Object obj = Component.getInstance("helloSeam");
				Assert.assertNotNull(obj, "Component.getInstance(\"helloSeam\") returned null");
				Assert.assertTrue(obj instanceof HelloSeam, "Component.getInstance(\"seamCalculator\") returned incorrect type");
				Assert.assertEquals("Hello Seam", ((HelloSeam)obj).sayHello());
			}
		}.run();
	}
}
