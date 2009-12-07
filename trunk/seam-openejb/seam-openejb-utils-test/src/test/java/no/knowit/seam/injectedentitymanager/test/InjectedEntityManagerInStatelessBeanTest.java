package no.knowit.seam.injectedentitymanager.test;

import java.util.List;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.injectedentitymanager.InjectedEntityManagerInStateless;
import no.knowit.seam.model.Movie;
import no.knowit.seam.openejb.mock.SeamTest;

public class InjectedEntityManagerInStatelessBeanTest extends SeamTest {
	
  private static final LogProvider log = Logging.getLogProvider(InjectedEntityManagerInStatelessBeanTest.class);
	
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
	 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
	 
	 contextProperties.put("log4j.category.no.knowit.seam.injectedentitymanager", "DEBUG");
	 super.beforeSuite();
	}


	@Test(groups={ "seam", "unit-test" } )
	public void shouldGetInjectedEntityManagerInStatelessSessionBean() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				InjectedEntityManagerInStateless injectedEntityManager = 
					getComponentInstanceWithAsserts("injectedEntityManagerInStatelessBean", InjectedEntityManagerInStateless.class);
				Assert.assertNotNull(injectedEntityManager, "Entity manager was null");
				
				injectedEntityManager.deleteAllMovies();
				Assert.assertEquals(injectedEntityManager.getMovies().size(), 0, "InjectedEntityManager.deleteAllMovies()");
				
				injectedEntityManager.addMovie(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992));
				injectedEntityManager.addMovie(new Movie("Joel Coen", "Fargo", 1996));
				injectedEntityManager.addMovie(new Movie("Joel Coen", "The Big Lebowski", 1998));
				
				List<Movie> list = injectedEntityManager.getMovies();
				Assert.assertEquals(list.size(), 3, "List.size()");
				
				Movie movie = injectedEntityManager.findMovieByTitle("Fargo");
				Assert.assertNotNull(movie, "InjectedEntityManager.findMovieByTitle");
				Assert.assertEquals(movie.getTitle(), "Fargo", "InjectedEntityManager.findMovieByTitle()");
				
				injectedEntityManager.deleteAllMovies();
				Assert.assertEquals(injectedEntityManager.getMovies().size(), 0, "InjectedEntityManager.deleteAllMovies()");
			}
		}.run();
	}

}
