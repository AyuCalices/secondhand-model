package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.*;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Path("offer")
public class OfferService {

    @GET
    @Path("{id}")
    public ArrayList<Offer> getOffer(@PathParam("id") @PositiveOrZero long offerIdentity) {
        ArrayList<Offer> offers = new ArrayList<Offer>();
        //TODO: how to search filter ?
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        offers.add(entityManager.find(Offer.class, offerIdentity));
        if (offers.size() == 0) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offers;
        }
    }

    @POST
    public void postOffer(Offer offer) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
    }

    @GET
    @Path("{id}")
    public Offer getOffers(@PathParam("id") @PositiveOrZero long offerIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer;
        }
    }

    @GET
    @Path("{id}")
    public Document getOfferAvatar(@PathParam("id") @PositiveOrZero long offerIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer.getAvatar();
        }
    }

    @GET
    @Path("{id}")
    public Person getOfferSeller(@PathParam("id") @PositiveOrZero long offerIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer.getSeller();
        }
    }

    @GET
    @Path("{id}")
    public Order getOfferOrder(@PathParam("id") @PositiveOrZero long offerIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            return null;
        } else {
            return offer.getOrder();
        }
    }

    @DELETE
    @Path("{id}")
    public void deleteOffer(@PathParam("id") @PositiveOrZero long offerIdentity, @HeaderParam("X-Requester-Identity") @Positive long requesterIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            Offer offer = entityManager.find(Offer.class, offerIdentity);
            if (offer == null) {
                throw new ClientErrorException(Response.Status.NOT_FOUND);
            } else if ((offer.getOrder() == null && offer.getSeller().getIdentity() == requesterIdentity) || requester.getGroup() == Group.ADMIN) {
                entityManager.remove(offer);
            } else {
                throw new ClientErrorException(Response.Status.FORBIDDEN);
            }
        }
    }
}