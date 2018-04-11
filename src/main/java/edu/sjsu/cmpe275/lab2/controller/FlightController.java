package edu.sjsu.cmpe275.lab2.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.FlightRepository;
import edu.sjsu.cmpe275.lab2.util.IntervalStartComparator;
import edu.sjsu.cmpe275.lab2.util.View;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/flight")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    /**
     * (1) Get a flight back as JSON.
     *
     * @param flightNumber the flight number
     * @return the flight json
     */

    @JsonView(View.FlightView.class)
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFlightJson(@PathVariable("flightNumber") String flightNumber) {

        return getFlight(flightNumber);

    }

    /**
     * (2) Get a flight back as XML.
     *
     * @param flightNumber the flight number
     * @param isXml        the is xml
     * @return the flight xml
     */
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.GET, params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getFlightXml(@PathVariable("flightNumber") String flightNumber,
                                          @RequestParam(value = "xml") String isXml) {
        if (isXml.equals("true")) {
            return getFlight(flightNumber);

        } else {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Xml param; found in invalid state!")),
                    HttpStatus.BAD_REQUEST);
        }

    }

    private ResponseEntity<?> getFlight(String flightNumber) {
        //query db
        Flight flight = flightRepository.findOne(flightNumber);

        if (flight == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested flight with number "
                    + flightNumber + " does not exist")), HttpStatus.NOT_FOUND);

        } else {
            return new ResponseEntity<>(flight, HttpStatus.OK);
        }

    }

    /**
     * (13) Create or update a flight.
     *
     * @param flightNumber      the flight number
     * @param price             the price
     * @param origin              the from
     * @param destination                the to
     * @param departureTime     the departure time
     * @param arrivalTime       the arrival time
     * @param description       the description
     * @param capacity          the capacity
     * @param model             the model
     * @param manufacturer      the manufacturer
     * @param yearOfManufacture the year of manufacture
     * @return the response entity
     */
    @JsonView(View.FlightView.class)
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createOrUpdateFlight(@PathVariable("flightNumber") String flightNumber,
                                                  @RequestParam(value = "price") int price,
                                                  @RequestParam(value = "origin") String origin,
                                                  @RequestParam(value = "destination") String destination,
                                                  @RequestParam(value = "departureTime") String departureTime,
                                                  @RequestParam(value = "arrivalTime") String arrivalTime,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "capacity") int capacity,
                                                  @RequestParam(value = "model") String model,
                                                  @RequestParam(value = "manufacturer") String manufacturer,
                                                  @RequestParam(value = "year") int yearOfManufacture) {
        try{

            if(origin==null||destination==null||origin==""||destination==""||departureTime==null||arrivalTime==null ||departureTime==""||arrivalTime==""||description==null||
                    description==""||model==null||model==""||manufacturer==null||manufacturer==""||price==0||capacity==0||yearOfManufacture==0){
                    return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "all parameters are not filled")), HttpStatus.BAD_REQUEST);
            }

            // to update the seatsLeft when capacity is modified
            Flight flight = flightRepository.findOne(flightNumber);


            if (capacity < 0) {
                return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Capacity can't be negative value!")), HttpStatus.BAD_REQUEST);
            }

            if (arrivalTime.equals(departureTime)) {
                return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Arrival and Departure Time can't be same!")), HttpStatus.BAD_REQUEST);
            }

            if (flight != null && capacity < flight.getReservations().size()) {
                return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "New capacity can't be less than the reservations for that flight!")), HttpStatus.BAD_REQUEST);
            }

            int seatsLeft = capacity;
            if (flight != null) {
                // set flight capacity
                seatsLeft = capacity - flight.getReservations().size();

                List<Interval> intervals = new ArrayList<>();

                for (Reservation reservation : flight.getReservations()) {

                    Passenger passenger = reservation.getPassenger();
                    List<Reservation> passengerReservations = passenger.getReservations();

                    for (Reservation passengerReservation : passengerReservations) {
                        List<Flight> flights = passengerReservation.getFlights();

                        for (Flight passengerFlight : flights) {

                            Date departureDateTime = (passengerFlight.getDepartureTime());
                            Date arrivalDateTime = (passengerFlight.getArrivalTime());

                            intervals.add(new Interval(departureDateTime.getTime(), arrivalDateTime.getTime()));
                        }
                    }

                }

                Collections.sort(intervals, new IntervalStartComparator());
                // check Time-Overlap for a certain passenger
                if (isOverlapping(intervals)) {
                    return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Passenger will have overlapping flight time!")), HttpStatus.BAD_REQUEST);
                }
            }
            else {
                flight = new Flight();
            }

            Plane plane = new Plane(capacity, model, manufacturer, yearOfManufacture);
            String format = "yyyy-MM-dd-HH";

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dateD = null;
            Date dateA = null;
            try {
                dateD = sdf.parse(departureTime);
                dateA = sdf.parse(arrivalTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Invalid Date format")), HttpStatus.BAD_REQUEST);

            }

            flight.setFlightNumber(flightNumber);
            flight.setPrice(price);
            flight.setDestination(destination);
            flight.setOrigin(origin);
            flight.setDepartureTime(dateD);
            flight.setArrivalTime(dateA);
            flight.setDescription(description);
            flight.setPlane(plane);
            flight.setSeatsLeft(seatsLeft);
            // save to db
            Flight flightObjFromDb = flightRepository.save(flight);

            //return new ResponseEntity<>(new BadRequestController(new BadRequest(200, "Inserted")), HttpStatus.OK);

            return getFlight(flightNumber);
            //return new ResponseEntity<>(flightObjFromDb, HttpStatus.OK);


        }catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "another passenger with the same number already exists.")), HttpStatus.BAD_REQUEST);
        }
    }

    public static boolean isOverlapping(List<Interval> sortedIntervals) {
        for (int i = 0, n = sortedIntervals.size(); i < n - 1; i++) {
            if (sortedIntervals.get(i).overlaps(sortedIntervals.get(i + 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * (14) Delete a flight.
     *
     * @param flightNumber the flight number
     * @return the response entity
     */
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteFlight(@PathVariable("flightNumber") String flightNumber) {

        // query db
        Flight flight = flightRepository.findOne(flightNumber);
        System.out.println("In delete flight" + flight);
        if (flight == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Flight with number " + flightNumber + " does not exist")), HttpStatus.NOT_FOUND);

        } else if (!flight.getReservations().isEmpty()) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Flight with number " + flightNumber + " has one or more reservation")), HttpStatus.BAD_REQUEST);

        } else {
            // remove from db
            flightRepository.delete(flightNumber);
            return new ResponseEntity<>(new BadRequestController(new BadRequest(200, "Flight with number " + flightNumber +  " is deleted successfully")), HttpStatus.OK);
        }
    }

}
