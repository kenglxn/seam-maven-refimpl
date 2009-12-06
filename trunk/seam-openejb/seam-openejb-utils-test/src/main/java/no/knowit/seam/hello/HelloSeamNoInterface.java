package no.knowit.seam.hello;

import org.jboss.seam.annotations.Name;

@Name("helloSeamNoInterface")
public class HelloSeamNoInterface {

	public String sayHello() {
		return "Hello Seam - No Interface";
	}

}
