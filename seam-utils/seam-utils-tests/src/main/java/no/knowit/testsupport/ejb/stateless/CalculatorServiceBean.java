package no.knowit.testsupport.ejb.stateless;

import javax.ejb.Stateless;

import no.knowit.testsupport.ejb.stateless.CalculatorService;

@Stateless(name="calculatorService")
public class CalculatorServiceBean implements CalculatorService {

  public int sum(int add1, int add2) {
    return add1+add2;
  }

  public int multiply(int mul1, int mul2) {
    return mul1*mul2;
  }

}
