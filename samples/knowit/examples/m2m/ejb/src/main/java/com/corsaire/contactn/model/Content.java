package com.corsaire.contactn.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Content implements Serializable {
	
	//seam-gen attributes (you should probably edit these)
	private Long id;
	private Integer version;
	private String title;
	private String description;
	private String url;
	
    //add additional entity attributes
	
	//seam-gen attribute getters/setters with annotations (you probably should edit)
		
	@Id @GeneratedValue
	public Long getId() {
	     return id;
	}

	public void setId(Long id) {
	     this.id = id;
	}
	
	@Version
	public Integer getVersion() {
	     return version;
	}

	private void setVersion(Integer version) {
	     this.version = version;
	}   	
	
	@NotNull
	@Length(max=70)
	public String getTitle() {
	     return title;
	}

	public void setTitle(String name) {
	     this.title = name;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}   	
}
