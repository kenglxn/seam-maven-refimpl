package org.superbiz.seamcalculator;

import javax.ejb.Local;

@Local
public interface SeamCalculator {
	int sum(int add1, int add2);
	int multiply(int mul1, int mul2);
	InjectedSeamComponent getInjectedSeamComponent();
	String helloFromInjectedComponent();
}
