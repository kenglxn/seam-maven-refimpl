package org.jboss.seam.example.action;

import javax.ejb.Local;

@Local
public interface Authenticator {

    boolean authenticate();

}
