# A Mavenized Version of Seam In Action, Chapter Eleven. #

This is a Mavenized version of the source code from chapter eleven in the excellent Seam In Action book using the **seam-refimpl** project as a template.
You will need JBoss-4.2.3 or JBoss-5.x and MYSQL-5.x to run the project "as is".

## Howto ##
Here are the steps required to get the project up and running.
  * [The Getting Started tutorial explains the basic steps](http://www.glxn.net/seam-maven-refimpl/doc/tutorial/).
  * [Export the examples "root" POM from the samples folder and install it to your local .m2 repo](http://seam-maven-refimpl.googlecode.com/svn/trunk/samples).
    * Open a command shell and cd to the folder where you exported the pom, e.g ./seam-refimpl/samples/.
    * Execute **mvn install** to install the pom.
    * This is the "root" POM for all the example applications, so you need to install it to your local m2 repo.
  * In MYSQL.
    * Create database "**open18ch10\_db**"
    * Execute ./samples/seaminaction/etc/schema/open18-schema-mysql-ch10.sql
    * Execute ./samples/seaminaction/etc/schema/open18-seed-data-mysql-ch10.sql
  * [Export project from svn](http://seam-maven-refimpl.googlecode.com/svn/trunk/samples/seaminaction/stages/projects-ch11)
    * Copy ./projects-ch11/config/profiles.xml to the project root folder, ./projects-ch11. Modify profiles.xml as explained in the getting started tutorial.
    * Open a command shell and cd to the project folder.
    * Execute **mvn install -Pexplode**.
  * Start the server and access the application at http://localhost:8080/open18-ch11.

### Modifications to original code ###
see: [chapter-developments.txt](http://seam-maven-refimpl.googlecode.com/svn/trunk/samples/seaminaction/stages/projects-ch11/chapter-developments.txt)