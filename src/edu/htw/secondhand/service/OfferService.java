package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.*;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

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

    static private final String QUERY_OFFER = "SELECT p.identity FROM Offer AS p WHERE "
            + "(:lowerCreationTimestamp is null or p.created >= :lowerCreationTimestamp) AND "
            + "(:upperCreationTimestamp is null or p.created <= :upperCreationTimestamp) AND "
            + "(:lowerModificationTimestamp is null or p.modified >= :lowerModificationTimestamp) AND "
            + "(:upperModificationTimestamp is null or p.modified <= :upperModificationTimestamp) AND "

            + "(:category is null or p.article.category = :category) AND "
            + "(:brand is null or p.article.brand = :brand) AND "
            + "(:alias is null or p.article.alias = :alias) AND "
            + "(:description is null or p.article.description = :description) AND "

            + "(:serial is null or p.serial = :serial) AND "
            + "(:price is null or p.price = :price) AND "
            + "(:postage is null or p.postage = :postage) AND "

            + "(:sellerReference is null or p.sellerReference = :sellerReference) AND "
            + "(:orderReference is null or p.orderReference = :orderReference)";

    static private final Comparator<Offer> OFFER_COMPARATOR = Comparator
            .comparing(Offer::getIdentity);

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
            @QueryParam("price") final long price,
            @QueryParam("postage") final long postage,

            @QueryParam("sellerReference") final long sellerReference,
            @QueryParam("orderReference") final long orderReference
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
                .setParameter("description", description)

                .setParameter("serial", serial)
                .setParameter("price", price)
                .setParameter("postage", postage)

                .setParameter("sellerReference", sellerReference)
                .setParameter("orderReference", orderReference)

                .getResultList()
                .stream()
                .map(identity -> entityManager.find(Offer.class, identity))
                .filter(offer -> offer != null)
                .sorted(OFFER_COMPARATOR)
                .toArray(Offer[]::new);

        return offers;
    }

    @POST
    @Produces(TEXT_PLAIN)
    @Consumes(APPLICATION_JSON)
    public long postOffer(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @QueryParam("avatarReference") @Positive Long avatarReference,
            @NotNull @Valid Offer offerTemplate
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null) throw new ClientErrorException(FORBIDDEN);

        final boolean insertMode = offerTemplate.getIdentity() == 0;
        boolean isRequesterAdmin = requester.getGroup() == Group.ADMIN;

        if (isRequesterAdmin) throw new ClientErrorException(FORBIDDEN);

        final Offer offer;
        if (insertMode) {
            offer = new Offer(requester);
            if (avatarReference == null) avatarReference = 1L;
        } else {
            offer = entityManager.find(Offer.class, offerTemplate.getIdentity());
            if (offer == null) throw new ClientErrorException(NOT_FOUND);
        }

        offer.setAvatar(offerTemplate.getAvatar());
        offer.setVersion(offerTemplate.getVersion());
        offer.setOrder(offerTemplate.getOrder());
        offer.setPostage(offerTemplate.getPostage());
        offer.setSerial(offerTemplate.getSerial());
        offer.setPrice(offerTemplate.getPrice());
        offer.getArticle().setAlias(offerTemplate.getArticle().getAlias());
        offer.getArticle().setBrand(offerTemplate.getArticle().getBrand());
        offer.getArticle().setCategory(offerTemplate.getArticle().getCategory());
        offer.getArticle().setDescription(offerTemplate.getArticle().getDescription());

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
        // MERKE: wenn sich Spiegelrelationen verändern, muss
        // die betroffene Entität aus dem second level cache entfernt werden
        return offer.getIdentity();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Offer findOffers(
            @PathParam("id") @Positive long offerIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer;
        }
    }

    @GET
    @Path("{id}/avatar")
    @Produces("image/*")
    public Response findOfferAvatar(
            @PathParam("id") @Positive long offerIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) throw new ClientErrorException(Response.Status.NOT_FOUND);

        return Response.ok(offer.getAvatar().getContent(), offer.getAvatar().getType()).build();
    }

    @GET
    @Path("{id}/seller")
    @Produces(APPLICATION_JSON)
    public Person findOfferSeller(
            @PathParam("id") @Positive long offerIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Offer offer = entityManager.find(Offer.class, offerIdentity);
        if (offer == null) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } else {
            return offer.getSeller();
        }
    }

    @GET
    @Path("{id}/order")
    @Produces(APPLICATION_JSON)
    public Order findOfferOrder(
            @PathParam("id") @Positive long offerIdentity
    ) {
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
    public void deleteOffer(
            @PathParam("id") @Positive long offerIdentity,
            @HeaderParam(REQUESTER_IDENTITY) @Positive long requesterIdentity
    ) {
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