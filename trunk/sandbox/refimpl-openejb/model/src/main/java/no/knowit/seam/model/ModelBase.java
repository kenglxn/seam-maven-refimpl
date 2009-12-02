package no.knowit.seam.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@MappedSuperclass 
public class ModelBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	@Version
	private Long version;
}
