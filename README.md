# Airline Reservation System

## REST API, Persistence, and Transactions

This project is hosted on AWS EC2 and for database AWS RDS is used.

Following are the requirements and constraints:

* Each passenger can make one or more reservations. Time overlap is not allowed among any of his reservation.
* Each reservation may consist of one or more flights.
* Each flight can carry one or more passengers.
* Each flight uses one plane, which is an embedded object with four fields mapped to the corresponding four columns in the airline table.
* The total amount of passengers can not exceed the capacity of an plane.
* When a passenger is deleted, all reservation made by him are automatically canceled for him.
* A flight can not be deleted if it needs to carry at least one passenger.

