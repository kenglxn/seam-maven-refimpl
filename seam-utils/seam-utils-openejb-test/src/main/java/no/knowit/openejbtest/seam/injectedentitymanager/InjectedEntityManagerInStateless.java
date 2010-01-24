package no.knowit.openejbtest.seam.injectedentitymanager;

import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import no.knowit.openejbtest.model.Movie;

@Local
public interface InjectedEntityManagerInStateless {
	public EntityManager getEntityManager();
	public void addMovie(Movie movie);
	public void deleteMovie(Movie movie);
	public void deleteAllMovies();
	public List<Movie> getMovies();
	public Movie findMovieByTitle(String title);
}
