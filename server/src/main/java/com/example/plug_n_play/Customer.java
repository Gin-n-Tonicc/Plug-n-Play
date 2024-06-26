package com.example.plug_n_play;

import jakarta.persistence.Id;

public class Customer {

    @Id
    public String id;

    public String firstName;
    public String lastName;
    public String description;

    public Customer() {}

    public Customer(String firstName, String lastName, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
