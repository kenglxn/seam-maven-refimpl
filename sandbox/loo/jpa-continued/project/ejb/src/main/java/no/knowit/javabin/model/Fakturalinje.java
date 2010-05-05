package no.knowit.javabin.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class Fakturalinje implements Serializable {

  private static final long serialVersionUID = 8378077916677672228L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer id;
  
  @Version
  private Long version;
  
  private String tekst;
  
  @ManyToOne
  private Faktura faktura;
  
  public Fakturalinje() {
    super();
  }

  public Fakturalinje(final Faktura faktura, final String tekst) {
    this();
    this.faktura = faktura;
    this.tekst = tekst;
  }
  
  public Integer getId() {
    return this.id;
  }

  public Long getVersion() {
    return this.version;
  }

  public String getTekst() {
    return tekst;
  }

  public Faktura getFaktura() {
    return faktura;
  }

}
