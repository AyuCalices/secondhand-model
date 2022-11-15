package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("people")
public class PersonService {

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public Person getPersonById(
        @PathParam("id") @PositiveOrZero long personIdentity,
        @HeaderParam("X-Requester-Identity") long requesterIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        long identity = personIdentity == 0 ? requesterIdentity : personIdentity;
        Person person = entityManager.find(Person.class, identity);
        if (person == null) {
            throw new ClientErrorException(404);
        }
        return person;
    }

}