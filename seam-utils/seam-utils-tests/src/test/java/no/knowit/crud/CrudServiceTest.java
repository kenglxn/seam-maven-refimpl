package no.knowit.crud;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.UserTransaction;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.model.NamedEntity;

import org.apache.log4j.Logger;
import org.apache.openejb.api.LocalClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Demonstrates local client injection and user transaction for testing transaction rollback.
 * For more information see: <a href="http://openejb.apache.org/3.0/local-client-injection.html">
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

  private static final Logger log = Logger.getLogger(CrudServiceTest.class);

  @EJB
  private CrudService crudService;

  @Resource
  private UserTransaction userTransaction;

  private Integer fargoId;
  private Integer theBigLebowskiId;
  private Integer reservoirDogsId;
  private Movie allQuietOnTheWesternFront;


  @Override
  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.crud", "debug");
    contextProperties.put("log4j.category.no.knowit.testsupport", "debug");
    super.beforeSuite();
  }

  @Override
  @BeforeClass
  public void setupClass() throws Exception {
    super.setupClass();

    container.getContext().bind("inject", this);


    // Delete all movies
    crudService.remove(Movie.class);
    assert crudService.find(Movie.class).size() == 0 : "List.size():";

    // Persist 4 movies
    ArrayList<Movie> movies = new ArrayList<Movie>();

    movies.add(Movie.with()
        .director(DIRECTOR_JOEL_COEN)
        .title(THE_BIG_LEBOWSKI_TITLE)
        .year(THE_BIG_LEBOWSKI_YEAR)
        .plot("...")
        .build());

    movies.add(Movie.with()
        .director("Quentin Tarantino")
        .title("Reservoir Dogs")
        .year(1992)
        .plot("After a simple jewelery heist goes terribly wrong, the surviving criminals begin " +
        "to suspect that one of them is a police informant.")
        .build());

    movies.add(Movie.with()
        .director(DIRECTOR_JOEL_COEN)
        .title("Fargo")
        .year(1996)
        .plot("Jerry Lundegaard's inept crime falls apart due to his and his henchmen's bungling " +
        "and the persistent police work of pregnant Marge Gunderson.")
        .build());

    movies.add(Movie.with()
        .director("Lewis Milestone")
        .title("All Quiet on the Western Front")
        .year(1930)
        .plot("One of the most powerful anti-war statements ever put on film, this gut-wrenching " +
            "story concerns a group of friends who join the Army during World War I and are " +
            "assigned to the Western Front, where their fiery patriotism is quickly turned to " +
        "horror and misery by the harsh realities of combat.")
        .build());

    movies = (ArrayList<Movie>) crudService.persist(movies);

    theBigLebowskiId = movies.get(0).getId();
    assert theBigLebowskiId != null;

    reservoirDogsId = movies.get(1).getId();
    assert reservoirDogsId != null;

    fargoId = movies.get(2).getId();
    assert fargoId != null;

    allQuietOnTheWesternFront = movies.get(3);
    assert allQuietOnTheWesternFront.getId() != null;

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
    final Movie theWall = crudService.persist(Movie.with()
        .director(THE_WALL_DIRECTOR)
        .title(THE_WALL_TITLE)
        .year(THE_WALL_YEAR)
        .plot(THE_WALL_PLOT)
        .build());

    Assert.assertNotNull(theWall, "crudService.persist: movie was null");
    Assert.assertNotNull(theWall.getId(), "movie.getId: movie.id was null");

    assert crudService.find(Movie.with().title(THE_WALL_TITLE).build(),
        true, false).size() == 1 : "List.size():";
  }

  @Test
  public void read() throws Exception {
    final Movie movie = crudService.find(Movie.class, theBigLebowskiId);
    Assert.assertNotNull(movie, "crudService.read: Did not find movie with id: " + theBigLebowskiId);
    Assert.assertEquals(movie.getTitle(), THE_BIG_LEBOWSKI_TITLE);
  }

  @Test
  public void update() throws Exception {
    Movie movie = crudService.find(Movie.class, theBigLebowskiId);
    assert movie != null : "Movie was null";
    movie = crudService.merge(Movie.with(movie).plot(THE_BIG_LEBOWSKI_PLOT).build());
    Assert.assertEquals(movie.getPlot(), THE_BIG_LEBOWSKI_PLOT);
  }

  @Test
  public void delete() throws Exception {
    crudService.remove(Movie.class, reservoirDogsId);
    Assert.assertNull(crudService.find(Movie.class, reservoirDogsId));

    final Movie aMovie = crudService.persist(Movie.with()
        .director("Director")
        .title("Title")
        .year(2011)
        .plot("Plot")
        .build());
    crudService.remove(aMovie);
    assert crudService.find(Movie.class, aMovie.getId()) == null;
  }

  @Test
  public void createOrUpdate() throws Exception {
    // Create
    Movie movie = crudService.store(Movie.with()
        .director("Martin Scorsese")
        .title("Shine a Light")
        .year(2007)
        .build());
    Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");

    // Modify and update
    movie = crudService.store(Movie.with(movie)
        .plot("Words greatest rockband meets words greatest director - finally!")
        .build());
    assert movie != null : "Movie was null";
  }

  @Test
  public void findAll() throws Exception {
    // Already tested in setupClass...
    final List<Movie> allMovies = crudService.find(Movie.class);
    assert allMovies.size() > 0 : "Did not find any movies";
  }

  @Test
  public void findByExample() throws Exception {
    final Movie exampleMovie = Movie.with()
    .director("Joel%")
    .year(1930)
    .build();
    final List<Movie> exampleMovies = crudService.find(exampleMovie, false, true);
    Assert.assertEquals(exampleMovies.size(), 3, "List.size()");
  }

  @Test
  public void deleteByExample() throws Exception {

    final int expectedCount = crudService.count(Movie.class);
    log.debug("deleteByExample: Also testing transaction Rollback. # of movies before transaction: " + expectedCount);

    final Movie exampleMovie = Movie.with()
    .director("Joel Coen")
    .year(1930)
    .build();

    userTransaction.begin();
    try {
      crudService.remove(exampleMovie, true);
      final List<Movie> movies = crudService.find(exampleMovie, false, true);
      Assert.assertEquals(movies.size(), 0, "List.size()");
    }
    finally {
      userTransaction.rollback();
    }
    log.debug("deleteByExample: # of movies after transaction rollback: " + expectedCount);
    Assert.assertEquals(crudService.count(Movie.class), expectedCount, "List.size()");
  }

  @Test
  public void shouldQueryANamedEntity() throws Exception {
    crudService.persist(new NamedEntity(100));
    assert crudService.find(NamedEntity.class).size() > 0;
    assert crudService.count(NamedEntity.class) > 0;
    crudService.remove(NamedEntity.class);
  }

  @Test
  public void getReference() {
    final Movie fargo = crudService.getReference(Movie.class, fargoId);
    assert fargo != null;
  }

  @Test
  public void refresh() {
    Movie fargo = crudService.find(Movie.class, fargoId);
    assert fargo != null;

    final String title = fargo.getTitle();
    fargo.setTitle("Moified title");

    fargo = crudService.refresh(fargo);
    assert fargo.getTitle().equals(title) : "Expected same title before and after refresh";

    fargo.setTitle("Moified title");
    fargo = crudService.refresh(Movie.class, fargoId);
    assert fargo.getTitle().equals(title) : "Expected same title before and after refresh";
  }

}
