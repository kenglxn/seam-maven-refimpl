package no.knowit.seam.seamframework;

import java.util.Arrays;

import no.knowit.seam.model.Movie;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("movieList")
public class MovieList extends EntityQuery<Movie> {

	private static final long serialVersionUID = -2035337519937136834L;

	private static final String EJBQL = "select movie from Movie movie";

	private static final String[] RESTRICTIONS = {
			"lower(movie.director) like lower(concat(#{movieList.movie.director},'%'))",
			"lower(movie.title) like lower(concat(#{movieList.movie.title},'%'))", };

	private Movie movie = new Movie();

	public MovieList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Movie getMovie() {
		return movie;
	}
}
