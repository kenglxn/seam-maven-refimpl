# A Mavenized Version of the jboss-seam-2.2.0.GA/examples/drools project #

This is a Mavenized version of the jboss-seam-2.2.0.GA/examples/drools project,
demonstrating the use of jBPM with Drools.

## How to install and run the application ##
  * [The Getting Started tutorial](http://www.glxn.net/seam-maven-refimpl/doc/tutorial/) explains the basic steps
  * [Checkout from svn](http://seam-maven-refimpl.googlecode.com/svn/trunk/) and build required modules
  * Copy **drools/src/main/filters/filter-prod.properties** to drools/src/main/filters/**filter-dev.properties** and modify the filter-dev.properties file as described in the getting started tutorial
  * Open a command shell and cd to project folder; **drools**
  * Execute **mvn install -Penv-dev,explode**
  * Start the server and access the application at http://localhost:8080/seam-drools