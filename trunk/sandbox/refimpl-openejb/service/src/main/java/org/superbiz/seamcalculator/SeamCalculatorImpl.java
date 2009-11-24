package org.superbiz.seamcalculator;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("seamCalculator")
@Stateless
@Scope(ScopeType.STATELESS)
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

	@Override
	public String helloFromInjectedComponent() {
		return injectedSeamComponent != null ? injectedSeamComponent.say() : null;
	}
}
