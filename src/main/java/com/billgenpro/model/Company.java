package com.billgenpro.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Company {
    @NotBlank(message = "Company name is required")
    private String name;
    
    private String address;
    private String phone;
    private String gst;

    // Constructors
    public Company() {}

    public Company(String name, String address, String phone, String gst) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.gst = gst;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGst() { return gst; }
    public void setGst(String gst) { this.gst = gst; }
}