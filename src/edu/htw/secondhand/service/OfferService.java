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

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("offer")
public class OfferService {

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public ArrayList<Offer> findOffer(@PathParam("id") @PositiveOrZero long offerIdentity) {
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
    @Produces({ TEXT_PLAIN })
    public void postOffer(Offer offer) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
    }

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public Offer findOffers(@PathParam("id") @PositiveOrZero long offerIdentity) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer;
        }
    }

    @GET
    @Path("{id}")
    @Produces({ "application/pdf", "image/*", "audio/*", "video/*" })
    public Document findOfferAvatar(@PathParam("id") @PositiveOrZero long offerIdentity) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer.getAvatar();
        }
    }

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public Person findOfferSeller(@PathParam("id") @PositiveOrZero long offerIdentity) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer.getSeller();
        }
    }

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public Order findOfferOrder(@PathParam("id") @PositiveOrZero long offerIdentity) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            return null;
        } else {
            return offer.getOrder();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces( TEXT_PLAIN )
    public long deleteOffer(@PathParam("id") @PositiveOrZero long offerIdentity, @HeaderParam("X-Requester-Identity") @Positive long requesterIdentity) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            Offer offer = entityManager.find(Offer.class, offerIdentity);
            if (offer == null) {
                throw new ClientErrorException(Response.Status.NOT_FOUND);
            } else if ((offer.getOrder() == null && offer.getSeller().getIdentity() == requesterIdentity) || requester.getGroup() == Group.ADMIN) {
                entityManager.remove(offer);

                try {
                    entityManager.getTransaction().commit();
                } finally {
                    entityManager.getTransaction().begin();
                }

                return offer.getIdentity();
            } else {
                throw new ClientErrorException(Response.Status.FORBIDDEN);
            }
        }
    }
}