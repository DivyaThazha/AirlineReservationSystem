package edu.sjsu.cmpe275.lab2.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String reservationNumber;

    @ManyToOne(targetEntity = Passenger.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Passenger passenger;

    private double price;

    @ManyToMany(targetEntity = Flight.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Flight> flights;

    public Reservation(){

    }

    public Reservation(Passenger passenger, double price, List<Flight> flights) {
        this.passenger = passenger;
        this.price = price;
        this.flights = flights;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
