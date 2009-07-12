Seam GroovyBooking Example
==========================

This is a Mavenized version of the JBoss Seam 2.1.2 groovybooking examle.
The example application is tested on JBoss Application Server 4.2.3 GA jdk6

Differences from the original app:
==================================
* The datasource file, seam-groovybooking-web-ds.xml, is moved to the project root folder
* Groovy files are located in the war/src/groovy folder
* Removed components.properties, uses filtering to set values in components.xml
* installed="false" did not work for persistence:entity-manager-factory, must evaluate to "true"
    <persistence:entity-manager-factory 
      name="groovyEntityManagerFactory"
      persistence-unit-name="groovy" 
      installed="true"/>
* No hot code replacement, all compiled code deployed to WEB-INF/classes
* Fixed transient dependencies
* Used gmaven-plugin to compile Groovy files
* "groovy-all" must be upgraded to version 1.6.3. 
  Version 1.5.4 provided by the Seam-2.1.2 root POM, does not work.

How to run the application:
===========================

Access the application at http://localhost:8080/seam-groovybooking-web
