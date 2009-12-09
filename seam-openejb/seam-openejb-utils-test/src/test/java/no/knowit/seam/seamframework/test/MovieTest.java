package no.knowit.seam.seamframework.test;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.jboss.seam.Component;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.mock.AbstractSeamTest.FacesRequest;


import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.openejb.mock.SeamTest;
import no.knowit.seam.seamframework.MovieHome;

public class MovieTest extends SeamTest {

  private static final LogProvider log = Logging.getLogProvider(MovieTest.class);
  
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		 contextProperties.put("log4j.category.no.knowit.seam.seamframework.test", "debug");
		 super.beforeSuite();
	}
	
	@Test
	public void newMovie() throws Exception {
		
		new FacesRequest() {
			
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid();
				
//				MovieHome movieHome = getComponentInstanceWithAsserts("movieHome", MovieHome.class);
//				MovieHome movieHome = (MovieHome)Component.getInstance("movieHome");
//				
//				movieHome.clearInstance();
//				movieHome.getInstance().setDirector("TEST-DIRECTOR");
//				movieHome.getInstance().setTitle("TEST-TITLE");
//				movieHome.getInstance().setYear(2007);
//				movieHome.persist();
				

				invokeMethod( "#{movieHome.clearInstance}" );
				setValue("#{movieHome.instance.director}", "TEST-DIRECTOR");
				setValue("#{movieHome.instance.title}", "TEST-TITLE");
				setValue("#{movieHome.instance.year}", 2007);
				Object result = invokeMethod( "#{movieHome.persist}" );
				Assert.assertEquals(result, "persisted");

				Conversation.instance().end();

			}

		}.run();

		
	}

}
