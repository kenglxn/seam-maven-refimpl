package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@NamedQueries({
  @NamedQuery(name = "Movie.all", query = "from Movie m"),
  @NamedQuery(name = "Movie.byDirectorOrTitle",
      query = "from Movie m where m.director = :director or m.title = :title")
})

@NamedNativeQuery(name = "Movie.sqlAll", query = "select * from movie m", resultClass = Movie.class)

public class Movie implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public static final String ALL_MOVIES = "Movie.all";
  public static final String MOVIES_BY_DIRECTOR_OR_TITLE = "Movie.byDirectorOrTitle";
  public static final String SQL_ALL_MOVIES = "Movie.sqlAll";

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer id;

  @Version
  private Long version;

  @org.hibernate.annotations.Index(name="idx_director")
  @Column(nullable = false, length = 50)
  @NotNull
  @Length(max = 50)
  private String director;

  @Column(unique = true, nullable = false, length = 60)
  @NotNull
  @Length(max = 60)
  private String title;

  @Column(nullable = false)
  @NotNull
  private Integer year;

  @Lob
  private String plot;

  public Movie() {
    // NOTE: Default constructor must be public for javax.el
    // I want to use a Builder instead of polluting code with setters/getters!
  }

  public static Builder with() {
    return new Builder();
  }

  public static Builder with(final Movie movie) {
    if(movie == null) {
      throw new IllegalArgumentException("movie was null");
    }
    return new Builder(movie);
  }

  public Integer getId() {
    return id;
  }

  public Long getVersion() {
    return version;
  }

  public String getDirector() {
    return director;
  }

  public String getTitle() {
    return title;
  }

  public Integer getYear() {
    return year;
  }

  public String getPlot() {
    return plot;
  }

  // Need public setters for javax.el
  // I want to use a Builder instead of polluting code with setters/getters!
  public void setDirector(final String director) {
    this.director = director;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public void setYear(final Integer year) {
    this.year = year;
  }

  public void setPlot(final String plot) {
    this.plot = plot;
  }

  public static class Builder{
    private final Movie movie;

    private Builder() {
      movie = new Movie();
    }

    private Builder(final Movie movie) {
      this.movie = movie;
    }

    public Builder director(final String director) {
      movie.director = director;
      return this;
    }

    public Builder title(final String title) {
      movie.title = title;
      return this;
    }

    public Builder year(final int year) {
      movie.year = year;
      return this;
    }

    public Builder plot(final String plot) {
      movie.plot = plot;
      return this;
    }

    public Movie build() {
      return movie;
    }
  }
}
