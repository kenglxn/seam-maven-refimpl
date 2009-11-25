package org.superbiz.seamcalculator;

import no.knowit.seam.openejb.mock.SeamOpenEjbTest;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class SeamCalculatorTest extends SeamOpenEjbTest {
	
	//private Logger log = Logger.getLogger(this.getClass());

	@BeforeSuite
	@Override
	public void beforeSuite() throws Exception {
		System.out.println("@BeforeSuite->CalculatorTest.beforeSuite");
		super.startSeam();
	}
	
	
	@Override
	@BeforeClass
	public void setupClass() throws Exception {
		System.out.println("@BeforeClass->CalculatorTest.setupClass");
		super.setupClass();

		// TODO:
		//Properties properties = new Properties();
		//properties.put("log4j.category.org.superbiz", "warn"); 
		//properties.put("log4j.category.org.superbiz.calculator.CalculatorTest", "debug");
	}

	private SeamCalculator getService() {
		
		Object obj;
		
		
		// Lifecycle.beginCall()/endCall is not needed if you execute test inside ComponentTest() or FacesRequest()
//		Lifecycle.beginCall();
//		try {
//			obj = Component.getInstance("seamCalculator");
//		}
//		finally {
//			Lifecycle.endCall();
//		}
		
		obj = Component.getInstance("seamCalculator");
		Assert.assertNotNull(obj, "Component.getInstance(\"seamCalculator\") returned null");
		Assert.assertTrue(obj instanceof SeamCalculator, "Component.getInstance(\"seamCalculator\") returned incorrect type");
		return (SeamCalculator)obj;
	}
	
	/**
	 * Lookup the Calculator bean via its Seam component name
	 * 
	 * @throws Exception
	 */
	@Test
	public void shallGetSeamComponentViaNameAnnotation() throws Exception {
		
		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				SeamCalculator calculator = getService();
				Assert.assertEquals(11, calculator.sum(5, 6));
				Assert.assertEquals(16, calculator.multiply(4, 4));
			}
		}.run();
	}
	
	@Test
	public void shallGetInjectedSeamComponent() throws Exception {
		
		new ComponentTest() {
			 @Override
       protected void testComponents() throws Exception {
					SeamCalculator calculator = getService();
					Assert.assertEquals("HELLO", calculator.helloFromInjectedComponent());
			 }
		}.run();
	}
}
