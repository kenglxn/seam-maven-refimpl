package no.knowit.seam.injectedseamcomponent;

import javax.ejb.Local;

import no.knowit.seam.hello.HelloSeam;

@Local
public interface Injector {
	public HelloSeam getInjectedSeamComponent();
}
