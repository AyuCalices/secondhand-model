package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Group;
import edu.htw.secondhand.persistence.Offer;
import edu.htw.secondhand.persistence.Order;
import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("order")
public class OrderService {

    @GET
    public String postOrder() {
        //TODO: tbd
        return "order";
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