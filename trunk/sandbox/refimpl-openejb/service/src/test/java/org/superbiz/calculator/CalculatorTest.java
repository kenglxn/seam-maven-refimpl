package org.superbiz.calculator;

import no.knowit.seam.mock.SeamOpenEjbTest;

import org.superbiz.calculator.*;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class CalculatorTest extends SeamOpenEjbTest {
	
	private Logger log = Logger.getLogger(this.getClass());

	// START SNIPPET: setup
	//private InitialContext initialContext;
	
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
		System.out.println("@BeforeSuite->CalculatorTest.beforeSuite");
	}
	
	
	@BeforeClass
	protected void setUp() throws Exception {
		System.out.println("@BeforeClass->CalculatorTest.setUp");

//		Properties properties = new Properties();
//		properties.put("log4j.category.org.superbiz", "warn"); 
//		properties.put("log4j.category.org.superbiz.calculator.CalculatorTest", "debug");
		
		boolean createContexts = !Contexts.isEventContextActive() && !Contexts.isApplicationContextActive();
    if (createContexts) {
        Lifecycle.beginCall();
    }
	}

	// END SNIPPET: setup

	/**
	 * Lookup the Calculator bean via its Seam component name
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculatorViaSeamComponentName() throws Exception {
		//Object seamComponent = Component.getInstance("calculator", true, true);

		Object seamComponent = getInstance("calculator");
		
		//CalculatorImpl seamComponent = getContextComponent("calculator");

		Assert.assertNotNull(seamComponent);
		System.out.println("@Test->CalculatorTest.testCalculatorViaSeamComponentName: " + seamComponent);
		//Assert.assertTrue(seamComponent instanceof CalculatorLocal);
	}

	
	/**
	 * Lookup the Calculator bean via its remote home interface
	 * 
	 * @throws Exception
	 */
	// START SNIPPET: remote
	@Test
	public void testCalculatorViaRemoteInterface() throws Exception {
		Object object = initialContext.lookup("calculator/Remote");

		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof CalculatorRemote);
		CalculatorRemote calc = (CalculatorRemote) object;
		Assert.assertEquals(10, calc.sum(4, 6));
		Assert.assertEquals(12, calc.multiply(3, 4));
	}

	// END SNIPPET: remote

	/**
	 * Lookup the Calculator bean via its local home interface
	 * 
	 * @throws Exception
	 */
	// START SNIPPET: local
	@Test
	public void testCalculatorViaLocalInterface() throws Exception {
		Object object = initialContext.lookup("calculator/Local");

		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof CalculatorLocal);
		CalculatorLocal calc = (CalculatorLocal) object;
		Assert.assertEquals(10, calc.sum(4, 6));
		Assert.assertEquals(12, calc.multiply(3, 4));
	}
	// END SNIPPET: local

}
