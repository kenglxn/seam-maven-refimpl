package no.knowit.seam.seamframework;

import javax.persistence.EntityManager;

import no.knowit.seam.model.Movie;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;

@Name("movieHome")
//@Scope(ScopeType.CONVERSATION)
//@Conversational

public class MovieHome extends EntityHome<Movie> {

	private static final long serialVersionUID = -4312145956140755980L;

	public void setMovieId(Integer id) {
		setId(id);
	}

	public Integer getMovieId() {
		return (Integer) getId();
	}

	@Override
	protected Movie createInstance() {
		Movie movie = new Movie();
		return movie;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Movie getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	
//  @Factory("movie")
//  public Movie initMovie() { 
//  	return getInstance(); 
//  }

}
