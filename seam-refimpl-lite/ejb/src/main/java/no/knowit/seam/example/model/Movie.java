package no.knowit.seam.example.model;

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
	}

	public Movie(final String director, final String title, Integer year) {
		this.director = director;
		this.title = title;
		this.year = year;
	}

	public Movie(final String director, final String title, Integer year, final String plot) {
		this.director = director;
		this.title = title;
		this.year = year;
		this.plot = plot;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getDirector() {
		return this.director;
	}

	public void setDirector(final String director) {
		this.director = director;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPlot() {
		return this.plot;
	}

	public void setPlot(final String plot) {
		this.plot = plot;
	}

}
