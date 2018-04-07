package edu.sjsu.cmpe275.lab2.controller;

import edu.sjsu.cmpe275.lab2.entity.*;

import edu.sjsu.cmpe275.lab2.respository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping(value = "/passenger")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    @RequestMapping(value = "/{id}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPassengerJson(@PathVariable("id") String id) {

        return getPassenger(id);

    }

    private ResponseEntity<?> getPassenger(String id) {
        //query db
        Passenger passenger = passengerRepository.findOne(id);

        if (passenger == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested passenger with id " +
                    id + " does not exist")), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(passenger, HttpStatus.OK);

        }

    }

}
