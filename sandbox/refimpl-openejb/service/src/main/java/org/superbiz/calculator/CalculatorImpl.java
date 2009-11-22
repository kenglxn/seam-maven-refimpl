package org.superbiz.calculator;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

/**
 * This is an EJB 3 style pojo stateless session bean
 * Every stateless session bean implementation must be annotated
 * using the annotation @Stateless
 * This EJB has 2 business interfaces: CalculatorRemote, a remote business
 * interface, and CalculatorLocal, a local business interface
 * 
 */
@Stateless(name="calculator")
@Name("calculator")
public class CalculatorImpl implements CalculatorRemote, CalculatorLocal {

	public int sum(int add1, int add2) {
		
		return add1+add2;
	}

	public int multiply(int mul1, int mul2) {
		return mul1*mul2;
	}

}
