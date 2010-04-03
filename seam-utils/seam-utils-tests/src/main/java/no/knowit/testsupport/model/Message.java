package no.knowit.testsupport.model;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Name("message")
@Scope(EVENT)
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long version;
	private String title;
	private String text;
	private Boolean read;
	private Date datetime;

	public Message(String title, String text, Boolean read, Date datetime) {
		this.title = title;
		this.text = text;
		this.read = read;
		this.datetime = datetime;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getVersion() {
		return version;
	}

  @Column(unique=true)
	@NotNull
	@Length(max = 100)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotNull
	@Lob
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@NotNull
	public boolean isRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	@NotNull
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
}
