package com.booking.bus.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false)
    private RouteStop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false)
    private RouteStop toStop;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String status = "booked";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private SeatOccupancy seatOccupancy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public RouteStop getFromStop() { return fromStop; }
    public void setFromStop(RouteStop fromStop) { this.fromStop = fromStop; }

    public RouteStop getToStop() { return toStop; }
    public void setToStop(RouteStop toStop) { this.toStop = toStop; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public SeatOccupancy getSeatOccupancy() { return seatOccupancy; }
    public void setSeatOccupancy(SeatOccupancy seatOccupancy) { this.seatOccupancy = seatOccupancy; }
}
