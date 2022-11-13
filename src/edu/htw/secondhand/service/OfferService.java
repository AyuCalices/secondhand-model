package edu.htw.secondhand.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("offer")
public class OfferService {

    @GET
    public String getOffer() {
        return "offer";
    }
}