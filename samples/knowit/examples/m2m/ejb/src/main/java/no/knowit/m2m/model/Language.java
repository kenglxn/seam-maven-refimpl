package no.knowit.m2m.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import no.knowit.entity.BaseEntity;

/**
 * @author Leif Olsen.
 * @version 1.0
 */
@Entity
public class Language extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Column(unique =true, length=2, nullable = false)
	private String code;
	
	@Column(length = 20, nullable = false)
	private String name;

	public Language() {
		super();
	}

	public String getCode() {
  	return code;
  }

	public void setCode( String code ) {
  	this.code = code;
  }

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder( super.toString() );
		return sb
			.append( "code='" ).append( code )
			.append( "name='" ).append( name )
			.append( "'}" )
			.toString();
	}
}
