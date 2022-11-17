package edu.htw.secondhand.persistence;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Address {
    @NotNull @Size(max = 63)
    @Column(nullable = false, updatable = true, length = 63)
    private String street;
    @NotNull @Size(max = 63)
    @Column(nullable = false, updatable = true, length = 63)
    private String city;
    @NotNull @Size(max = 63)
    @Column(nullable = false, updatable = true, length = 63)
    private String country;
    @NotNull @Size(max = 15)
    @Column(nullable = false, updatable = true, length = 15)
    private String postcode;

    @JsonbProperty
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @JsonbProperty
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonbProperty
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonbProperty
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
