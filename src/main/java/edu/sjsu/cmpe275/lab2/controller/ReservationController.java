package edu.sjsu.cmpe275.lab2.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.*;
import edu.sjsu.cmpe275.lab2.util.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private FlightRepository flightRepository;


    @JsonView(View.ReservationView.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReservation(@PathVariable("id") String number) {
        //query db
        Reservation reservation = reservationRepository.findOne(number);

        if (reservation == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested reservation with id " +
                    number + " does not exist")), HttpStatus.NOT_FOUND);


        } else {

            //Passenger passenger = passengerRepository.getPassengerByOrderNo(reservation.getOrderNumber());
            //reservation.setPassenger(new PassengerInformation(passenger));
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }

    }

    /*private Reservation removePassenger(Reservation reservation){
        Set<Flight> flights = reservation.getFlights();
        for(Flight f : flights) {
            f.setPassengers(null);
        }
        reservation.setFlights(flights);

        return reservation;
    }*/
}
