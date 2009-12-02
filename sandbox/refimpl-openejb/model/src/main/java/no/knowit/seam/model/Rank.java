package no.knowit.seam.model;

import javax.persistence.*;
import java.util.*;
import java.io.Serializable;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="type",discriminatorType=DiscriminatorType.STRING)

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

public abstract class Rank implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
  @GeneratedValue(strategy=GenerationType.TABLE)
	protected Long id;
	
  protected String rankName;
  protected String avatar;
}
