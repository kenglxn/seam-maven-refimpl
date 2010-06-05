package no.knowit.testsupport.ejb.stateless;

import javax.ejb.Local;

@Local
public interface HelloService {
  String greet();
}
