package org.superbiz.seamcalculator;

import org.jboss.seam.annotations.Name;

@Name("injectedSeamComponent")
public class InjectedSeamComponentImpl implements InjectedSeamComponent {

	public String say() {
		return "HELLO";
	}

}
