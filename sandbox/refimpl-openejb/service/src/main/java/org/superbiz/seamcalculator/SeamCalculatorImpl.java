package org.superbiz.seamcalculator;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("seamCalculator")
//@Stateless
public class SeamCalculatorImpl implements SeamCalculator {
	
	@In(create=true)
	private InjectedSeamComponent injectedSeamComponent;

	@PostConstruct
	public void postConstruct() {
		System.out.println("@PostConstruct->SeamCalculatorImpl.postConstruct");
	}
	
	public int sum(int add1, int add2) {
		return add1+add2;
	}

	public int multiply(int mul1, int mul2) {
		return mul1*mul2;
	}

	public InjectedSeamComponent getInjectedSeamComponent() {
		return injectedSeamComponent;
	}

	@Override
	public String helloFromInjectedComponent() {
		return injectedSeamComponent.say();
	}
}
