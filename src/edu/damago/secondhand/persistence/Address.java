package edu.damago.secondhand.persistence;

public class Address {
    private char[] street;
    private char[] city;
    private char[] country;
    private char[] postcode;

    protected Address() {}

    public char[] getStreet() {
        return street;
    }

    public void setStreet(char[] street) {
        this.street = street;
    }

    public char[] getCity() {
        return city;
    }

    public void setCity(char[] city) {
        this.city = city;
    }

    public char[] getCountry() {
        return country;
    }

    public void setCountry(char[] country) {
        this.country = country;
    }

    public char[] getPostcode() {
        return postcode;
    }

    public void setPostcode(char[] postcode) {
        this.postcode = postcode;
    }
}
