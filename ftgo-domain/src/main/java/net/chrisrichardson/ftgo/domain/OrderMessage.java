package net.chrisrichardson.ftgo.domain;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_messages")
@Access(AccessType.FIELD)
@DynamicUpdate
public class OrderMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  private OrderMessage() {
  }

  public OrderMessage(Order order, String message) {
    this.order = order;
    this.message = message;
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Order getOrder() {
    return order;
  }

  public String getMessage() {
    return message;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
