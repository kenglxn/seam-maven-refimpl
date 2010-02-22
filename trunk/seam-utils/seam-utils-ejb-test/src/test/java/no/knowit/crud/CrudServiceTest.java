package no.knowit.crud;

import java.util.List;

import javax.naming.NamingException;

import no.knowit.crudtest.model.Movie;
import no.knowit.openejb.mock.OpenEjbTest;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * 
 * @author LeifOO
 *
 */
public class CrudServiceTest extends OpenEjbTest {
	
	private Logger log = Logger.getLogger(this.getClass());
	private Movie theWall;
	private Integer fargoId;
	
	private CrudService lookupCrudService() throws Exception {
		try {
			Object service = initialContext.lookup("crudService/Local");
			Assert.assertNotNull(service, "initialContext.lookup: returned null");
			Assert.assertTrue(service instanceof CrudService, "initialContext.lookup: incorrect type");
			return (CrudService) service;
		}
		catch (NamingException e) {
			log.error(e);
			throw(e);
		}
	}
	
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		contextProperties.put("log4j.category.no.knowit.crud", "debug");
		super.beforeSuite();
	}

	@Override
	@AfterClass
	public void cleanupClass() throws Exception {
		CrudService crudService = lookupCrudService();
		List<Movie> allMovies = crudService.find(Movie.class);
  	crudService.remove((List)allMovies);
		super.cleanupClass();
	}
	
	
	@Test
	public void create() throws Exception {
		CrudService crudService = lookupCrudService();
		Movie movie = crudService.persist(new Movie("Alan Parker", "The Wall", 2000));
		Assert.assertNotNull(movie, "crudService.persist: movie was null");
		Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
		
		// Keep for read, update and delete tests
		theWall = movie;
		
		movie = crudService.persist(new Movie("Joel Coen", "Fargo", 1996));
		fargoId = movie.getId();
	}

	@Test(dependsOnMethods={ "create" })
	public void read() throws Exception {
		CrudService crudService = lookupCrudService();
		Movie movie = crudService.find(Movie.class, theWall.getId());
		Assert.assertNotNull(movie, "crudService.find: Did not find movie with id: " + theWall.getId());
	}

	@Test(dependsOnMethods={ "read" })
	public void update() throws Exception {
		CrudService crudService = lookupCrudService();
		theWall.setYear(1999);
		theWall = crudService.merge(theWall);
		Assert.assertEquals(theWall.getYear(), new Integer(1999));
	}
	
	@Test(dependsOnMethods={ "update" })
	public void delete() throws Exception {
		CrudService crudService = lookupCrudService();
		
		crudService.remove(Movie.class, fargoId);
		Assert.assertNull(crudService.find(Movie.class, fargoId));
		
		crudService.remove(theWall);
		Assert.assertNull(crudService.find(Movie.class, theWall.getId()));
	}
	
	@Test(dependsOnMethods={ "delete" })
	public void createOrUpdate() throws Exception {
		CrudService crudService = lookupCrudService();
		
		Movie movie = new Movie("Joel Choen", "The Big Leboi", 1998);
		movie = crudService.store(movie);
		Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
		
		// Modify and update
		movie.setDirector("Joel Coen");
		movie.setTitle("The Big Lebowski");
		crudService.store(movie);
		Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
		
		// Add a second movie
		movie = crudService.store(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992));
		Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
	}
	
	@Test(dependsOnMethods={ "createOrUpdate" })
	public void findAll() throws Exception {
		CrudService crudService = lookupCrudService();
		List<Movie> allMovies = crudService.find(Movie.class);
  	Assert.assertEquals(allMovies.size(), 2, "List.size()");
	}
	
	@Test(dependsOnMethods={ "findAll" })
	public void findByExample() throws Exception {
		CrudService crudService = lookupCrudService();
		Movie exampleMovie = new Movie();
		exampleMovie.setDirector("Joel%");
		exampleMovie.setYear(1992);
		List<Movie> exampleMovies = crudService.find(exampleMovie, true, true);
  	Assert.assertEquals(exampleMovies.size(), 2, "List.size()");
	}
	
	@Test(dependsOnMethods={ "findByExample" })
	public void deleteByExample() throws Exception {
		CrudService crudService = lookupCrudService();
		Movie exampleMovie = new Movie();
		exampleMovie.setDirector("Joel Coen");
		crudService.remove(exampleMovie, true);
		
		List<Movie> movies = crudService.find(exampleMovie, false, true);
  	Assert.assertEquals(movies.size(), 0, "List.size()");
	}

	@Test(dependsOnMethods={ "deleteByExample" })
	public void deleteAll() throws Exception {
		CrudService crudService = lookupCrudService();
		Movie exampleMovie = new Movie();
		crudService.remove(exampleMovie, true);
		
		List<Movie> movies = crudService.find(exampleMovie, false, false);
  	Assert.assertEquals(movies.size(), 0, "List.size()");
	}
}
