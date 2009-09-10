package net.glxn.admintool.action;

import javax.ejb.Local;

@Local
public interface Authenticator {

    boolean authenticate();

}
