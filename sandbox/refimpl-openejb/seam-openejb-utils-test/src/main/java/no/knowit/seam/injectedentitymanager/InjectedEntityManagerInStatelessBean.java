package no.knowit.seam.injectedentitymanager;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import no.knowit.seam.model.Movie;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateless
@Name("injectedEntityManagerInStatelessBean")
@Scope(ScopeType.STATELESS)

public class InjectedEntityManagerInStatelessBean implements InjectedEntityManagerInStateless {

	@In
	private EntityManager entityManager;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public void addMovie(Movie movie) {
		entityManager.persist(movie);
	}
	
	@Override
	public void deleteMovie(Movie movie) {
		entityManager.remove(movie);
	}
	
	@Override
	public void deleteAllMovies() {
		List<Movie> list = getMovies();
		for (Movie movie : list) {
			deleteMovie(movie);
		}
	}

	@Override
	public List<Movie> getMovies() {
		
		if(entityManager != null) {
			Query query = entityManager.createQuery("select m from Movie as m");
			return query.getResultList();
		}
		return null;
	}

	@Override
	public Movie findMovieByTitle(String title) {
		Query query = entityManager
			.createQuery("from Movie m where m.title=:title")
			.setParameter("title", title);
		
		Movie result = null;
		try {
			result = (Movie)query.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;		
	}
}
