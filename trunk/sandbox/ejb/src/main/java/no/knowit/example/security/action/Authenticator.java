package no.knowit.example.security.action;

import javax.ejb.Local;

@Local
public interface Authenticator {

    boolean authenticate();

}
