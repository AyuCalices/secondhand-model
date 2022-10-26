package edu.damago.secondhand.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    @Column(nullable = false, updatable = true)
    private String street;
    @Column(nullable = false, updatable = true)
    private String city;
    @Column(nullable = false, updatable = true)
    private String country;
    @Column(nullable = false, updatable = true)
    private String postcode;

    protected Address() {}

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
