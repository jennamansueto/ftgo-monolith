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

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "consumer_id", nullable = false)
  private Long consumerId;

  @Column(name = "restaurant_id", nullable = false)
  private Long restaurantId;

  @Column(name = "message", nullable = false, length = 1000)
  private String message;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  private OrderMessage() {
  }

  public OrderMessage(Long orderId, Long consumerId, Long restaurantId, String message) {
    this.orderId = orderId;
    this.consumerId = consumerId;
    this.restaurantId = restaurantId;
    this.message = message;
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public Long getConsumerId() {
    return consumerId;
  }

  public Long getRestaurantId() {
    return restaurantId;
  }

  public String getMessage() {
    return message;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
