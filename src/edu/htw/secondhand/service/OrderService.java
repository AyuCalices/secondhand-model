package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Group;
import edu.htw.secondhand.persistence.Offer;
import edu.htw.secondhand.persistence.Order;
import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.htw.secondhand.service.BasicAuthenticationReceiverFilter.REQUESTER_IDENTITY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.*;

@Path("orders")
public class OrderService {

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_PLAIN)
    public long createOrUpdateOrder(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @QueryParam("offerReference") final Set<Long> offerReferences,
            @NotNull @Valid final Order orderTemplate
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null || requester.getGroup() == Group.ADMIN) throw new ClientErrorException(FORBIDDEN);

        final boolean insertMode = orderTemplate.getIdentity() == 0;

        final Order order;
        if (insertMode) {
            order = new Order(requester);
            if (offerReferences.isEmpty()) throw new ClientErrorException(BAD_REQUEST);
        } else {
            order = entityManager.find(Order.class, orderTemplate.getIdentity());
            if (order == null) throw new ClientErrorException(NOT_FOUND);
        }

        if (requester.getIdentity() != order.getBuyer().getIdentity() && requester.getIdentity() != order.getSellerReference())
            throw new ClientErrorException(FORBIDDEN);

        order.setModified(System.currentTimeMillis());
        order.setVersion(orderTemplate.getVersion());
        order.setArrived(orderTemplate.getArrived());
        order.setDeparted(orderTemplate.getDeparted());
        order.setPayed(orderTemplate.getPayed());
        order.setTrackingReference(orderTemplate.getTrackingReference());

        if (insertMode)
            entityManager.persist(order);
        else
            entityManager.flush();

        try {
            entityManager.getTransaction().commit();
        } catch (final RollbackException e) {
            throw new ClientErrorException(CONFLICT);
        } finally {
            entityManager.getTransaction().begin();
        }

        Person seller = null;
        for (final long offerReference : offerReferences) {
            final Offer offer = entityManager.find(Offer.class, offerReference);
            if (offer == null) throw new ClientErrorException(Response.Status.NOT_FOUND);
            if (seller != null && seller.getIdentity() != offer.getSeller().getIdentity())
                throw new ClientErrorException(Response.Status.FORBIDDEN);
            seller = offer.getSeller();
            offer.setOrder(order);
        }

        entityManager.flush();
        try {
            entityManager.getTransaction().commit();
        } finally {
            entityManager.getTransaction().begin();
        }

        final Cache cache = entityManager.getEntityManagerFactory().getCache();
        cache.evict(Person.class, requester.getIdentity());
        cache.evict(Order.class, order.getIdentity());

        return order.getIdentity();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Order findOrder(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @PathParam("id") @Positive final long orderIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) throw new ClientErrorException(NOT_FOUND);
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null || (requester.getGroup() != Group.ADMIN && requester.getIdentity() != order.getBuyer().getIdentity()) && requester.getIdentity() != order.getSellerReference())
            throw new ClientErrorException(FORBIDDEN);

        return order;
    }

    @GET
    @Path("{id}/buyer")
    @Produces(APPLICATION_JSON)
    public Person findOrderBuyer(
            @PathParam("id") @Positive final long orderIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) throw new ClientErrorException(NOT_FOUND);

        return order.getBuyer();
    }

    @GET
    @Path("{id}/seller")
    @Produces(APPLICATION_JSON)
    public Person findOrderSeller(
            @PathParam("id") @Positive final long orderIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) throw new ClientErrorException(NOT_FOUND);

        return order.getOffers().stream().findFirst().get().getSeller();
    }

    @GET
    @Path("{id}/offers")
    @Produces(APPLICATION_JSON)
    public Offer[] findOrderOffers(
            @PathParam("id") @Positive final long orderIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) throw new ClientErrorException(NOT_FOUND);

        return order.getOffers().stream().sorted().toArray(Offer[]::new);
    }

    @DELETE
    @Path("{id}")
    @Produces(TEXT_PLAIN)
    public long deleteOrder(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @PathParam("id") @Positive final long orderIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null || requester.getGroup() != Group.ADMIN) throw new ClientErrorException(FORBIDDEN);

        final Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) throw new ClientErrorException(NOT_FOUND);

        entityManager.remove(order);

        try {
            entityManager.getTransaction().commit();
        } finally {
            entityManager.getTransaction().begin();
        }

        final Cache cache = entityManager.getEntityManagerFactory().getCache();
        cache.evict(Person.class, order.getBuyer().getIdentity());

        return order.getIdentity();
    }
}