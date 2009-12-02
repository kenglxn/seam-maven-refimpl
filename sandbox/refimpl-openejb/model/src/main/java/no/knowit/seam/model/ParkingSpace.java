package no.knowit.seam.model;

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
import javax.persistence.OneToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class ParkingSpace implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Version
	private Long version;
	
	private Integer lot;
	private String location;
}
