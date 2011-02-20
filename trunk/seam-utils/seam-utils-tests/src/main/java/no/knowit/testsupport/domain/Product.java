package no.knowit.testsupport.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import com.google.common.base.Preconditions;

@Entity
public class Product implements Serializable {

  private static final long serialVersionUID = -6847972902247042866L;

  @Id
  @GeneratedValue
  private Long id;

  @Version
  private long version;

  @Column(unique = true, nullable = false)
  private final String code = UUID.randomUUID().toString();

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private long price;

  protected Product() {
  }

  public Long getId() {
    return id;
  }

  public long getVersion() {
    return version;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public long getPrice() {
    return price;
  }

  // Builder
  public static Builder with() {
    return new Builder();
  }

  public static class Builder {
    private final Product item = new Product();

    private Builder() {
    }

    public Product build() {
      Preconditions.checkState(!item.name.trim().isEmpty(), "Product name can not be empty");
      Preconditions.checkState(item.price > 0, "Product price must be greater than 0");
      return item;
    }

    public Builder name(final String name) {
      item.name = Preconditions.checkNotNull(name, "Product name can not be empty").trim();
      return this;
    }

    public Builder price(final long price) {
      item.price = price;
      return this;
    }

  } //~Builder

}
