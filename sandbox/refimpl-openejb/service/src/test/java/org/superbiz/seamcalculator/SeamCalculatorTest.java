package org.superbiz.seamcalculator;

import no.knowit.seam.mock.SeamOpenEjbTest;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class SeamCalculatorTest extends SeamOpenEjbTest {
	
	//private Logger log = Logger.getLogger(this.getClass());

	@BeforeSuite
	public void beforeSuite() throws Exception {
		System.out.println("@BeforeSuite->CalculatorTest.beforeSuite");
	}
	
	
	@BeforeClass
	protected void setUp() throws Exception {
		System.out.println("@BeforeClass->CalculatorTest.setUp");

		// TODO:
		//Properties properties = new Properties();
		//properties.put("log4j.category.org.superbiz", "warn"); 
		//properties.put("log4j.category.org.superbiz.calculator.CalculatorTest", "debug");
	}

	private SeamCalculator getService() {
		Object obj;
		Lifecycle.beginCall();
		try {
			obj = Component.getInstance("seamCalculator");
		}
		finally {
			Lifecycle.endCall();
		}
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
		
//		System.out.println("@Test->CalculatorTest.testCalculatorViaSeamComponentName: " + calculator);
//		System.out.println("@Test->CalculatorTest.testCalculatorViaSeamComponentName: " + calculator.sum(1, 1));
		
		SeamCalculator calculator = getService();
		System.out.println("@Test->CalculatorTest.shallGetInjectedSeamComponent: " + calculator);
		
		Assert.assertEquals(11, calculator.sum(5, 6));
		Assert.assertEquals(16, calculator.multiply(4, 4));
	}
	
	@Test
	public void shallGetInjectedSeamComponent() throws Exception {
		SeamCalculator calculator = getService();
		Assert.assertNotNull(calculator.getInjectedSeamComponent(), "calculator.getInjectedSeamComponent() returned null");
		Assert.assertEquals("HELLO", calculator.helloFromInjectedComponent());
	}
}
