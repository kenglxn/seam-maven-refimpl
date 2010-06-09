package no.knowit.crud;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.model.Movie;

import org.apache.openejb.api.LocalClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Demonstrates local client injection. For more information see:
 * <a href="http://openejb.apache.org/3.0/local-client-injection.html">
 * OpenEJB: Local Client Injection</a> and 
 * <a href="https://blogs.apache.org/openejb/entry/example_testing_transaction_rollback">
 * OpenEJB Example: Testing Transaction Rollback</a>
 * @author LeifOO
 *
 */
@LocalClient
public class CrudServiceTest extends OpenEjbTest {
  
  private static final String  DIRECTOR_JOEL_COEN      = "Joel Coen";
  private static final String  THE_BIG_LEBOWSKI_TITLE  = "The Big Lebowski";
  private static final Integer THE_BIG_LEBOWSKI_YEAR   = 1992;
  private static final String  THE_BIG_LEBOWSKI_PLOT   =
    "\"Dude\" Lebowski, mistaken for a millionaire Lebowski, seeks restitution for his " +
    "ruined rug and enlists his bowling buddies to help get it.";
    
  private static final String  THE_WALL_DIRECTOR = "Alan Parker";
  private static final String  THE_WALL_TITLE    = "The Wall";
  private static final Integer THE_WALL_YEAR     = 1999;
  private static final String  THE_WALL_PLOT     = 
    "A troubled rock star descends into madness " +
    "in the midst of his physical and social isolation from everyone.";

  
  @EJB
  private CrudService crudService;

  @Resource
  private UserTransaction userTransaction;

  @PersistenceContext
  private EntityManager entityManager;

	
	private Integer fargoId;
  private Integer theBigLebowskiId;
  private Integer reservoirDogsId;

	
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
	  //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
		contextProperties.put("log4j.category.no.knowit.crud", "debug");
    contextProperties.put("log4j.category.no.knowit.testsupport", "debug");
    super.beforeSuite();
	}

  @Override
  @BeforeClass
  public void setupClass() throws Exception {
    super.setupClass();
    
    getInitialContext().bind("inject", this);
    
    
    // Delete all movies
    crudService.remove(Movie.class);
    assert crudService.find(Movie.class).size() == 0 : "List.size():";
    
    // Persist 3 movies
    ArrayList<Movie> movies = new ArrayList<Movie>();

    movies.add(new Movie(DIRECTOR_JOEL_COEN, THE_BIG_LEBOWSKI_TITLE, THE_BIG_LEBOWSKI_YEAR, "..."));
    
    movies.add(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992, 
      "After a simple jewelery heist goes terribly wrong, the surviving criminals begin " +
      "to suspect that one of them is a police informant."));
    
    movies.add(new Movie(DIRECTOR_JOEL_COEN, "Fargo", 1996, 
      "Jerry Lundegaard's inept crime falls apart due to his and his henchmen's bungling " +
      "and the persistent police work of pregnant Marge Gunderson."));
    
    movies.add(new Movie("Lewis Milestone", "All Quiet on the Western Front", 1930, 
      "One of the most powerful anti-war statements ever put on film, this gut-wrenching story " +
      "concerns a group of friends who join the Army during World War I and are assigned to the " +
      "Western Front, where their fiery patriotism is quickly turned to horror and misery by the " +
      "harsh realities of combat."));
      
    movies = (ArrayList<Movie>) crudService.persist(movies);
    
    theBigLebowskiId = movies.get(0).getId();
    assert theBigLebowskiId != null;
    
    reservoirDogsId = movies.get(1).getId();
    assert reservoirDogsId != null;

    fargoId = movies.get(2).getId();
    assert fargoId != null;

    assert crudService.find(Movie.class).size() == 4 : "List.size(): expected 4";
  }

	@Override
	@AfterClass
	public void cleanupClass() throws Exception {
    crudService.remove(Movie.class);
    assert crudService.find(Movie.class).size() == 0 : "Expected Movie list size 0 after cleanup"; 
		super.cleanupClass();
	}
	
	@Test
	public void create() throws Exception {
		Movie theWall = crudService.persist(
		  new Movie(THE_WALL_DIRECTOR, THE_WALL_TITLE, THE_WALL_YEAR, THE_WALL_PLOT));
		
		Assert.assertNotNull(theWall, "crudService.persist: movie was null");
		Assert.assertNotNull(theWall.getId(), "movie.getId: movie.id was null");
		
    assert crudService.find(
      new Movie(null, THE_WALL_TITLE, null), true, false).size() == 1 : "List.size():";
	}

	@Test
	public void read() throws Exception {
		Movie movie = crudService.find(Movie.class, theBigLebowskiId);
		Assert.assertNotNull(movie, "crudService.read: Did not find movie with id: " + theBigLebowskiId);
		Assert.assertEquals(movie.getTitle(), THE_BIG_LEBOWSKI_TITLE);
	}
	
	@Test
	public void update() throws Exception {
    Movie movie = crudService.find(Movie.class, theBigLebowskiId);
    movie.setPlot(THE_BIG_LEBOWSKI_PLOT);
		movie = crudService.merge(movie);
		Assert.assertEquals(movie.getPlot(), THE_BIG_LEBOWSKI_PLOT);
	}
	
	@Test
	public void delete() throws Exception {
		crudService.remove(Movie.class, reservoirDogsId);
		Assert.assertNull(crudService.find(Movie.class, reservoirDogsId));
	}

	@Test
	public void createOrUpdate() throws Exception {
		// Create
		Movie movie = crudService.store(new Movie("Martin Scorsese", "Shine a Light", 2007));
		Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
		
		// Modify and update
		movie.setPlot("Words greatest rockband meets words greatest director - finally!");
		crudService.store(movie);
	}
	
	@Test
	public void findAll() throws Exception {
	  // Already tested in setupClass...
		List<Movie> allMovies = crudService.find(Movie.class);
  	assert allMovies.size() > 0 : "Did not find any movies";
	}

	@Test
	public void findByExample() throws Exception {
		Movie exampleMovie = new Movie();
		exampleMovie.setDirector("Joel%");
		exampleMovie.setYear(1930);
		List<Movie> exampleMovies = crudService.find(exampleMovie, false, true);
  	Assert.assertEquals(exampleMovies.size(), 3, "List.size()");
	}
	
	@Test(dependsOnMethods={ "findByExample" })
	public void deleteByExample() throws Exception {

    Movie exampleMovie = new Movie();
    exampleMovie.setDirector("Joel Coen");
    exampleMovie.setYear(1930);
	  userTransaction.begin();
	  try {
	    crudService.remove(exampleMovie, true);
	    List<Movie> movies = crudService.find(exampleMovie, false, true);
	    Assert.assertEquals(movies.size(), 0, "List.size()");
	  }
	  finally {
	    userTransaction.rollback();
	  }
	}
}
