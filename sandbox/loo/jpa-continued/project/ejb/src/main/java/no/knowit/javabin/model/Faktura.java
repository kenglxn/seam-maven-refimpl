package no.knowit.javabin.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Faktura implements Serializable {

  private static final long serialVersionUID = 1593425600652596803L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer id;
  
  @Version
  private Long version;
  
  private String name;

  @OneToMany (mappedBy="faktura", cascade=CascadeType.ALL)
  private Collection<Fakturalinje> fakturalinjer = new ArrayList<Fakturalinje>();
  
  public Faktura() {
    super();
  }
  
  public Faktura(final String name) {
    this();
    this.name = name;
  }
  
  public Integer getId() {
    return this.id;
  }

  public Long getVersion() {
    return this.version;
  }

  public String getName() {
    return name;
  }

  public Collection<Fakturalinje> getFakturalinjer() {
    return fakturalinjer;
  }
  
  public boolean leggTilFakturalinje(final Fakturalinje fakturalinje) {
    return fakturalinjer.add(fakturalinje);
  }
}
