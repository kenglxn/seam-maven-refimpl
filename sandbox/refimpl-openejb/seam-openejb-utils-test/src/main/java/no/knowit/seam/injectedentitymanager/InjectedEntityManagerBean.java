package no.knowit.seam.injectedentitymanager;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.knowit.seam.model.Movie;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("injectedEntityManager")
@Stateless
@Scope(ScopeType.STATELESS)
public class InjectedEntityManagerBean implements InjectedEntityManager {

	@In
	private EntityManager entityManager;

	@PostConstruct
	protected void initDatabase() throws Exception {
		addMovie(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992));
		addMovie(new Movie("Joel Coen", "Fargo", 1996));
		addMovie(new Movie("Joel Coen", "The Big Lebowski", 1998));
	}
	
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public void addMovie(Movie movie) throws Exception {
		entityManager.persist(movie);
	}

	@Override
	public void deleteMovie(Movie movie) throws Exception {
		entityManager.remove(movie);
	}

	@Override
	public List<Movie> getMovies() throws Exception {
		
		if(entityManager != null) {
			Query query = entityManager.createQuery("select m from Movie as m");
			return query.getResultList();
		}
		return null;
	}
}
