package org.superbiz.calculator;

import java.util.Properties;

import javax.naming.Context;

import no.knowit.openejb.BootStrapOpenEjb;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CalculatorTest {
	
	protected static Context initialContext;
	
	@BeforeClass
	public void setUp() throws Exception {
		Properties p = BootStrapOpenEjb.getDefaultContextProperties(null);
		
		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		p.put("log4j.category.org.superbiz", "warn"); 
		p.put("log4j.category.org.superbiz.logic.CalculatorTest", "debug"); 
		initialContext = BootStrapOpenEjb.bootstrap(p);
	}

	/**
	 * Lookup the Calculator bean via its remote home interface
	 * 
	 * @throws Exception
	 */
	@Test
	public void shallGetCalculatorViaRemoteInterface() throws Exception {
		Object object = initialContext.lookup("ejbCalculator/Remote");

		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof CalculatorRemote);
		CalculatorRemote calc = (CalculatorRemote) object;
		Assert.assertEquals(10, calc.sum(4, 6));
		Assert.assertEquals(12, calc.multiply(3, 4));
	}

	/**
	 * Lookup the Calculator bean via its local home interface
	 * 
	 * @throws Exception
	 */
	@Test
	public void shallGetCalculatorViaLocalInterface() throws Exception {
		
		// Named bean:
		Object object = initialContext.lookup("ejbCalculator/Local");
	
		// Unnamed bean:
		//Object object = initialContext.lookup("CalculatorImpl/Local");

		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof CalculatorLocal);
		CalculatorLocal calc = (CalculatorLocal) object;
		Assert.assertEquals(10, calc.sum(4, 6));
		Assert.assertEquals(12, calc.multiply(3, 4));
	}
}
