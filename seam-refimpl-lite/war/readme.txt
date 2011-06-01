This is WAR only implementation. All maven goals should be run within war directory.
This project configuration supports:
    -JSFUnit tests
    -hot deployment of Seam components
    -hot deployment of JSFUnit tests
    -maven-cli-plugin
    -Unitils & DBMaintain

--------------------
maven-cli-plugin:
--------------------
This is cool plugin for maven that speeds up execution of maven goals. What is even cooler it allows to alias bunch of goals, i.e.:
<plugin>
    <groupId>org.twdata.maven</groupId>
    <artifactId>maven-cli-plugin</artifactId>
    <version>1.0.6-SNAPSHOT</version>
    <configuration>
        <userAliases>
            <disableConstraints>properties:read-project-properties dbmaintain:disableConstraints</disableConstraints>
            <clearDatabase>properties:read-project-properties dbmaintain:clearDatabase</clearDatabase>
            <updateDatabase>properties:read-project-properties dbmaintain:updateDatabase</updateDatabase>
            <unexplode>clean -Punexplode</unexplode>
            <explode>package -S -Pexplode</explode>
            <restart>package -S -Prestart</restart>
            <!--jsfunitRun - updates database, builds project with JSFUnit tests, deploys it into running JBoss AS and runs JSFUnit tests -->
            <jsfunitRun>clean properties:read-project-properties dbmaintain:clearDatabase dbmaintain:updateDatabase
                dbmaintain:updateSequences dbmaintain:disableConstraints verify -Pjsfunit -Dmaven.test.skip=false
            </jsfunitRun>
            <jsfunitRestart>restart -Pjsfunit -Dmaven.test.skip=false updateDatabase disableConstraints</jsfunitRestart>
            <!--jsfunitExplode - compiles only JSFUnit tests and copies them into exploded package on running JBoss AS, this is meant for rapid test modifications -->
            <jsfunitExplode>test -PexplodeTestsFast,skipSurefire -Dmaven.test.skip=false</jsfunitExplode>
            <!--jsfunitExplodeAll - same as explode, but includes JSFUnit tests, use this when you need to update facelets and JSFUnit tests-->
            <jsfunitExplodeAll>explode -Pjsfunit,skipSurefire -Dmaven.test.skip=false</jsfunitExplodeAll>
        </userAliases>
    </configuration>
</plugin>

To lunch maven cli console run: mvn cli:execute-phase -P cli
From now on you can type i.e. "restart" to restart your exploded deployment on server. Original version of that plugin does not support verify phase which is
required to run JSFUnit tests. Use custom version from
http://bernard.labno.pl/artifactory/plugins-snapshot-local/org/twdata/maven/maven-cli-plugin/1.0.6-SNAPSHOT/maven-cli-plugin-1.0.6-SNAPSHOT.jar

Project is configured with several useful aliases:
    -explode - hot deploys facelets & Seam components
    -unexplode - undeploys application from application server and clears target directory
    -restart - deploys entire application to app server
    -jsfunitRun - runs JSFUnit tests (it is assumed that JBoss is running)
    -jsfunitExplode - hot deploys JSFUnit tests and data sets
    -jsfunitRestart - deploys entire application with JSFUnit tests to app server
    -updateDatabase - updates database using DBMaintain plugin (executes scripts from ../src/main/dbscripts)

Note that jsfunitRun deploys application via jmx-console so it must be deployed on your server and you should run unexplode first to make sure that no exploded
deployment of the same application is on the server.

--------------------
JSFUnit tests:
--------------------
JSFUnit tests are run by failsafe plugin during integration-test phase (use verify phase). Tests must obey naming convention "*IT".
When you build your application with JSFUnit tests remember that /src/test/resources/web.xml is used in stead of /src/main/webapp/WEB-INF/web.xml.
Always keep those files synchronized (the one used for JSFUnit tests has several additional lines at the bottom).
JSFUnit tests cannot be hot deployed, but they can invoke Seam components which in turn can be hot deployed, so you need to delegate.
See example of AuthenticatorIT, AuthenticatorITI and AuthenticatorITC. AuthenticatorIT is the propper test which will be run by JSFUnit, but it delegates
everything to AuthenticatorITC (c is for component). Because AuthenticatorIT lands in WEB-INF/classes and AuthenticatorITC lands in WEB-INF/dev they are
loaded by different classloaders and AuthenticatorIT cannot see AuthenticatorITC. But AuthenticatorIT can obtain AuthenticatorITC by invoking
Component.getInstance. It will return Object. It cannot be cast to AuthenticatorITC class because that class is not visible by AuthenticatorIT's classloader.
But it can be cast to some interface and thats what AuthenticatorITI is used for.
So, AuthenticatorITC is hot deployable while AuthenticatorIT and AuthenticatorITI are not.

You can run tests from CLI console with jsfunitRun command (start JBoss before that).
When you develop tests it is better to jsfunitRestart and then lunch each test suite from browser, i.e.:

    http://localhost:8080/seam-refimpl/ServletTestRunner?suite=no.knowit.seam.example.security.test.AuthenticatorIT&xsl=cactus-report.xsl

the xsl parameter is optional and can be used to transform XML results into human readable HTML form.

In order to be able to call Component.getInstance from AuthenticatorIT there must be active Seam context. This is done in components.xml:

    <web:context-filter regex-url-pattern="/Servlet.*" installed="${jsfunit.context_filter.installed}"/>

In production this can be turned off. It maps context-filter to JSFUnit servlets.

--------------------
Hot deployment:
--------------------
Project is configured to support hot deployment. Seam hot deploys stuff that lands in WEB-INF/dev. Nothing that is instantiated by classed from outside of that
directory can be put there (jsf validators, converters, entities, jsfunit test cases etc.). So everything that needs to be done is to copy some classes to WEB-INF/classes
and some classes to WEB-INF/dev. To tell Maven which class goes where use war/nonhotdeployable file. It uses Ant pattern syntax. Example:
**/*IT.class
**/*IT$*.class
**/*ITI.class
**/no/knowit/seam/example/model/**/*.class
**/no/knowit/seam/example/test/AbstractUnitilisedJSFUnitTestCase.class
**/no/knowit/seam/example/test/AbstractUnitilisedJSFUnitTestCase$*.class

First line includes JSFUnit tests (IT suffix stands for integration test).
Second line includes any inner classes that could be defined in JSFUnit tests.
Third line includes interfaces of JSFUnit tests.
Fourth line includes entities. (If you scatter entities all over your packages tree then you will be in pain of maintaining this file often).
Fifth and sixth lines include base class used for tests.

--------------------
Unitils:
--------------------
This is cool library for updating your database with data sets before each test. Datasets are defined as XML files in src/test/resources and must
match test's package an class name. Unitils looks cool with JUnit4 but this configuration uses JSFunit+Cactus which enforces JUnit3.
This is what AbstractUnitilisedJSFUnitTestCase is for. It invokes Unitils stuff before each test.
Important: if you want to have your data set inserted annotate your test (*IT) with @DataSet.
If you want XSD schema for your database to be generated just change unitils.generateDataSetStructure propert in filter to true.
It will regenerate src/test/dbschemaxsd/*.xsd.
Important: Under windows do not use ${basedir} in properties related to unitils. You will get problem with backslashes.
Just type full path with slashes, i.e.: d:/documents and settings/tralala/