package edu.htw.secondhand.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("document")
public class DocumentService {

    @GET
    public String getDocument() {
        return "document";
    }

}