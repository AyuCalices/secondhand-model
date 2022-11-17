package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.*;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

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
            throw new ClientErrorException(NOT_FOUND);
        }
        return person;
    }

    @GET
    @Path("{id}/avatar")
    @Produces({ TEXT_PLAIN })
    public Response getAvatarForPerson(
       @PathParam("id") @Positive long personIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Person person = entityManager.find(Person.class, personIdentity);
        if (person == null) {
            throw new ClientErrorException(NOT_FOUND);
        }
        Document avatar = person.getAvatar();
        return Response.ok(avatar.getContent(), avatar.getType()).build();
    }

    @GET
    @Path("{id}/offers")
    @Produces({ TEXT_PLAIN })
    public Set<Offer> getOffersForPerson(
            @PathParam("id") @Positive long personIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Person person = entityManager.find(Person.class, personIdentity);
        if (person == null) {
            throw new ClientErrorException(NOT_FOUND);
        }
        return person.getOffers();
    }

    @GET
    @Path("{id}/orders")
    @Produces({ TEXT_PLAIN })
    public Set<Order> getOrdersForPerson(
            @PathParam("id") @Positive long personIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Person person = entityManager.find(Person.class, personIdentity);
        if (person == null) {
            throw new ClientErrorException(NOT_FOUND);
        }
        return person.getOrders();
    }

    @DELETE
    @Path("{id}")
    public void deletePerson(
        @PathParam("id") @Positive long personIdentity,
        @HeaderParam("X-Requester-Identity") @Positive long requesterIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Person queriedPerson = entityManager.find(Person.class, personIdentity);
        Person requesterPerson = entityManager.find(Person.class, requesterIdentity);
        boolean isRequesterAdmin = requesterPerson != null && requesterPerson.getGroup() == Group.ADMIN;

        if (queriedPerson == null) {
            throw new ClientErrorException(NOT_FOUND);
        } else {
            if (isRequesterAdmin || requesterIdentity == queriedPerson.getIdentity()) {
                entityManager.remove(queriedPerson); // p. 14 commit transaction try-finally?
            } else {
                throw new ClientErrorException(FORBIDDEN);
            }
        }
    }

    @GET
    @Produces({ TEXT_PLAIN })
    public Person[] getFilteredPersons(
            @QueryParam("resultOffset") @PositiveOrZero Integer resultOffset,
            @QueryParam("resultLimit") @PositiveOrZero Integer resultLimit,
            @QueryParam("email") String email,
            @QueryParam("group") Group group,
            @QueryParam("givenName") String givenName,
            @QueryParam("familyName") String familyName,
            @QueryParam("title") String title,
            @QueryParam("country") String country,
            @QueryParam("postcode") String postcode,
            @QueryParam("street") String street,
            @QueryParam("city") String city,
//            @QueryParam("version") Integer version,
            @QueryParam("creationTimestamp") String creationTimestamp,
            @QueryParam("modificationTimestamp") String modificationTimestamp
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final String sqlQuery = "SELECT p.identity FROM Person AS p WHERE "
                + "(:email is null or p.email = :email) AND "
                + "(:group is null or p.group = :group) AND "
                + "(:givenName is null or p.name.given = :givenName) AND "
                + "(:familyName is null or p.name.family = :familyName) AND "
                + "(:title is null or p.name.title = :title) AND "
                + "(:country is null or p.address.country = :country) AND "
                + "(:postcode is null or p.address.postcode = :postcode) AND "
                + "(:street is null or p.address.street = :street) AND "
                + "(:city is null or p.address.city = :city)"
//                + "(:version is null or p.version = :version)"
                + "(:creationTimestamp is null or p.creationTimestamp = :creationTimestamp)"
                + "(:modificationTimestamp is null or p.modificationTimestamp = :modificationTimestamp)";
        final TypedQuery<Long> query = entityManager.createQuery(sqlQuery, Long.class);
        if(resultOffset != null) query.setFirstResult(resultOffset);
        if(resultLimit != null) query.setMaxResults(resultLimit);
        final Comparator<Person> personComparator = Comparator
                .comparing(Person::getName)
                .thenComparing(Person::getEmail);
        return query
                .setParameter("email", email)
                .setParameter("group", group)
                .setParameter("givenName", givenName)
                .setParameter("familyName", familyName)
                .setParameter("title", title)
                .setParameter("country", country)
                .setParameter("postcode", postcode)
                .setParameter("street", street)
                .setParameter("city", city)
//                .setParameter("version", version)
                .setParameter("creationTimestamp", creationTimestamp)
                .setParameter("modificationTimestamp", modificationTimestamp)
                .getResultList()
                .stream()
                .map(identity -> entityManager.find(Person.class, identity))
                .filter(person -> person != null)
                .sorted(personComparator)
                .toArray(Person[]::new);
    }

//    @POST
//    @Consumes({ TEXT_PLAIN })
//    @Produces({ TEXT_PLAIN })
//    public insertOrUpdatePerson(
//            @HeaderParam("Requester-Identity") @Positive final long requesterIdentity,
//            @HeaderParam("X-Set-Password") String password,
//            @QueryParam("avatarReference") Long avatarReference,
//            @NotNull @Valid Person personTemplate
//    ) {
//        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
//        final boolean shouldInsertEntity = personTemplate.getIdentity() == 0;
//        Person requesterPerson = entityManager.find(Person.class, requesterIdentity);
//        boolean isRequesterAdmin = requesterPerson != null && requesterPerson.getGroup() == Group.ADMIN;
//
//        if (shouldInsertEntity) {
//            entityManager
//        } else {
//            if (isRequesterAdmin || requesterIdentity == shouldInsertEntity.getIdentity()) {
//                entityManager.remove(shouldInsertEntity); // p. 14 commit transaction try-finally?
//            } else {
//                throw new ClientErrorException(FORBIDDEN);
//            }
//        }
//    }
}