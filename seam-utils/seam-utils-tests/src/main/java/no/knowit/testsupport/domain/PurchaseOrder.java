package no.knowit.testsupport.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.google.common.base.Preconditions;

@Entity
public class PurchaseOrder implements Serializable{

  private static final long serialVersionUID = 2055267049618370550L;
  private static final String CUSTOMER_NAME_CAN_NOT_BE_EMPTY = "Customer name can not be empty";
  private static final String PURCHASE_ORDER_CAN_NOT_BE_NULL = "Purchase order can not be null";
  private static final String ORDER_LINE_CAN_NOT_BE_NULL = "OrderLine can not be null";

  @Id
  @GeneratedValue
  private Long id;

  @Version
  private long version;

  @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
  private final Set<OrderLine> orderLines = new HashSet<OrderLine>();

  @Column(nullable = false)
  private String customerName;

  @Column(nullable = false)
  private Date date;

  protected PurchaseOrder() {
  }

  public Long getId() {
    return id;
  }

  public long getVersion() {
    return version;
  }

  public String getCustomerName() {
    return customerName;
  }

  public Date getDate() {
    return date;
  }

  public long getTotal() {
    long result = 0;
    for (final OrderLine o : orderLines) {
      result += o.getTotal();
    }
    return result;
  }

  public Set<OrderLine> getOrderines() {
    return Collections.unmodifiableSet(orderLines);
  }

  OrderLine addOrderLine(final OrderLine orderLine) {
    OrderLine result = findOrderLine(orderLine);

    if (result != null) {
      orderLines.remove(result);
      final int quantity = result.getQuantity() + orderLine.getQuantity();
      result = OrderLine.modify(result).quantity(quantity).build();
    }
    else {
      result = orderLine;
    }

    if (result.getQuantity() > 0) {
      result.purchaseOrder = this;
      orderLines.add(result);
      return result;
    }
    return null;
  }

  OrderLine removeOrderLine(final OrderLine orderLine) {
    final OrderLine result = findOrderLine(orderLine);
    return result != null && orderLines.remove(result) ? result : null;
  }

  public OrderLine findOrderLine(final OrderLine orderLine) {
    Preconditions.checkArgument(orderLine != null, ORDER_LINE_CAN_NOT_BE_NULL);

    if (orderLines.contains(orderLine)) {
      for (final OrderLine o : orderLines) {
        if (o.equals(orderLine)) {
          return o;
        }
      }
    }
    return null;
  }

  // Builder
  public static Builder with(final String customerName) {
    return new Builder(customerName);
  }

  public static Builder modify(final PurchaseOrder po) {
    return new Builder(po);
  }

  public static class Builder {
    private final PurchaseOrder po;

    private Builder(final String customerName) {
      po = new PurchaseOrder();

      po.customerName = Preconditions.checkNotNull(customerName,
          CUSTOMER_NAME_CAN_NOT_BE_EMPTY).trim();
      Preconditions.checkArgument(!customerName.isEmpty(), CUSTOMER_NAME_CAN_NOT_BE_EMPTY);
      po.date = new Date();
    }

    private Builder(final PurchaseOrder po) {
      this.po = Preconditions.checkNotNull(po, PURCHASE_ORDER_CAN_NOT_BE_NULL);
    }

    public PurchaseOrder build() {
      return po;
    }

    public Builder orderLine(final Product product, final int quantity) {
      final OrderLine orderLine = OrderLine.with(product).quantity(quantity).build();
      po.addOrderLine(orderLine);
      return this;
    }

  } //~Builder
}
