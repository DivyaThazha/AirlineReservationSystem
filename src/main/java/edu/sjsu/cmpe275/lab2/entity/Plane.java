package edu.sjsu.cmpe275.lab2.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Embeddable
public class Plane {

    @Column
    private int capacity;

    @Column
    private String model;

    @Column
    private String manufacturer;

    @Column
    private int year;
}
