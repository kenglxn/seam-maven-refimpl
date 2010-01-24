package no.knowit.openejbtest.seam.hello;

import javax.ejb.Local;

@Local
public interface HelloSeam {
	public String sayHello();
}
