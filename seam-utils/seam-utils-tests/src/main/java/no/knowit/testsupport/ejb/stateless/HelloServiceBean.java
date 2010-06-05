package no.knowit.testsupport.ejb.stateless;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import no.knowit.testsupport.ejb.stateless.HelloService;

@Stateless(name="HelloService")
public class HelloServiceBean implements HelloService {
  @EJB
  private CalculatorService calculatorService;

  @Override
  public String greet() {
    return "Hello " + calculatorService.sum(1,1) + " you";
  }
}
