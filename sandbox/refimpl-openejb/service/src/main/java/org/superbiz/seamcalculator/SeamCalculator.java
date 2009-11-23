package org.superbiz.seamcalculator;

import javax.ejb.Local;

@Local
public interface SeamCalculator {
	public int sum(int add1, int add2);
	public int multiply(int mul1, int mul2);
}
