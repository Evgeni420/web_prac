package com.booking.bus.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "route_number", nullable = false)
    private String routeNumber;

    @Column(name = "route_description")
    private String routeDescription;

    @Column(name = "bus_capacity", nullable = false)
    private Integer busCapacity;

    @Transient
    private String[] departureTimes;

    @Column(nullable = false)
    private Boolean published = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopIndex")
    private List<RouteStop> stops = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Fare> fares = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<Trip> trips = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteDescription() { return routeDescription; }
    public void setRouteDescription(String routeDescription) { this.routeDescription = routeDescription; }

    public Integer getBusCapacity() { return busCapacity; }
    public void setBusCapacity(Integer busCapacity) { this.busCapacity = busCapacity; }

    public String[] getDepartureTimes() { return departureTimes; }
    public void setDepartureTimes(String[] departureTimes) { this.departureTimes = departureTimes; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RouteStop> getStops() { return stops; }
    public void setStops(List<RouteStop> stops) { this.stops = stops; }

    public List<Fare> getFares() { return fares; }
    public void setFares(List<Fare> fares) { this.fares = fares; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }

    public List<Trip> getTrips() { return trips; }
    public void setTrips(List<Trip> trips) { this.trips = trips; }
}
