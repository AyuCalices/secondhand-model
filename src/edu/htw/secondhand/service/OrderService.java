package edu.htw.secondhand.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("order")
public class OrderService {

    @GET
    public String getOrder() {
        return "order";
    }
}