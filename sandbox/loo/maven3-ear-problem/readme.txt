Problem: 
  Maven3 does not evaluate external properties during the "ear:generate-application-xml" goal.
  
Description:
  In projects with more than one developer each developer often has her/his own 
  requirements regarding how to set up the development environment: The targeted 
  application server is different, e.g. JBoos-4.3 vs JBoss-5, and the database 
  connection url is different for each developer - etc, etc.
  
  Targeting different environments often involves affecting properties in the
  project POMs. With this requirement we can not use Maven filters to read 
  properties into the POM; they are ony visible to resources and can not be
  accessed inside the POM.
  
  This is where the Properties Maven Plugin makes our life a little easier when 
  dealing with properties that we need to access inside our POM. It provides goals 
  to read and write properties from and to files, and also to set system properties. 
  It's main use-case is loading properties from files instead of declaring them in 
  pom.xml, something that comes in handy when dealing with different environments. 
  The plugin read properties during the "initialize" phase and the properties are 
  then accessible from the pom.
  
  The use-case above works perfectly in Maven2 and the ear:generate-application-xml 
  goal creates the jboss-app.xml.
  
  In Maven3 I get the following error message:
    [ERROR] Failed to execute goal 
    org.apache.maven.plugins:maven-ear-plugin:2.4.2:generate-application-xml 
    (default-generate-application-xml) on project mvn3-problem-ear: Failed to in
    itialize JBoss configuration: Invalid JBoss configuration, version[${as.version}] 
    is not supported. -> [Help 1]
  
  If I put the ${as.version} property placeholder inside Maven's property tag
  everything works ok in Maven3. 