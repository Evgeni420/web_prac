package com.booking.bus.entity;

import javax.persistence.*;

@Entity
@Table(name = "seat_occupancies")
public class SeatOccupancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false)
    private RouteStop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false)
    private RouteStop toStop;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public RouteStop getFromStop() { return fromStop; }
    public void setFromStop(RouteStop fromStop) { this.fromStop = fromStop; }

    public RouteStop getToStop() { return toStop; }
    public void setToStop(RouteStop toStop) { this.toStop = toStop; }
}
