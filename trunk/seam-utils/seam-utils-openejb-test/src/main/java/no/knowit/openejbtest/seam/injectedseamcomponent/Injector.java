package no.knowit.openejbtest.seam.injectedseamcomponent;

import javax.ejb.Local;

import no.knowit.openejbtest.seam.hello.HelloSeam;
import no.knowit.openejbtest.seam.hello.HelloSeamNoInterface;

@Local
public interface Injector {
	public HelloSeam getInjectedSeamComponent();
	public HelloSeamNoInterface getNoInterfaceInjectedSeamComponent();
}
