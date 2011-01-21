package no.knowit.seam.injectedentitymanager;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.seam.injectedentitymanager.InjectedEntityManagerInStateless;
import no.knowit.seam.openejb.mock.SeamOpenEjbTest;

public class InjectedEntityManagerInStatelessBeanTest extends SeamOpenEjbTest {
	
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
				
				injectedEntityManager.addMovie(Movie.with()
				    .director("Quentin Tarantino").title("Reservoir Dogs").year(1992).build());
				injectedEntityManager.addMovie(Movie.with()
            .director("Joel Coen").title("Fargo").year(1996).build());
				injectedEntityManager.addMovie(Movie.with()
            .director("Joel Coen").title("The Big Lebowski").year(1998).build());
				
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
