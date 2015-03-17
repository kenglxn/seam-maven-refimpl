# A Mavenized Version of Seam In Action, Chapter Four. #

This is a Mavenized version of the source code from chapter four in the excellent Seam In Action book using the **seam-refimpl** project as a template.
You will need JBoss-4.2.3 or JBoss-5.x and MYSQL-5.x to run the project "as is".

## How to install and run the application ##
  * [The Getting Started tutorial](http://www.glxn.net/seam-maven-refimpl/doc/tutorial/) explains the basic steps
  * [Checkout from svn](http://seam-maven-refimpl.googlecode.com/svn/trunk/) and build required modules
  * In MYSQL.
    * Create database "**open18ch04\_db**"
    * Execute ./samples/seaminaction/etc/schema/open18-schema-mysql-ch04.sql
    * Execute ./samples/seaminaction/etc/schema/open18-seed-data-mysql-ch04.sql
  * Copy **projects-ch04/src/main/filters/filter-prod.properties** to projects-ch04/src/main/filters/**filter-dev.properties** and modify the filter-dev.properties file as described in the getting started tutorial
  * Open a command shell and cd to project folder; **projects-ch04**
  * Execute **mvn install -Penv-dev,explode**
  * Start the server and access the application at http://localhost:8080/open18-ch04

### Modifications to original code ###
see: [chapter-developments.txt](http://seam-maven-refimpl.googlecode.com/svn/trunk/samples/seaminaction/stages/projects-ch04/chapter-developments.txt)