package no.knowit.seam.injectedseamcomponent;

import javax.ejb.Stateless;

import no.knowit.seam.hello.HelloSeam;
import no.knowit.seam.hello.HelloSeamNoInterface;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("injector")
@Stateless
@Scope(ScopeType.STATELESS)
public class InjectorBean implements Injector {

	@In(create=true)
	private HelloSeam helloSeam;

	@In(create=true)
	private HelloSeamNoInterface helloSeamNoInterface;

	public HelloSeam getInjectedSeamComponent() {
		return helloSeam;
	}

	public HelloSeamNoInterface getNoInterfaceInjectedSeamComponent() {
		return helloSeamNoInterface;
	}
}
