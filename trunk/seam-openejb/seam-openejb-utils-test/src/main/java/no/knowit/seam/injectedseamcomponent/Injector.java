package no.knowit.seam.injectedseamcomponent;

import javax.ejb.Local;

import no.knowit.seam.hello.HelloSeam;
import no.knowit.seam.hello.HelloSeamNoInterface;

@Local
public interface Injector {
	public HelloSeam getInjectedSeamComponent();
	public HelloSeamNoInterface getNoInterfaceInjectedSeamComponent();
}
