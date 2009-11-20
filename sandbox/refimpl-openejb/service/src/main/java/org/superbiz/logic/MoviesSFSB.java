package org.superbiz.logic;

import org.superbiz.model.Movie;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import java.util.List;

@Stateful(name = "movies")
public class MoviesSFSB implements Movies {

	// Note: unitName is not needed if we have only one persistence unit
	@PersistenceContext(unitName = "movie-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public void addMovie(Movie movie) throws Exception {
		entityManager.persist(movie);
	}

	public void deleteMovie(Movie movie) throws Exception {
		entityManager.remove(movie);
	}

	public List<Movie> getMovies() throws Exception {
		Query query = entityManager.createQuery("SELECT m from Movie as m");
		return query.getResultList();
	}

}
