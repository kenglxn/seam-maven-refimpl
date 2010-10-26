package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Movie implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  
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
  
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Movie movie) {
    if(movie == null) {
      throw new IllegalArgumentException("movie was null");
    }
    return new Builder(movie);
  }

  public Integer getId() {
    return this.id;
  }

  public Long getVersion() {
    return this.version;
  }

  public String getDirector() {
    return this.director;
  }

  public String getTitle() {
    return this.title;
  }

  public Integer getYear() {
    return this.year;
  }

  public String getPlot() {
    return this.plot;
  }
  
  // Need public setters for javax.el
  // I want to use a Builder instead of polluting code with setters/getters!
  public void setDirector(String director) {
    this.director = director;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public void setPlot(String plot) {
    this.plot = plot;
  }

  public static class Builder{
    private final Movie movie;
    
    private Builder() {
      movie = new Movie();
    }
    
    private Builder(Movie movie) {
      this.movie = movie;
    }
    
    public Builder withDirector(final String director) {
      movie.director = director;
      return this;
    }
    
    public Builder withTitle(final String title) {
      movie.title = title;
      return this;
    }
    
    public Builder withYear(final int year) {
      movie.year = year;
      return this;
    }
    
    public Builder withPlot(final String plot) {
      movie.plot = plot;
      return this;
    }
    
    public Movie build() {
      return movie;
    }
  }
}
