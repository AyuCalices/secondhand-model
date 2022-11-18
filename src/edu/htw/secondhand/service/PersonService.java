package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.*;
import edu.htw.secondhand.util.HashCodes;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.Comparator;

import static edu.htw.secondhand.service.BasicAuthenticationReceiverFilter.REQUESTER_IDENTITY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.*;

@Path("people")
public class PersonService {

    static private final String QUERY_PEOPLE = "SELECT p.identity FROM Person AS p WHERE "
            + "(:lowerCreationTimestamp is null or p.created >= :lowerCreationTimestamp) AND "
            + "(:upperCreationTimestamp is null or p.created <= :upperCreationTimestamp) AND "
            + "(:lowerModificationTimestamp is null or p.modified >= :lowerModificationTimestamp) AND "
            + "(:upperModificationTimestamp is null or p.modified <= :upperModificationTimestamp) AND "
            + "(:email is null or p.email = :email) AND "
            + "(:group is null or p.group = :group) AND "
            + "(:givenName is null or p.name.given = :givenName) AND "
            + "(:familyName is null or p.name.family = :familyName) AND "
            + "(:title is null or p.name.title = :title) AND "
            + "(:country is null or p.address.country = :country) AND "
            + "(:postcode is null or p.address.postcode = :postcode) AND "
            + "(:street is null or p.address.street = :street) AND "
            + "(:city is null or p.address.city = :city) AND "
            + "(:bic is null or p.account.bic = :bic) AND "
            + "(:iban is null or p.account.iban = :iban)";

    static private final Comparator<Person> PERSON_COMPARATOR = Comparator
            .comparing(Person::getName)
            .thenComparing(Person::getEmail);
    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Person findPerson(
        @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
        @PathParam("id") @PositiveOrZero final long personIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final long identity = personIdentity == 0 ? requesterIdentity : personIdentity;
        final Person person = entityManager.find(Person.class, identity);
        if (person == null) throw new ClientErrorException(NOT_FOUND);
        return person;
    }

    @GET
    @Path("{id}/avatar")
    @Produces("image/*")
    public Response findAvatar(
       @PathParam("id") @Positive final long personIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person person = entityManager.find(Person.class, personIdentity);
        if (person == null) throw new ClientErrorException(NOT_FOUND);

        final Document avatar = person.getAvatar();
        return Response.ok(avatar.getContent(), avatar.getType()).build();
    }

    @GET
    @Path("{id}/offers")
    @Produces(APPLICATION_JSON)
    public Offer[] findOffers(
            @PathParam("id") @Positive final long personIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person person = entityManager.find(Person.class, personIdentity);
        if (person == null) throw new ClientErrorException(NOT_FOUND);
        return person.getOffers().stream().sorted().toArray(Offer[]::new);
    }

    @GET
    @Path("{id}/orders")
    @Produces(APPLICATION_JSON)
    public Order[] findOrders(
            @PathParam("id") @Positive final long personIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person person = entityManager.find(Person.class, personIdentity);
        if (person == null) throw new ClientErrorException(NOT_FOUND);
        return person.getOrders().stream().sorted().toArray(Order[]::new);
    }

