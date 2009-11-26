package no.knowit.seam.injectedentitymanager.test;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.injectedentitymanager.InjectedEntityManager;
import no.knowit.seam.model.Movie;
import no.knowit.seam.openejb.mock.SeamTest;

public class InjectedEntityManagerTest extends SeamTest {
	
  private static final LogProvider log = Logging.getLogProvider(InjectedEntityManagerTest.class);

	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		 contextProperties.put("log4j.category.no.knowit.seam.injectedentitymanager.test", "DEBUG");
		 super.beforeSuite();
	}

	@Test(groups={ "seam", "unit-test" })
	public void pleaseLetTheInjectedEntityManagerWorkAsExpected() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				InjectedEntityManager injectedEntityManager = getComponentInstanceWithAsserts("injectedEntityManager", InjectedEntityManager.class);
				Assert.assertNotNull(injectedEntityManager, "Entity manager was null");
				
				/*
				List<Movie> list = injectedEntityManager.getMovies();
				Assert.assertEquals(3, list.size(), "List.size()");
				*/
				
				log.debug("*** The @In(jected) Seam component says Hello :-)");
			}
		}.run();
	}
}
