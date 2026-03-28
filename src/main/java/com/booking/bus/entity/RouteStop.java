package com.booking.bus.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "route_stops")
public class RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "stop_index", nullable = false)
    private Integer stopIndex;

    @Column(name = "stop_name", nullable = false)
    private String stopName;

    @Column(name = "offset_minutes", nullable = false)
    private Integer offsetMinutes;

    @OneToMany(mappedBy = "fromStop", cascade = CascadeType.ALL)
    private List<Fare> faresFrom = new ArrayList<>();

    @OneToMany(mappedBy = "toStop", cascade = CascadeType.ALL)
    private List<Fare> faresTo = new ArrayList<>();

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Integer getStopIndex() { return stopIndex; }
    public void setStopIndex(Integer stopIndex) { this.stopIndex = stopIndex; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    public Integer getOffsetMinutes() { return offsetMinutes; }
    public void setOffsetMinutes(Integer offsetMinutes) { this.offsetMinutes = offsetMinutes; }

    public List<Fare> getFaresFrom() { return faresFrom; }
    public void setFaresFrom(List<Fare> faresFrom) { this.faresFrom = faresFrom; }

    public List<Fare> getFaresTo() { return faresTo; }
    public void setFaresTo(List<Fare> faresTo) { this.faresTo = faresTo; }
}
