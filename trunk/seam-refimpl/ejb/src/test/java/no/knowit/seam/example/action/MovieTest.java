package no.knowit.seam.example.action;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import no.knowit.crud.CrudService;
import no.knowit.seam.example.model.Movie;
import no.knowit.seam.openejb.mock.SeamOpenEjbTest;

import org.jboss.seam.Component;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MovieTest extends SeamOpenEjbTest {

  private static final String  DIRECTOR_JOEL_COEN= "Joel Coen";
  private static final String  THE_WALL_DIRECTOR = "Alan Parker";
  private static final String  THE_WALL_TITLE    = "The Wall";
  private static final Integer THE_WALL_YEAR     = 1992;
  private static final String  THE_WALL_PLOT     = "A troubled rock star descends into madness in the midst of his physical and social isolation from everyone.";
  private Integer              theWallId;
  
  private static final LogProvider log = Logging.getLogProvider(MovieTest.class);

	private CrudService lookupCrudService() throws Exception {
		try {
			Object service = initialContext.lookup("crudService/Local");
			assert service != null : "initialContext.lookup(\"crudService/Local\") returned null";
			assert service instanceof CrudService : "initialContext.lookup(\"crudService/Local\") returned incorrect type";
			return (CrudService) service;
		}
		catch (NamingException e) {
			log.error(e);
			throw(e);
		}
	}
	
	private boolean mockLogin() {
		Credentials credentials = (Credentials)Component.getInstance("org.jboss.seam.security.credentials");
		Identity identity = (Identity)Component.getInstance("org.jboss.seam.security.identity");
		credentials.setUsername("admin");
		identity.addRole("admin");
		return identity.login().equals("loggedIn");
	}

	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		
		//System.out.println("**** MovieTest.beforeSuite()");

		
		// Change some logging, INFO|DEBUG|WARN|ERROR|FATAL
		contextProperties.put("log4j.category.org.jboss.seam.Component", "DEBUG");
		contextProperties.put("log4j.category.org.jboss.seam.transaction", "DEBUG");
		contextProperties.put("log4j.category.org.jboss.seam.mock", "DEBUG");

		contextProperties.put("log4j.category.no.knowit.seam.openejb.mock", "DEBUG");
		
		contextProperties.put("log4j.category.no.knowit.seam.example", "debug");
		
		super.beforeSuite();
	}

	@Override
	@BeforeClass
	public void setupClass() throws Exception {
		super.setupClass();
		
		// Delete all movies
		CrudService crudService = lookupCrudService();
		crudService.remove(new Movie(), true);
		assert crudService.find(Movie.class).size() == 0 : "List.size():";
		
		// Persist 3 movies
		ArrayList<Movie> movies = new ArrayList<Movie>();
		movies.add(new Movie(DIRECTOR_JOEL_COEN, "The Big Lebowski", 1998, 
			"\"Dude\" Lebowski, mistaken for a millionaire Lebowski, seeks restitution for his " +
			"ruined rug and enlists his bowling buddies to help get it."));
		movies.add(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992, 
			"After a simple jewelery heist goes terribly wrong, the surviving criminals begin " +
			"to suspect that one of them is a police informant."));
		movies.add(new Movie(DIRECTOR_JOEL_COEN, "Fargo", 1996, 
			"Jerry Lundegaard's inept crime falls apart due to his and his henchmen's bungling " +
			"and the persistent police work of pregnant Marge Gunderson."));
		
		crudService.persist(movies);
	}


	@Test
	public void newMovie() throws Exception {
		
		new NonFacesRequest("/view/example/MovieList.xhtml") {
      @Override
      protected void renderResponse() throws Exception {
      	int actual = (Integer)getValue("#{movieList.resultList.size}");
      	Assert.assertEquals(actual, 3, "movieList.size:");
      }
		}.run();

		new FacesRequest("/login.xhtml") {
			@Override
			protected void updateModelValues() throws Exception {
				assert !isSessionInvalid() : "Invalid session";
				setValue("#{credentials.username}", "admin");
			}
			
			@Override
			protected void invokeApplication() throws Exception {
				assert invokeMethod("#{authenticator.authenticate}").equals(true) : "Authentication failed";
				assert invokeMethod("#{identity.login}").equals("loggedIn") : "Login failed";
				setOutcome("/view/example/MovieEdit.xhtml");
			}
		}.run();
   
		new FacesRequest("/view/example/MovieEdit.xhtml") {
	    @Override
	    protected void updateModelValues() throws Exception {
				invokeMethod( "#{movieHome.clearInstance}" );
				setValue("#{movieHome.instance.director}", THE_WALL_DIRECTOR);
				setValue("#{movieHome.instance.title}",    THE_WALL_TITLE);
				setValue("#{movieHome.instance.year}",     THE_WALL_YEAR);
	    }

			@Override
			protected void invokeApplication() {
				assert invokeMethod("#{movieHome.persist}").equals("persisted");
				theWallId = (Integer)getValue("#{movieHome.movieId}");
				setOutcome("/view/example/Movie.xhtml");
			}
			
			@Override
			protected void afterRequest() {
				assert isInvokeApplicationComplete();
				assert !isRenderResponseBegun();
			}
		}.run();
		
		new NonFacesRequest("/view/example/Movie.xhtml") {
			@Override
			protected void renderResponse() {
				setValue("#{movieHome.movieId}", theWallId);
				invokeMethod("#{movieHome.wire}");
				assert getValue("#{movieHome.instance.director}").equals(THE_WALL_DIRECTOR);
				assert getValue("#{movieHome.instance.title}").equals(THE_WALL_TITLE);
				assert getValue("#{movieHome.instance.year}").equals(THE_WALL_YEAR);
			}
		}.run();
		
    new FacesRequest() {
			@Override
			protected void invokeApplication() throws Exception {
				assert getValue("#{identity.loggedIn}").equals(true) : "Not logged in";
				assert invokeAction("#{identity.logout}") == null;
				assert getValue("#{identity.loggedIn}").equals(false) : "Logout failed";
			}
		}.run();       
	}

	@Test(dependsOnMethods={ "newMovie" })
	public void editMovie() throws Exception {
		new FacesRequest("/view/example/MovieEdit.xhtml") {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid() : "Invalid session";
				assert mockLogin() : "Login failed";
				setValue("#{movieHome.movieId}", theWallId);
				invokeMethod( "#{movieHome.wire}" );
				setValue("#{movieHome.instance.plot}", THE_WALL_PLOT);
				Object result = invokeMethod( "#{movieHome.update}" );
				Assert.assertEquals(result, "updated", "#{movieHome.update}");
				Conversation.instance().end();
			}
		}.run();
	}
	
	@Test(dependsOnMethods={ "editMovie" })
	public void unauthorizedAccess() throws Exception {
		new FacesRequest("/view/example/MovieEdit.xhtml") {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid() : "Invalid session";
				setValue("#{movieHome.movieId}", theWallId);
				invokeMethod( "#{movieHome.wire}" );
				setValue("#{movieHome.instance.plot}", THE_WALL_PLOT);
				
				try {
					invokeMethod( "#{movieHome.update}" );
					assert false : "Unauthorized access!";
				} 
				catch (javax.el.ELException e) {
					assert e.getCause() instanceof org.jboss.seam.security.NotLoggedInException : 
						"Expected to fail with: 'org.jboss.seam.security.NotLoggedInException'";
					
					log.debug("Passed expected unauthorized access failure: " + e.getMessage());
				}
				finally {
					Conversation.instance().end();
				}
			}
		}.run();
	}

	@Test(dependsOnMethods={ "editMovie" })
	public void persistWithMissingRequiredFieldValue() throws Exception {
		new FacesRequest("/view/example/MovieEdit.xhtml") {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid() : "Invalid session";
				assert mockLogin() : "Login failed";
				invokeMethod("#{movieHome.clearInstance}");
				setValue("#{movieHome.instance.director}", THE_WALL_DIRECTOR);
				setValue("#{movieHome.instance.year}",     THE_WALL_YEAR);
				
				try {
					invokeMethod( "#{movieHome.persist}" );
					assert false : "Movie with missing field value persisted!";
				} 
				catch (javax.el.ELException e) {
					assert e.getCause() instanceof javax.persistence.PersistenceException : 
						"Expected to fail with: 'javax.persistence.PersistenceException'";
					
					log.debug("Passed expected missing required field value failure: " + e.getMessage());
				}
				finally {
					Conversation.instance().end();
				}
			}
		}.run();
	}
	
	
	@Test(dependsOnMethods={ "editMovie" })
	public void duplicateUniqueSecondaryIndex() throws Exception {
		new FacesRequest("/view/example/MovieEdit.xhtml") {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid() : "Invalid session";
				assert mockLogin() : "Login failed";
				invokeMethod("#{movieHome.clearInstance}");
				setValue("#{movieHome.instance.director}", THE_WALL_DIRECTOR);
				setValue("#{movieHome.instance.title}",    THE_WALL_TITLE);
				setValue("#{movieHome.instance.year}",     THE_WALL_YEAR);
				
				try {
					invokeMethod( "#{movieHome.persist}" );
					assert false : "Movie with duplicate title persisted!";
				} 
				catch (javax.el.ELException e) {
					assert e.getCause() instanceof javax.persistence.PersistenceException : 
						"Expected to fail with: 'javax.persistence.PersistenceException'";
					
					log.debug("Passed expected duplicate unique secondary index failure: " + e.getMessage());
				}
				finally {
					Conversation.instance().end();
				}
			}
		}.run();
	}

	@Test(dependsOnMethods={ "duplicateUniqueSecondaryIndex" })
	public void deleteMovie() throws Exception {
		new FacesRequest("/view/example/MovieEdit.xhtml") {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid() : "Invalid session";
				assert mockLogin() : "Login failed";
				setValue("#{movieHome.movieId}", theWallId);
				invokeMethod( "#{movieHome.wire}" );
				Assert.assertEquals(invokeMethod( "#{movieHome.remove}" ), "removed", "#{movieHome.remove}");
				Conversation.instance().end();
			}
		}.run();
	}

	@Test(dependsOnMethods={ "deleteMovie" })
	public void findMoviesDirectedByJoelCoen() throws Exception {
		
		new FacesRequest("/view/example/MovieList.xhtml") {
      @Override
      protected void updateModelValues() throws Exception {
				setValue("#{movieList.movie.director}", DIRECTOR_JOEL_COEN);
      }

      @Override
      protected void renderResponse() throws Exception {
      	List<Movie> list= (List<Movie>) invokeMethod( "#{movieList.getResultList()}" );
      	Assert.assertEquals(list.size(), 2, "#{movieList.getResultList()}");
      	Movie movie = list.get(0);
      	Assert.assertEquals(movie.getDirector(), DIRECTOR_JOEL_COEN, "movie.getDirector()");
      }
		}.run();
	}
}
