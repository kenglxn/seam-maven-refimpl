Before you build any projects you'll have to build the root pom and required dependencies.

* Open a command shell and CD to the "trunk" folder, e.g. ./seam-maven-refimpl/trunk.

* Type: mvn clean install
  - Builds the no.knowit.seam:root pom 
    which is used as a root pom by all projects this trunk.
	- Builds the no.knowit.seam:seam-utils modules
	
* CD to the "seam-refimpl" folder and type "mvn clean install"

* CD to the "samples" folder and play with the example projects

Note to Eclipse users:
The m2eclipse plugin seems to be a bit unpredictable when it comes to downloading source code and 
javadocs for the projects dependencies. If you find that source code and javadocs are missing for 
some dependencies try this:

* Open a command shell and CD to the ./trunk/seam-refimpl folder and type: 
  mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true
	
* Import the project into Eclipse as described in the Eclipse tutorial