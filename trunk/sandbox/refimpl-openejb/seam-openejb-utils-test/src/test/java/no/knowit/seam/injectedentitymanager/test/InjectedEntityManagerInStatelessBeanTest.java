package no.knowit.seam.injectedentitymanager.test;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.injectedentitymanager.InjectedEntityManagerInStateless;
import no.knowit.seam.model.Movie;
import no.knowit.seam.openejb.mock.SeamTest;

public class InjectedEntityManagerInStatelessBeanTest extends SeamTest {
	
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
	 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
	 
	 contextProperties.put("log4j.category.no.knowit.seam.injectedentitymanager.test", "DEBUG");
	 super.beforeSuite();
	}

	@Test(groups={ "seam", "unit-test" })
	public void testInjectedEntityManagerInStatelessSessionBean() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				InjectedEntityManagerInStateless injectedEntityManager = 
					getComponentInstanceWithAsserts("injectedEntityManagerInStatelessBean", InjectedEntityManagerInStateless.class);
				Assert.assertNotNull(injectedEntityManager, "Entity manager was null");
				
				injectedEntityManager.deleteAllMovies();
				Assert.assertEquals(0, injectedEntityManager.getMovies().size(), "InjectedEntityManager.deleteAllMovies()");
				
				injectedEntityManager.addMovie(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992));
				injectedEntityManager.addMovie(new Movie("Joel Coen", "Fargo", 1996));
				injectedEntityManager.addMovie(new Movie("Joel Coen", "The Big Lebowski", 1998));
				
				List<Movie> list = injectedEntityManager.getMovies();
				Assert.assertEquals(3, list.size(), "List.size()");
				
				Movie movie = injectedEntityManager.findMovieByTitle("Fargo");
				Assert.assertNotNull(movie, "InjectedEntityManager.findMovieByTitle");
				Assert.assertEquals("Fargo", movie.getTitle(), "InjectedEntityManager.findMovieByTitle()");
				
				injectedEntityManager.deleteAllMovies();
				Assert.assertEquals(0, injectedEntityManager.getMovies().size(), "InjectedEntityManager.deleteAllMovies()");
			}
		}.run();
	}
}
