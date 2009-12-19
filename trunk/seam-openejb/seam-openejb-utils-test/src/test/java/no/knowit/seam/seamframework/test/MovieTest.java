package no.knowit.seam.seamframework.test;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.jboss.seam.Component;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.mock.AbstractSeamTest.FacesRequest;


import no.knowit.crud.CrudService;
import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.model.Movie;
import no.knowit.seam.openejb.mock.SeamTest;
import no.knowit.seam.seamframework.MovieHome;
import no.knowit.seam.seamframework.MovieList;

public class MovieTest extends SeamTest {

  private static final LogProvider log = Logging.getLogProvider(MovieTest.class);
  
  private Integer reservoirDogsMovieId;
  
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
		 contextProperties.put("log4j.category.no.knowit.seam.seamframework.test", "debug");
		 super.beforeSuite();
	}
	
	@Override
	@BeforeClass
	public void setupClass() throws Exception {
		super.setupClass();
		
		CrudService crudService = (CrudService)initialContext.lookup("crudService/Local");
		crudService.persist(new Movie("Alan Parker", "The Wall", 1999));

	}
	
	@Test
	public void newMovies() throws Exception {
		
		new FacesRequest() {
			
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid();
				
				MovieHome movieHome = getComponentInstanceWithAsserts("movieHome", MovieHome.class);
//				MovieHome movieHome = (MovieHome)Component.getInstance("movieHome");
				
				movieHome.clearInstance();
				movieHome.getInstance().setDirector("Joel Coen");
				movieHome.getInstance().setTitle("Fargo");
				movieHome.getInstance().setYear(1996);
				String result = movieHome.persist();
				Assert.assertEquals(result, "persisted");
				Conversation.instance().end();
			}

		}.run();
		
		new FacesRequest() {
			
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid();
				
				invokeMethod( "#{movieHome.clearInstance}" );
				setValue("#{movieHome.instance.director}", "Quentin Tarantino");
				setValue("#{movieHome.instance.title}", "Reservoir Dogs");
				setValue("#{movieHome.instance.year}", 1992);
				Object result = invokeMethod( "#{movieHome.persist}" );
				Assert.assertEquals(result, "persisted", "Persist failed!");
				
				invokeMethod( "#{movieHome.clearInstance}" );
				setValue("#{movieHome.instance.director}", "Joel Coen");
				setValue("#{movieHome.instance.title}", "The Big Lebowski");
				setValue("#{movieHome.instance.year}", 1998);
				result = invokeMethod( "#{movieHome.persist}" );
				Assert.assertEquals(result, "persisted");

				Conversation.instance().end();
			}
		}.run();
	}

	@Test(dependsOnMethods={ "newMovies" })
	public void listMovies() throws Exception {
		
		new FacesRequest() {
			
	    @Override
	    protected void updateModelValues() throws Exception {
				setValue("#{movieList.movie.director}", "");
				setValue("#{movieList.movie.title}", "");
	    }
	    
      @Override
      protected void renderResponse() throws Exception {
				MovieList movieList = (MovieList)Component.getInstance("movieList");
      	List<Movie> list = movieList.getResultList();
      	Assert.assertEquals(list.size(), 4, "List.size()");
      }
		}.run();
	}

	
	@Test(dependsOnMethods={ "listMovies" })
	public void findMovie() throws Exception {
		
		new FacesRequest() {
      @Override
      protected void updateModelValues() throws Exception {
				setValue("#{movieList.movie.director}", "Quentin Tarantino");
      }
      
			@Override
			protected void invokeApplication() throws Exception {
			}
			
      @Override
      protected void renderResponse() throws Exception {
      	List<Movie> list = (List<Movie>) invokeMethod( "#{movieList.getResultList}" );
      	Assert.assertEquals(list.size(), 1, "List.size()");
      	Movie movie = list.get(0);
      	Assert.assertEquals(movie.getDirector(), "Quentin Tarantino", "Movie.getTitle()");
      	
      	reservoirDogsMovieId = movie.getId();
      }
		}.run();
	}

	@Test(dependsOnMethods={ "findMovie" })
	public void editMovie() throws Exception {
		new FacesRequest() {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid();
				setValue("#{movieHome.movieId}", reservoirDogsMovieId);
				invokeMethod( "#{movieHome.wire}" );
				setValue("#{movieHome.instance.director}", "Joel Coen Edited");
				Object result = invokeMethod( "#{movieHome.update}" );
				Assert.assertEquals(result, "updated", "Update failed!");
				Conversation.instance().end();
			}
		}.run();
	}
	
	@Test(dependsOnMethods={ "editMovie" })
	public void deleteMovie() throws Exception {
		new FacesRequest() {
			@Override
			protected void invokeApplication() throws Exception {
				Conversation.instance().begin();
				assert !isSessionInvalid();
				setValue("#{movieHome.movieId}", reservoirDogsMovieId);
				invokeMethod( "#{movieHome.wire}" );
				Object result = invokeMethod( "#{movieHome.remove}" );
				Assert.assertEquals(result, "removed", "Remove failed!");
				Conversation.instance().end();
			}
		}.run();
	}
	
}