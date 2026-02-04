package net.chrisrichardson.ftgo.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_messages")
@Access(AccessType.FIELD)
public class OrderMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "sent_at", nullable = false)
  private LocalDateTime sentAt;

  private OrderMessage() {
  }

  public OrderMessage(Long orderId, String message) {
    this.orderId = orderId;
    this.message = message;
    this.sentAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public String getMessage() {
    return message;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }
}
