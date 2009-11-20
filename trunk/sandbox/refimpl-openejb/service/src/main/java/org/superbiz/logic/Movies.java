package org.superbiz.logic;

import org.superbiz.model.Movie;

import java.util.List;

import javax.ejb.Local;

@Local
public interface Movies {
	void addMovie(Movie movie) throws Exception;

	void deleteMovie(Movie movie) throws Exception;

	List<Movie> getMovies() throws Exception;
}
