package no.knowit.testsupport.ejb.stateless;

import javax.ejb.Remote;

@Remote
public interface CalculatorService {
  int sum(int add1, int add2);
  int multiply(int mul1, int mul2);
}
