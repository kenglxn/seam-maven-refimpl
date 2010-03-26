package no.knowit.m2m.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import no.knowit.entity.BaseEntity;

/**
 * @author Leif Olsen.
 * @version 1.0
 */
@Entity
public class Interest extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Column(length = 20, nullable = false)
	private String name;

	public Interest() {
		super();
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
		return sb.append( "name:'" ).append( name ).append( "'}" ).toString();
	}
}
