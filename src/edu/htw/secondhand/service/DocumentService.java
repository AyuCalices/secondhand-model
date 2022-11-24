package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Document;
import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.HashCodes;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.htw.secondhand.service.BasicAuthenticationReceiverFilter.REQUESTER_IDENTITY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("documents")
public class DocumentService {

    private static final String QUERY_DOCUMENT = "SELECT d FROM Document AS d WHERE d.hash = :hash";

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Document findDocumentMetadata(
        @PathParam("id") @Positive final long documentIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Document document = entityManager.find(Document.class, documentIdentity);
        if (document == null) throw new ClientErrorException(NOT_FOUND);
        return document;
    }

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN, "application/pdf", "image/*", "audio/*", "video/*" })
    public Response findDocumentContent(
        @PathParam("id") @Positive final long documentIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Document document = entityManager.find(Document.class, documentIdentity);
        if (document == null) throw new ClientErrorException(NOT_FOUND);
        return Response.ok(document.getContent(), document.getType()).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(TEXT_PLAIN)
    public long deleteDocument(
        @PathParam("id") @Positive final long documentIdentity
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final Document document = entityManager.find(Document.class, documentIdentity);
        if (document == null) throw new ClientErrorException(NOT_FOUND);
        entityManager.remove(document);

        try {
            entityManager.getTransaction().commit();
        } finally {
            entityManager.getTransaction().begin();
        }

        return document.getIdentity();
    }

    @POST
    @Consumes("*/*")
    @Produces(TEXT_PLAIN)
    public Long createOrUpdateDocument(
        @HeaderParam("Content-Type") @NotNull final String documentContentType,
        @NotNull final byte[] documentContent
    ) {
        final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        final TypedQuery<Document> query = entityManager.createQuery(QUERY_DOCUMENT, Document.class);
        final String contentHash = HashCodes.sha2HashText(256, documentContent);
        final Document document = query
                .setParameter("hash", contentHash)
                .getResultList()
                .stream()
                .findAny()
                .orElseGet(() -> new Document(documentContent));

        document.setType(documentContentType);

        if(document.getIdentity() == 0)
            entityManager.persist(document);
        else
            entityManager.flush();

        try {
            entityManager.getTransaction().commit();
        } catch (final RollbackException e) {
            throw new ClientErrorException(CONFLICT);
        } finally {
            entityManager.getTransaction().begin();
        }

        return document.getIdentity();
    }

}