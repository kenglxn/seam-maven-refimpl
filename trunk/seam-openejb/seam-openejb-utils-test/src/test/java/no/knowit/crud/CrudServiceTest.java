package no.knowit.crud;

import java.util.List;

import javax.naming.NamingException;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.model.Movie;

import org.apache.log4j.Logger;
import org.testng.Assert;
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
		contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		//contextProperties.put("openejb.deployments.classpath.ear", "true");
		contextProperties.put("log4j.category.no.knowit.crud", "debug");
		
		super.beforeSuite();
	}

	@Test
	public void create() throws Exception {
		CrudService crudService = lookupCrudService();
		Movie movie = crudService.persist(new Movie("Alan Parker", "The Wall", 2000));
		Assert.assertNotNull(movie, "crudService.persist: movie was null");
		Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
		
		// Keep for read, update and delete test methods
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
		crudService.store(movie);
		
		movie.setDirector("Joel Coen");
		movie.setTitle("The Big Lebowski");
		crudService.store(movie);
	}
	
	@Test(dependsOnMethods={ "createOrUpdate" })
	public void findAll() throws Exception {
		CrudService crudService = lookupCrudService();
		List<Movie> allMovies = crudService.find(Movie.class);
	}
	
}
