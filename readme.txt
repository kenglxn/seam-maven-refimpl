Before you build any projects you'll have to build the root pom and required dependencies.

* Open a command shell and CD to the trunk folder, e.g. ./seam-maven-refimpl/trunk.

* Type: mvn clean install
  - The maven install command builds the no.knowit.seam:root pom which all projects in this trunk
    uses as its root pom.
	- Builds the no.knowit.seam:seam-openejb modules which is used for test purposes

Note to Eclipse users:
The m2eclipse plugin seems to be a bit unpredictable when it comes to downloading source code and 
javadocs for the projects dependencies. Try this if you find that source code and javadocs are missing for 
some dependencies:

* Open a command shell and CD to the ./trunk/seam-refimpl folder and type: 
  mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true