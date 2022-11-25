package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Group;
import edu.htw.secondhand.persistence.Offer;
import edu.htw.secondhand.persistence.Order;
import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Set;

import static edu.htw.secondhand.service.BasicAuthenticationReceiverFilter.REQUESTER_IDENTITY;
import static javax.ws.rs.core.Response.Status.*;

@Path("order")
public class OrderService {

    @GET
    public long postOrder(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @QueryParam("avatarReference") @Positive Long avatarReference,
            @NotNull @Valid Order orderTemplate
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null) throw new ClientErrorException(FORBIDDEN);

        final boolean insertMode = orderTemplate.getIdentity() == 0;
        boolean isRequesterAdmin = requester.getGroup() == Group.ADMIN;

        if (isRequesterAdmin) throw new ClientErrorException(FORBIDDEN);

        final Order order;
        if (insertMode) {
            order = new Order(requester);
            if (avatarReference == null) avatarReference = 1L;
        } else {
            order = entityManager.find(Order.class, orderTemplate.getIdentity());
            if (order == null) throw new ClientErrorException(NOT_FOUND);
        }

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
        // MERKE: wenn sich Spiegelrelationen verändern, muss
        // die betroffene Entität aus dem second level cache entfernt werden
        return order.getIdentity();
    }

    @GET
    @Path("{id}")
    public Order getOrders(@PathParam("id") @Positive long orderIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return order;
        }
    }

    @GET
    @Path("{id}/buyer")
    public Person findOrderBuyer(@PathParam("id") @Positive long orderIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return order.getBuyer();
        }
    }

    @GET
    @Path("{id}/seller")
    public Person findOrderSeller(@PathParam("id") @Positive long orderIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return order.getOffers().stream().findFirst().get().getSeller();
        }
    }

    @GET
    @Path("{id}/offers")
    public Set<Offer> findOrderOffer(@PathParam("id") @Positive long orderIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Order order = entityManager.find(Order.class, orderIdentity);
        if (order == null) {
            return null;
        } else {
            return order.getOffers();
        }
    }

    @DELETE
    @Path("{id}")
    public void deleteOrder(@PathParam("id") @Positive long orderIdentity, @HeaderParam("X-Requester-Identity") @Positive long requesterIdentity) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            Order order = entityManager.find(Order.class, orderIdentity);
            if (order == null) {
                throw new ClientErrorException(Response.Status.NOT_FOUND);
            } else if (requester.getGroup() == Group.ADMIN) {
                entityManager.remove(order);
            } else {
                throw new ClientErrorException(Response.Status.FORBIDDEN);
            }
        }
    }
}