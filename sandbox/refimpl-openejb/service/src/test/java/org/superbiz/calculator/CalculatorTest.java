package org.superbiz.calculator;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import no.knowit.openejb.SeamOpenEjbTest;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CalculatorTest extends SeamOpenEjbTest {
	
	private Logger log = Logger.getLogger(this.getClass());

	// START SNIPPET: setup
	private InitialContext initialContext;

	@BeforeClass
	protected void setUp() throws Exception {

//		Properties properties = new Properties();
//		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
//		
//		properties.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}");
//		properties.put("log4j.category.org.superbiz", "warn"); 
//		properties.put("log4j.category.org.superbiz.calculator.CalculatorTest", "debug"); 
//		
//		initialContext = new InitialContext(properties);
		
		
//		log.debug("**** Attempting to start SEAM");
//		SeamOpenEjbTest bs = new SeamOpenEjbTest();
//		bs.beforeSuite();
//		bs.beforeClass();
//		bs.begin();
//		log.debug("**** SEAM started");
		
//		bs.beforeClass();
//		log.debug("**** before class executed");
//		
//		Object o = Component.getInstance("calculator");
	}

	// END SNIPPET: setup

	/**
	 * Lookup the Calculator bean via its remote home interface
	 * 
	 * @throws Exception
	 */
	// START SNIPPET: remote
	@Test
	public void testCalculatorViaRemoteInterface() throws Exception {
//		Object object = initialContext.lookup("CalculatorImpl/Remote");
//
//		Assert.assertNotNull(object);
//		Assert.assertTrue(object instanceof CalculatorRemote);
//		CalculatorRemote calc = (CalculatorRemote) object;
//		Assert.assertEquals(10, calc.sum(4, 6));
//		Assert.assertEquals(12, calc.multiply(3, 4));
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
//		Object object = initialContext.lookup("CalculatorImpl/Local");
//
//		Assert.assertNotNull(object);
//		Assert.assertTrue(object instanceof CalculatorLocal);
//		CalculatorLocal calc = (CalculatorLocal) object;
//		Assert.assertEquals(10, calc.sum(4, 6));
//		Assert.assertEquals(12, calc.multiply(3, 4));
	}
	// END SNIPPET: local

}
