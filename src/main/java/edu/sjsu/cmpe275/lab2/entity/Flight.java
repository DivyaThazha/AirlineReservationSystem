package edu.sjsu.cmpe275.lab2.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String flightNumber; // Each flight has a unique flight number.
    private double price;
    private String origin;
    private String destination;
    private Date departureTime;
    private Date arrivalTime;
    private int seatsLeft;
    private String description;

    @Embedded
    private Plane plane;  // Embedded

    @Transient
    private List<Passenger> passengers;

}
