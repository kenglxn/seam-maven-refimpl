package no.knowit.seam.injectedseamcomponent;

import javax.ejb.Stateless;

import no.knowit.seam.hello.HelloSeam;

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

	public HelloSeam getInjectedSeamComponent() {
		return helloSeam;
	}
}
