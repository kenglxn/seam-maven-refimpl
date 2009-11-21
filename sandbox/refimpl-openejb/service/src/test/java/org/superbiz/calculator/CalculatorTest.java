/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.calculator;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import openejb.BootstrapSeam;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

public class CalculatorTest {
	
	private Logger log = Logger.getLogger(this.getClass());

	// START SNIPPET: setup
	private InitialContext initialContext;

	@BeforeClass
	protected void setUp() throws Exception {
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		properties.put("log4j.category.org.superbiz", "warn"); 
		properties.put("log4j.category.org.superbiz.calculator.CalculatorTest", "debug"); 

//		properties.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}");
		
		initialContext = new InitialContext(properties);
		
		
		log.debug("**** Attempting to start SEAM");
		
		BootstrapSeam bs = new BootstrapSeam();
		bs.beforeSuite();
		bs.beforeClass();
		bs.begin();
		
		log.debug("**** SEAM started");
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
		Object object = initialContext.lookup("CalculatorImplRemote");

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
		Object object = initialContext.lookup("CalculatorImplLocal");

		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof CalculatorLocal);
		CalculatorLocal calc = (CalculatorLocal) object;
		Assert.assertEquals(10, calc.sum(4, 6));
		Assert.assertEquals(12, calc.multiply(3, 4));
	}
	// END SNIPPET: local

}
