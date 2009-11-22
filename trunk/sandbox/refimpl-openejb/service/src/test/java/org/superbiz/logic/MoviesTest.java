package org.superbiz.logic;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import no.knowit.openejb.OpenEjbBootStrap;

import org.apache.log4j.Logger;
import org.superbiz.model.Movie;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MoviesTest {

  private Logger log = Logger.getLogger(this.getClass());

	protected static Context context;

//	public MoviesTest() {
//	}
//
//	@BeforeClass
//	public void setUp() throws Exception {
//		Properties p = new Properties();
//		
//		p.put("openejb.embedded.initialcontext.close", "destroy"); // http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
//		p.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}");
//		p.put("log4j.category.org.superbiz", "warn"); 
//		p.put("log4j.category.org.superbiz.logic.MoviesTest", "debug"); 
//		
//		context = OpenEjbBootStrap.bootstrap(p);
//	}
//
//	@AfterClass
//	public void tearDown() throws Exception {
//		context.close();
//	}
//
//	private Movies createService() throws Exception {
//		try {
//			Object obj = context.lookup("movies/Local");
//			Assert.assertNotNull(obj, "context.lookup(moviesLocal): returned null");
//			Assert.assertTrue(obj instanceof Movies, "context.lookup(moviesLocal): incorrect type");
//			return (Movies) obj;
//		}
//		catch (NamingException e) {
//			log.error(e);
//			throw(e);
//		}
//	}
//	
//	@Test
//	public void test() throws Exception {
//		Movies movies = createService();
//		movies.addMovie(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992));
//		movies.addMovie(new Movie("Joel Coen", "Fargo", 1996));
//		movies.addMovie(new Movie("Joel Coen", "The Big Lebowski", 1998));
//
//		List<Movie> list = movies.getMovies();
//		Assert.assertEquals(3, list.size(), "List.size()");
//
//		for (Movie movie : list) {
//			movies.deleteMovie(movie);
//		}
//
//		Assert.assertEquals(0, movies.getMovies().size(), "Movies.getMovies()");
//	}
}
