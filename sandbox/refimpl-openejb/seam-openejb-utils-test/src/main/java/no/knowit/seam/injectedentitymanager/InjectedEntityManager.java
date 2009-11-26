package no.knowit.seam.injectedentitymanager;

import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import no.knowit.seam.model.Movie;

@Local
public interface InjectedEntityManager {
	public EntityManager getEntityManager();
	public void addMovie(Movie movie) throws Exception;
	public void deleteMovie(Movie movie) throws Exception;
	public List<Movie> getMovies() throws Exception;
}
