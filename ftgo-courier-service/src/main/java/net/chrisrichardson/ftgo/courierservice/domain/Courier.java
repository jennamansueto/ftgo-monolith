package net.chrisrichardson.ftgo.courierservice.domain;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.PersonName;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courier")
@Access(AccessType.FIELD)
@DynamicUpdate
public class Courier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private PersonName name;

  @Embedded
  private Address address;

  @Embedded
  private Plan plan = new Plan();

  private Boolean available;

  public Courier() {
  }

  public Courier(PersonName name, Address address) {
    this.name = name;
    this.address = address;
  }

  public void noteAvailable() {
    this.available = true;
  }

  public void noteUnavailable() {
    this.available = false;
  }

  public void addAction(CourierAction action) {
    plan.add(action);
  }

  public void cancelDelivery(long orderId) {
    plan.removeDelivery(orderId);
  }

  public void assignOrder(long orderId, LocalDateTime readyBy) {
    plan.add(CourierAction.makePickup(orderId));
    plan.add(CourierAction.makeDropoff(orderId, readyBy.plusMinutes(30)));
  }

  public boolean isAvailable() {
    return available != null && available;
  }

  public Plan getPlan() {
    return plan;
  }

  public Long getId() {
    return id;
  }

  public PersonName getName() {
    return name;
  }

  public Address getAddress() {
    return address;
  }

  public List<CourierAction> actionsForDelivery(long orderId) {
    return plan.actionsForDelivery(orderId);
  }
}
