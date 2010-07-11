package no.knowit.seam.example.security;

import javax.ejb.Local;

@Local
public interface Authenticator {

    boolean authenticate();

}
