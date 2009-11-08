package no.knowit.example.security.action;

import javax.ejb.Stateless;

@Stateless
public class SimpleServiceBean implements SimpleService{

    public String sayHello(String friend) {
        return "hello " + friend;
    }
}
