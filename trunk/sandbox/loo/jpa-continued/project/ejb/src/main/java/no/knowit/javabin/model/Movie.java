package no.knowit.javabin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class Movie implements java.io.Serializable {

	private static final long serialVersionUID = 1515968741131218968L;
	
  @Id
  @GeneratedValue(strategy = IDENTITY)
	private Integer id;
  
  @Version
	private Long version;

  @Column(length=50, nullable=false)
  @NotEmpty
	private String director;

  @Column(length=60, unique = true, nullable=false)
  @NotEmpty
	private String title;

  @Column(nullable=false)
  @NotNull 
  private Integer year;

	public Movie() {
	}

	public Movie(String director, String title, Integer year) {
		this.director = director;
		this.title = title;
		this.year = year;
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

	public void setDirector(String director) {
		this.director = director;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
}
