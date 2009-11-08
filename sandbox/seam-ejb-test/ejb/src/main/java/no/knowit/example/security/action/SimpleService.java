package no.knowit.example.security.action;

import javax.ejb.Local;

/**
 * User: ken
 * Date: 08.nov.2009
 * Time: 09:57:08
 */
@Local
public interface SimpleService {
    String sayHello(String friend);
}
