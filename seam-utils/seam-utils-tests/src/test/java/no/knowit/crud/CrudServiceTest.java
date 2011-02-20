package no.knowit.crud;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.UserTransaction;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.domain.Product;
import no.knowit.testsupport.domain.PurchaseOrder;
import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.model.NamedEntity;

import org.apache.log4j.Logger;
import org.apache.openejb.api.LocalClient;
import org.hibernate.LazyInitializationException;
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

  private static final Logger log = Logger.getLogger(CrudServiceTest.class);

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

  private static final String ALL_QUIET_ON_THE_WESTERN_FRONT = "All Quiet on the Western Front";

  @EJB
  private CrudService crudService;

  @Resource
  private UserTransaction userTransaction;

  private Integer fargoId;
  private Integer theBigLebowskiId;
  private Integer reservoirDogsId;
  private Movie allQuietOnTheWesternFront;
  private List<Movie> moviesUnderTest = new ArrayList<Movie>();

  private Product productA;
  private Product productB;
  private PurchaseOrder purchaseOrder;


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
    crudService.removeWithType(Movie.class);
    assert crudService.findWithType(Movie.class).size() == 0 : "List.size():";

    // Persist 4 movies
    moviesUnderTest.add(Movie.with()
        .director(DIRECTOR_JOEL_COEN)
        .title(THE_BIG_LEBOWSKI_TITLE)
        .year(THE_BIG_LEBOWSKI_YEAR)
        .plot("...")
        .build());

    moviesUnderTest.add(Movie.with()
        .director("Quentin Tarantino")
        .title("Reservoir Dogs")
        .year(1992)
        .plot("After a simple jewelery heist goes terribly wrong, the surviving criminals begin " +
        "to suspect that one of them is a police informant.")
        .build());

    moviesUnderTest.add(Movie.with()
        .director(DIRECTOR_JOEL_COEN)
        .title("Fargo")
        .year(1996)
        .plot("Jerry Lundegaard's inept crime falls apart due to his and his henchmen's bungling " +
        "and the persistent police work of pregnant Marge Gunderson.")
        .build());

    moviesUnderTest.add(Movie.with()
        .director("Lewis Milestone")
        .title(ALL_QUIET_ON_THE_WESTERN_FRONT)
        .year(1930)
        .plot("One of the most powerful anti-war statements ever put on film, this gut-wrenching " +
            "story concerns a group of friends who join the Army during World War I and are " +
            "assigned to the Western Front, where their fiery patriotism is quickly turned to " +
        "horror and misery by the harsh realities of combat.")
        .build());

    crudService.persistCollection(moviesUnderTest);

    theBigLebowskiId = moviesUnderTest.get(0).getId();
    assert theBigLebowskiId != null;

    reservoirDogsId = moviesUnderTest.get(1).getId();
    assert reservoirDogsId != null;

    fargoId = moviesUnderTest.get(2).getId();
    assert fargoId != null;

    allQuietOnTheWesternFront = moviesUnderTest.get(3);
    assert allQuietOnTheWesternFront.getId() != null;

    assert crudService.findWithType(Movie.class).size() == 4 : "List.size(): expected 4";
  }

  @Override
  @AfterClass
  public void cleanupClass() throws Exception {
    crudService.removeWithType(Movie.class);
    assert crudService.findWithType(Movie.class).size() == 0 : "Expected Movie list size 0 after cleanup";
    super.cleanupClass();
  }

  @Test
  public void persist() throws Exception {
    final Movie theWall = crudService.persist(Movie.with()
        .director(THE_WALL_DIRECTOR)
        .title(THE_WALL_TITLE)
        .year(THE_WALL_YEAR)
        .plot(THE_WALL_PLOT)
        .build());

    Assert.assertNotNull(theWall, "crudService.persist: movie was null");
    Assert.assertNotNull(theWall.getId(), "movie.getId: movie.id was null");

    assert crudService.findByExample(Movie.with().title(THE_WALL_TITLE).build(),
        true, false).size() == 1 : "List.size():";
  }


  @Test
  public void findByPrimaryKey() throws Exception {
    final Movie movie = crudService.find(Movie.class, theBigLebowskiId);
    Assert.assertNotNull(movie, "crudService.read: Did not find movie with id: " + theBigLebowskiId);
    Assert.assertEquals(movie.getTitle(), THE_BIG_LEBOWSKI_TITLE);
  }

  @Test
  public void merge() throws Exception {
    Movie movie = crudService.find(Movie.class, theBigLebowskiId);
    assert movie != null : "Movie was null";
    movie = crudService.merge(Movie.with(movie).plot(THE_BIG_LEBOWSKI_PLOT).build());
    Assert.assertEquals(movie.getPlot(), THE_BIG_LEBOWSKI_PLOT);
  }

  @Test
  public void mergeCollection() {
    final int i = 0;
    for (Movie movie : moviesUnderTest) {
      movie = crudService.refresh(moviesUnderTest.get(0));
      moviesUnderTest.set(i, movie);
    }

    final String aNewPlot = "A new plot ...";
    moviesUnderTest.get(0).setPlot(aNewPlot);

    // Need a cast to use the *Collection methods
    moviesUnderTest = (List<Movie>) crudService.mergeCollection(moviesUnderTest);

    final Movie mergedMovie = crudService.find(Movie.class, moviesUnderTest.get(0).getId());
    assert aNewPlot.equals(mergedMovie.getPlot()) : "Expected: " + aNewPlot;
  }

  @Test
  public void remove() throws Exception {
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
  public void store() throws Exception {
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
  void storeCollection() {
    final String yetAnotherPlot = "Yet anoter plot ...";
    List<Movie> moviesToStore = new ArrayList<Movie>();

    final Movie movieToMerge = Movie
    .with(crudService.refresh(moviesUnderTest.get(0)))
    .plot(yetAnotherPlot)
    .build();

    final Movie movieToPersist = Movie.with()
    .director("Robert Rossen")
    .title("All the King's Men")
    .year(1949)
    .plot("Based on the Pulitzer Prize-winning novel by Robert Penn Warren.")
    .build();

    moviesToStore.add(movieToMerge);
    moviesToStore.add(movieToPersist);

    // Need a cast to use the *Collection methods
    moviesToStore = (List<Movie>) crudService.storeCollection(moviesToStore);

    final Movie mergedMovie = crudService.find(Movie.class, moviesUnderTest.get(0).getId());
    assert yetAnotherPlot.equals(mergedMovie.getPlot()) : "Expected: " + yetAnotherPlot;

    final Integer id = moviesToStore.get(1).getId();
    assert id != null : "Expected id != null";
    assert crudService.find(Movie.class, id) != null : "Expected to find movie with id: " + id;
  }

  @Test
  public void findAll() throws Exception {
    // Already tested in setupClass...
    final List<Movie> allMovies = crudService.findWithType(Movie.class);
    assert allMovies.size() > 0 : "Did not find any movies";
  }

  @Test
  public void findByExample() throws Exception {
    final Movie exampleMovie = Movie.with()
    .director("Joel%")
    .year(1930)
    .build();
    final List<Movie> exampleMovies = crudService.findByExample(exampleMovie, false, true);
    Assert.assertEquals(exampleMovies.size(), 3, "List.size()");
  }

  @Test
  public void findwithQuery() {
    final String jpql = "from Movie m";
    List<Movie> movies;

    movies = crudService.findWithQuery(jpql, null, 0, 2);
    assert movies.size() == 2 : "expected: 2 but was: " + movies.size();

    final String jpqlByDirectorOrTitle = jpql + " where m.director = :director or m.title = :title";
    movies = crudService.findWithQuery(jpqlByDirectorOrTitle, QueryParameter
        .with("director", DIRECTOR_JOEL_COEN)
        .and("title", ALL_QUIET_ON_THE_WESTERN_FRONT)
        .parameters(), -1, -1);
    assert movies.size() > 0 : "expected movies.size() > 0";
  }

  @Test
  public void findWithNamedQuery() {
    List<Movie> movies;
    movies = crudService.findWithNamedQuery(Movie.ALL_MOVIES, null, 0, 2);
    assert movies.size() == 2 : "expected: 2 but was: " + movies.size();

    movies = crudService.findWithNamedQuery(Movie.MOVIES_BY_DIRECTOR_OR_TITLE, QueryParameter
        .with("director", DIRECTOR_JOEL_COEN)
        .and("title", ALL_QUIET_ON_THE_WESTERN_FRONT)
        .parameters(), -1, -1);
    assert movies.size() > 0 : "expected movies.size() > 0";
  }

  @Test
  public void findByNativeQuery() {
    final String sql = "select * from movie m";

    List<?> rawResults;
    rawResults = crudService.findByNativeQuery(sql, CrudService.EMPTY_PARAMETER_MAP, 0, 2);
    assert rawResults.size() == 2 : "expected: 2 but was: " + rawResults.size();

    List<Movie> movies;
    movies = crudService.findByNativeQuery(sql, Movie.class);
    assert movies.size() >= 4 : "expected movies.size() >= 4, got: " + movies.size();

    final String sqlByDirector = sql + " where m.director = :director";
    rawResults = crudService.findByNativeQuery(sqlByDirector,
        QueryParameter.with("director", DIRECTOR_JOEL_COEN).parameters(), -1, -1);
    assert movies.size() > 0 : "expected movies.size() > 0";

    movies = crudService.findByNativeQuery(sqlByDirector, Movie.class,
        QueryParameter.with("director", DIRECTOR_JOEL_COEN).parameters(), -1, -1);
  }

  @Test
  public void findByNamedNativeQuery() {
    List<Movie> movies;
    movies = crudService.findWithNamedQuery(Movie.SQL_ALL_MOVIES);
    assert movies.size() > 0 : "expected movies.size() > 0";

    movies = crudService.findWithNamedQuery(Movie.SQL_ALL_MOVIES, null, -1, -1);
    assert movies.size() > 0 : "expected movies.size() > 0";
  }

  @Test
  public void removeByExample() throws Exception {

    final int expectedCount = crudService.count(Movie.class);
    log.debug("deleteByExample: Also testing transaction Rollback. # of movies before transaction: " + expectedCount);

    final Movie exampleMovie = Movie.with()
    .director("Joel Coen")
    .year(1930)
    .build();

    userTransaction.begin();
    try {
      crudService.removeByExample(exampleMovie, true);
      final List<Movie> movies = crudService.findByExample(exampleMovie, false, true);
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
    assert crudService.findWithType(NamedEntity.class).size() > 0;
    assert crudService.count(NamedEntity.class) > 0;
    crudService.removeWithType(NamedEntity.class);
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

  @Test(expectedExceptions = LazyInitializationException.class)
  public void shouldGetLazyInitializationException() {
    createPurchaseOrderIfNull();

    final PurchaseOrder po = crudService.find(PurchaseOrder.class, purchaseOrder.getId());
    assert crudService.isManaged(po) == false : "Expected unmanaged purchase order entity";
    po.getOrderines().size();
  }

  @Test
  public void shouldNotGetLazyInitializationException() {
    createPurchaseOrderIfNull();

    PurchaseOrder po = crudService.find(PurchaseOrder.class, purchaseOrder.getId());
    assert crudService.isManaged(po) == false : "Expected unmanaged purchase order entity";

    po = crudService.touchRelations(po, 1, "orderLines");
    po.getOrderines().size();
  }

  private void createPurchaseOrderIfNull() {
    if (productA == null) {
      productA = crudService.persist(Product.with().name("Product A").price(10).build());
    }
    if (productB == null) {
      productB = crudService.persist(Product.with().name("Product B").price(15).build());
    }

    if (purchaseOrder == null) {
      purchaseOrder = crudService.persist(
          PurchaseOrder.with("Acme inc")
          .orderLine(productA, 2)
          .orderLine(productB, 10)
          .build()
      );
    }
  }
}
