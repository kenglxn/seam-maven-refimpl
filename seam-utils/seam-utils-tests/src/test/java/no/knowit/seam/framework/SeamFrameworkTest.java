package no.knowit.seam.framework;

import java.util.List;

import no.knowit.crud.CrudService;
import no.knowit.seam.openejb.mock.SeamOpenEjbTest;
import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.seam.framework.MovieHome;
import no.knowit.testsupport.seam.framework.MovieList;

import org.jboss.seam.Component;
import org.jboss.seam.core.Conversation;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class SeamFrameworkTest extends SeamOpenEjbTest {

  private Integer reservoirDogsMovieId;
  
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
    contextProperties.put("log4j.category.no.knowit.seam.seamframework", "debug");
    super.beforeSuite();
	}
	
	@Override
	@BeforeClass
	public void setupClass() throws Exception {
		super.setupClass();
		
    CrudService crudService = (CrudService)getInitialContext().lookup("crudService/Local");
    crudService.remove(Movie.class);
    assert crudService.findWithType(Movie.class).size() == 0 : "Expected Movie list size 0 before tests";
    
		crudService.persist(Movie.with()
      .director("Alan Parker")
      .title("The Wall")
      .year(1999)
      .build());
	}
	
  @Override
  @AfterClass
	public void cleanupClass() throws Exception {
    CrudService crudService = (CrudService)getInitialContext().lookup("crudService/Local");
    crudService.remove(Movie.class);
    assert crudService.findWithType(Movie.class).size() == 0 : "Expected Movie list size 0 after cleanup"; 

    super.cleanupClass();
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
				
				Movie.with(movieHome.getInstance())
          .director("Joel Coen")
          .title("Fargo")
          .year(1996).build();
				
//				movieHome.getInstance().setDirector("Joel Coen");
//				movieHome.getInstance().setTitle("Fargo");
//				movieHome.getInstance().setYear(1996);
				
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
			
      @SuppressWarnings("unchecked")
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
