package no.knowit.openejbtest.seam.hello;

import org.jboss.seam.annotations.Name;

@Name("helloSeam")
public class HelloSeamBean implements HelloSeam {

	public String sayHello() {
		return "Hello Seam";
	}

}
