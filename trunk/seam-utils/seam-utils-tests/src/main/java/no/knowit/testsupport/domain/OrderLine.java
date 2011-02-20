package no.knowit.testsupport.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@Entity
public class OrderLine implements Comparable<OrderLine>, Serializable {
  private static final long serialVersionUID = 3334888774103233917L;

  @Id
  @GeneratedValue
  private Long id;

  @Version
  private long version;

  @ManyToOne
  @JoinColumn(name = "po_id", nullable = false)
  PurchaseOrder purchaseOrder;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  private int quantity;
  private long price;
  private int discount;

  protected OrderLine() {
  }

  public Long getId() {
    return id;
  }

  public long getVersion() {
    return version;
  }

  public PurchaseOrder getPurchaseOrder() {
    return purchaseOrder;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public long getPrice() {
    return price;
  }

  public int getDiscount() {
    return discount;
  }

  public long getTotal() {
    final long t = price * quantity;
    return t - t * discount / 100;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(product.getId()); // Builder ensures item != null
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof OrderLine) {
      final OrderLine other = (OrderLine) o;
      return Objects.equal(product.getId(), other.product.getId()); // Builder ensures item != null
    }
    return false;
  }

  @Override
  public int compareTo(final OrderLine other) {
    return hashCode() - other.hashCode();
  }


  // Builder
  static Builder with(final Product product) {
    return new Builder(product);
  }

  static Builder modify(final OrderLine orderLine) {
    return new Builder(orderLine);
  }

  static class Builder {
    private final OrderLine orderLine;

    private Builder(final Product product) {
      orderLine = new OrderLine();
      orderLine.product = Preconditions.checkNotNull(product, "Item can not be null");
    }

    private Builder(final OrderLine orderLine) {
      this.orderLine = Preconditions.checkNotNull(orderLine, "OrderLine can not be null");
    }

    OrderLine build() {
      orderLine.price = orderLine.product.getPrice();
      orderLine.discount = orderLine.quantity < 10 ? 0 : 5;
      return orderLine;
    }

    Builder quantity(final int quantity) {
      orderLine.quantity = quantity;
      return this;
    }
  } //~Builder
}
