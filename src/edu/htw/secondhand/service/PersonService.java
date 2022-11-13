package edu.htw.secondhand.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("person")
public class PersonService {

    @GET
    public String getPerson() {
        return "person";
    }

}