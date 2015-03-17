# A Mavenized Version of the jboss-seam-2.2.0/examples/ui project #

This is a Mavenized version of the jboss-seam-2.2.0/examples/ui project.

## Howto ##
The steps required to get the project up and running:
  * [The Getting Started tutorial](http://www.glxn.net/seam-maven-refimpl/doc/tutorial/) explains the basic steps
  * [Checkout from svn](http://seam-maven-refimpl.googlecode.com/svn/trunk/) and build required modules
  * Copy jboss-seam-ui/src/main/filters/filter-prod.properties to jboss-seam-ui/src/main/filters/filter-dev.properties and modify the filter-dev.properties file as described in the getting started tutorial.
  * Open a command shell and cd to project folder, jboss-seam-ui
  * Execute **mvn install -Penv-dev,explode**
  * Start the server and access the application at http://localhost:8080/seam-ui