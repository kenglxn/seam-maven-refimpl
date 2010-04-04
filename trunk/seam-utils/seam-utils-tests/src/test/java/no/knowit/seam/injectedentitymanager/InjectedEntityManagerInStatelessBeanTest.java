package no.knowit.seam.injectedentitymanager;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.seam.injectedentitymanager.InjectedEntityManagerInStateless;
import no.knowit.seam.openejb.mock.SeamOpenEjbTest;

public class InjectedEntityManagerInStatelessBeanTest extends SeamOpenEjbTest {
	
//	@Override
//	@BeforeSuite
//	public void beforeSuite() throws Exception {
//	 contextProperties.put("log4j.category.no.knowit.seam.injectedentitymanager", "DEBUG");
//	 super.beforeSuite();
//	}


	@Test
	public void shouldInjectEntityManagerInStatelessSeamComponent() throws Exception {

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
