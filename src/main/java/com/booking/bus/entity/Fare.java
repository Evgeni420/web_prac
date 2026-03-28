package com.booking.bus.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fares")
public class Fare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false)
    private RouteStop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false)
    private RouteStop toStop;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "travel_time_minutes", nullable = false)
    private Integer travelTimeMinutes;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public RouteStop getFromStop() { return fromStop; }
    public void setFromStop(RouteStop fromStop) { this.fromStop = fromStop; }

    public RouteStop getToStop() { return toStop; }
    public void setToStop(RouteStop toStop) { this.toStop = toStop; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getTravelTimeMinutes() { return travelTimeMinutes; }
    public void setTravelTimeMinutes(Integer travelTimeMinutes) { this.travelTimeMinutes = travelTimeMinutes; }
}
