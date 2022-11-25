package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.*;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Comparator;

import static edu.htw.secondhand.service.BasicAuthenticationReceiverFilter.REQUESTER_IDENTITY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.*;

@Path("offers")
public class OfferService {

    static private final String QUERY_OFFER = "SELECT o.identity FROM Offer AS o WHERE "
            + "(:lowerCreationTimestamp is null or o.created >= :lowerCreationTimestamp) AND "
            + "(:upperCreationTimestamp is null or o.created <= :upperCreationTimestamp) AND "
            + "(:lowerModificationTimestamp is null or o.modified >= :lowerModificationTimestamp) AND "
            + "(:upperModificationTimestamp is null or o.modified <= :upperModificationTimestamp) AND "

            + "(:category is null or o.article.category = :category) AND "
            + "(:brand is null or o.article.brand = :brand) AND "
            + "(:alias is null or o.article.alias = :alias) AND "
            + "(:description is null or o.article.description LIKE :description) AND "

            + "(:serial is null or o.serial = :serial) AND "
            + "(:lowerPrice is null or o.price >= :lowerPrice) AND "
            + "(:upperPrice is null or o.price <= :upperPrice) AND "
            + "(:lowerPostage is null or o.postage >= :lowerPostage) AND "
            + "(:upperPostage is null or o.postage <= :upperPostage)";

    @GET
    @Produces(APPLICATION_JSON)
    public Offer[] findOffer(
            @QueryParam("resultOffset") @PositiveOrZero final Integer resultOffset,
            @QueryParam("resultLimit") @PositiveOrZero final Integer resultLimit,
            @QueryParam("lowerCreationTimestamp") final Long lowerCreationTimestamp,
            @QueryParam("upperCreationTimestamp") final Long upperCreationTimestamp,
            @QueryParam("lowerModificationTimestamp") final Long lowerModificationTimestamp,
            @QueryParam("upperModificationTimestamp") final Long upperModificationTimestamp,

            @QueryParam("category") final Category category,
            @QueryParam("brand") final String brand,
            @QueryParam("alias") final String alias,
            @QueryParam("description") final String description,

            @QueryParam("serial") final String serial,
            @QueryParam("lowerPrice") final long lowerPrice,
            @QueryParam("upperPrice") final long upperPrice,
            @QueryParam("lowerPostage") final long lowerPostage,
            @QueryParam("upperPostage") final long upperPostage
    ){
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");

        final TypedQuery<Long> query = entityManager.createQuery(QUERY_OFFER, Long.class);
        if (resultOffset != null) query.setFirstResult(resultOffset);
        if (resultLimit != null) query.setMaxResults(resultLimit);

        final Offer[] offers = query
                .setParameter("lowerCreationTimestamp", lowerCreationTimestamp)
                .setParameter("upperCreationTimestamp", upperCreationTimestamp)
                .setParameter("lowerModificationTimestamp", lowerModificationTimestamp)
                .setParameter("upperModificationTimestamp", upperModificationTimestamp)
                .setParameter("category", category)
                .setParameter("brand", brand)
                .setParameter("alias", alias)
                .setParameter("description", description == null ? null : "%" + description + "%")
                .setParameter("serial", serial)
                .setParameter("lowerPrice", lowerPrice)
                .setParameter("upperPrice", upperPrice)
                .setParameter("lowerPostage", lowerPostage)
                .setParameter("upperPostage", upperPostage)
                .getResultList()
                .stream()
                .map(identity -> entityManager.find(Offer.class, identity))
                .filter(offer -> offer != null)
                .sorted()
                .toArray(Offer[]::new);

        return offers;
    }

    @POST
    @Produces(TEXT_PLAIN)
    @Consumes(APPLICATION_JSON)
    public long postOffer(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @QueryParam("avatarReference") @Positive Long avatarReference,
            @NotNull @Valid final Offer offerTemplate
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null || requester.getGroup() == Group.ADMIN) throw new ClientErrorException(FORBIDDEN);

        final boolean insertMode = offerTemplate.getIdentity() == 0;

        final Offer offer;
        if (insertMode) {
            offer = new Offer(requester);
            if (avatarReference == null) avatarReference = 1L;
        } else {
            offer = entityManager.find(Offer.class, offerTemplate.getIdentity());
            if (offer == null) throw new ClientErrorException(NOT_FOUND);
        }

        offer.setModified(System.currentTimeMillis());
        offer.setVersion(offerTemplate.getVersion());
        offer.setSerial(offerTemplate.getSerial());
        offer.setPrice(offerTemplate.getPrice());
        offer.setPostage(offerTemplate.getPostage());
        offer.getArticle().setAlias(offerTemplate.getArticle().getAlias());
        offer.getArticle().setBrand(offerTemplate.getArticle().getBrand());
        offer.getArticle().setCategory(offerTemplate.getArticle().getCategory());
        offer.getArticle().setDescription(offerTemplate.getArticle().getDescription());
        if (avatarReference != null) {
            final Document avatar = entityManager.find(Document.class, avatarReference);
            if (avatar == null) throw new ClientErrorException(NOT_FOUND);
            offer.setAvatar(avatar);
        }

        if (insertMode)
            entityManager.persist(offer);
        else
            entityManager.flush();

        try {
            entityManager.getTransaction().commit();
        } catch (final RollbackException e) {
            throw new ClientErrorException(CONFLICT);
        } finally {
            entityManager.getTransaction().begin();
        }

        final Cache cache = entityManager.getEntityManagerFactory().getCache();
        cache.evict(Person.class, requester.getIdentity());
        return offer.getIdentity();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Offer findOffers(
            @PathParam("id") @Positive long offerIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) throw new ClientErrorException(NOT_FOUND);
        return offer;
    }

    @GET
    @Path("{id}/avatar")
    @Produces("image/*")
    public Response findOfferAvatar(
            @PathParam("id") @Positive long offerIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) throw new ClientErrorException(NOT_FOUND);
        if (!offer.getAvatar().getType().startsWith("image/")) throw new ClientErrorException(NOT_ACCEPTABLE);

        return Response.ok(offer.getAvatar().getContent(), offer.getAvatar().getType()).build();
    }

    @GET
    @Path("{id}/seller")
    @Produces(APPLICATION_JSON)
    public Person findOfferSeller(
            @PathParam("id") @Positive long offerIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) throw new ClientErrorException(NOT_FOUND);

        return offer.getSeller();
    }

    @GET
    @Path("{id}/order")
    @Produces(APPLICATION_JSON)
    public Order findOfferOrder(
            @PathParam("id") @Positive long offerIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) throw new ClientErrorException(NOT_FOUND);

        return offer.getOrder();
    }

    @DELETE
    @Path("{id}")
    @Produces(TEXT_PLAIN)
    public long deleteOffer(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @PathParam("id") @Positive final long offerIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) throw new ClientErrorException(NOT_FOUND);

        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null || (requester.getGroup() != Group.ADMIN && offer.getOrder() != null)) throw new ClientErrorException(FORBIDDEN);

        entityManager.remove(offer);

        try {
            entityManager.getTransaction().commit();
        } finally {
            entityManager.getTransaction().begin();
        }

        final Cache cache = entityManager.getEntityManagerFactory().getCache();
        cache.evict(Person.class, offer.getSeller().getIdentity());

        return offer.getIdentity();
    }
}