    @DELETE
    @Path("{id}")
    public long deletePerson(
        @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
        @PathParam("id") @Positive final long personIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person person = entityManager.find(Person.class, personIdentity);
        final Person requester = entityManager.find(Person.class, requesterIdentity);

        final boolean isRequesterAdmin = requester != null && requester.getGroup() == Group.ADMIN;
        if (person == null) throw new ClientErrorException(NOT_FOUND);
        if (!isRequesterAdmin && requesterIdentity != person.getIdentity()) throw new ClientErrorException(FORBIDDEN);
        entityManager.remove(person);

        try {
            entityManager.getTransaction().commit();
        } finally {
            entityManager.getTransaction().begin();
        }

        return person.getIdentity();
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Person[] queryPeople(
            @QueryParam("resultOffset") @PositiveOrZero final Integer resultOffset,
            @QueryParam("resultLimit") @PositiveOrZero final Integer resultLimit,
            @QueryParam("lowerCreationTimestamp") final Long lowerCreationTimestamp,
            @QueryParam("upperCreationTimestamp") final Long upperCreationTimestamp,
            @QueryParam("lowerModificationTimestamp") final Long lowerModificationTimestamp,
            @QueryParam("upperModificationTimestamp") final Long upperModificationTimestamp,
            @QueryParam("email") final String email,
            @QueryParam("group") final Group group,
            @QueryParam("title") final String title,
            @QueryParam("givenName") final String givenName,
            @QueryParam("familyName") final String familyName,
            @QueryParam("street") final String street,
            @QueryParam("city") final String city,
            @QueryParam("country") final String country,
            @QueryParam("postcode") final String postcode,
            @QueryParam("bic") final String bic,
            @QueryParam("iban") final String iban
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");

        final TypedQuery<Long> query = entityManager.createQuery(QUERY_PEOPLE, Long.class);
        if (resultOffset != null) query.setFirstResult(resultOffset);
        if (resultLimit != null) query.setMaxResults(resultLimit);

        final Person[] people = query
                .setParameter("lowerCreationTimestamp", lowerCreationTimestamp)
                .setParameter("upperCreationTimestamp", upperCreationTimestamp)
                .setParameter("lowerModificationTimestamp", lowerModificationTimestamp)
                .setParameter("upperModificationTimestamp", upperModificationTimestamp)
                .setParameter("email", email)
                .setParameter("group", group)
                .setParameter("title", title)
                .setParameter("givenName", givenName)
                .setParameter("familyName", familyName)
                .setParameter("street", street)
                .setParameter("city", city)
                .setParameter("country", country)
                .setParameter("postcode", postcode)
                .setParameter("bic", bic)
                .setParameter("iban", iban)
                .getResultList()
                .stream()
                .map(identity -> entityManager.find(Person.class, identity))
                .filter(person -> person != null)
                .sorted(PERSON_COMPARATOR)
                .toArray(Person[]::new);

        return people;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_PLAIN)
    public long insertOrUpdatePerson(
            @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
            @HeaderParam("X-Set-Password") @Size(min = 3) final String password,
            @QueryParam("avatarReference") @Positive Long avatarReference,
            @NotNull @Valid final Person personTemplate
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Person requester = entityManager.find(Person.class, requesterIdentity);
        if (requester == null) throw new ClientErrorException(FORBIDDEN);

        final boolean insertMode = personTemplate.getIdentity() == 0;
        boolean isRequesterAdmin = requester.getGroup() == Group.ADMIN;

        final Person person;
        if (insertMode) {
            person = new Person();
            if (avatarReference == null) avatarReference = 1L;
        } else {
            person = entityManager.find(Person.class, personTemplate.getIdentity());
            if (person == null) throw new ClientErrorException(NOT_FOUND);
        }

        person.setVersion(personTemplate.getVersion());
        person.setModified(System.currentTimeMillis());
        person.setEmail(personTemplate.getEmail());
        if (requester.getGroup() == Group.ADMIN) person.setGroup(personTemplate.getGroup());
        person.getName().setTitle(personTemplate.getName().getTitle());
        person.getName().setGiven(personTemplate.getName().getGiven());
        person.getName().setFamily(personTemplate.getName().getFamily());
        person.getAddress().setStreet(personTemplate.getAddress().getStreet());
        person.getAddress().setCity(personTemplate.getAddress().getCity());
        person.getAddress().setCountry(personTemplate.getAddress().getCountry());
        person.getAddress().setPostcode(personTemplate.getAddress().getPostcode());
        person.getAccount().setBic(personTemplate.getAccount().getBic());
        person.getAccount().setIban(personTemplate.getAccount().getIban());
        person.getPhones().retainAll(personTemplate.getPhones());
        person.getPhones().addAll(personTemplate.getPhones());
        if (password != null) person.setPasswordHash(HashCodes.sha2HashText(256, password));
        if (avatarReference != null) {
            final Document avatar = entityManager.find(Document.class, avatarReference);
            if (avatar == null) throw new ClientErrorException(NOT_FOUND);
            person.setAvatar(avatar);
        }

        if (insertMode)
            entityManager.persist(person);
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
        return person.getIdentity();
    }
}