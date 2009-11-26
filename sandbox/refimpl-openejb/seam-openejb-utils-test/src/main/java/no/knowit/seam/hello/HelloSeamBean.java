package no.knowit.seam.hello;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

@Name("helloSeam")
public class HelloSeamBean implements HelloSeam {

	public String sayHello() {
		return "Hello Seam";
	}

}
