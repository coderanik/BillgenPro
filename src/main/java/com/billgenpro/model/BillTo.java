package com.billgenpro.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class BillTo {
    private String name;
    private String address;
    private String phone;

    // Constructors
    public BillTo() {}

    public BillTo(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}