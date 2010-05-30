Problem: 
  Maven2 does not calculate file paths correctly in profile activation
  
Description:
  In a multi module project I want to activate a profile in a child 
  POM based on file exist/missing in parent POM's directory, e.g.
  
  <profiles>
    <profile>
      <id>env-dev</id>
      <activation>
        <file>
          <exists>${basedir}/../dev.properties</exists>
        </file>
      </activation>
      <properties>
        <env>dev</env>
      </properties>
    </profile>
    <profile>
      <id>env-prod</id>
      <activation>
        <file>
          <missing>${basedir}/../dev.properties</missing>
        </file>
      </activation>
      <properties>
        <env>prod</env>
      </properties>
    </profile>
  </profiles>
  
  Given that two files, dev.properties and prod.properties, 
  exists in the parent POM's directory and two profiles, 
  env-dev and env-prod ,is defined in the child POM.
  When I execute mvn help:acive.profiles, then the "env-dev" 
  profile should be activated for the child POM.
  
  This works ok when I execute mvn help:active-profiles from the 
  paren POM's directoy, but WRONG profile is activated when I execute 
  mvn help:active-profiles from the child directory.
  
  In Maven3, the env-dev profile is activated correctly.
  Maven 2 activates correct profile if I add a hard coded file path, e.g. 
  
  <profile>
    <id>env-dev</id>
    <activation>
      <file>
        <exists>c:\\projects\\mvn2-profile-activation-problem\\dev.properties</exists>
      </file>
    </activation>
    <properties>
      <env>dev</env>
    </properties>
  </profile>
  