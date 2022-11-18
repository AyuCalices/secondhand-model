package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Document;
import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.htw.secondhand.service.BasicAuthenticationReceiverFilter.REQUESTER_IDENTITY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("documents")
public class DocumentService {

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
    public Long createOrUpdateDocument(
        @HeaderParam(REQUESTER_IDENTITY) @Positive final long requesterIdentity,
        @HeaderParam("Content-Type") @NotNull final String documentType,
        @NotNull final byte[] documentContent
    ) {
        return Long.getLong("7");
    }

}