package no.knowit.seam.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
//@DiscriminatorValue("SPECIAL_RANK") 
public class SpecialRank extends Rank {
  protected String message;
}

