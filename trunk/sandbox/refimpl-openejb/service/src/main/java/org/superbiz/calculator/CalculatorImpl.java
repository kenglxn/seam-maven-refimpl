package org.superbiz.calculator;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * This is an EJB 3 style pojo stateless session bean
 * Every stateless session bean implementation must be annotated
 * using the annotation @Stateless
 * This EJB has 2 business interfaces: CalculatorRemote, a remote business
 * interface, and CalculatorLocal, a local business interface
 * 
 */
//@Stateless //(name="ejbCalculator")
@Name("seamCalculator")
public class CalculatorImpl implements CalculatorRemote, CalculatorLocal {

	@In(required=false)
	private EntityManager em;
	
	@PostConstruct
	public void postConstruct() {
		System.out.println("****************** @PostConstruct");
	}
	
	public int sum(int add1, int add2) {
		
		return add1+add2;
	}

	public int multiply(int mul1, int mul2) {
		return mul1*mul2;
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
}
