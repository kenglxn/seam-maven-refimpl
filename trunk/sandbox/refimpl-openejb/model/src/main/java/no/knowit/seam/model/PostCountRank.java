package no.knowit.seam.model;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;

@Entity
@AttributeOverrides({
  @AttributeOverride(name="avatar", column=@Column(name="IMAGE"))
})
public class PostCountRank extends Rank {
  protected Integer minimumPostCount;
}

