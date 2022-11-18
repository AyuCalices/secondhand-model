package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Document;
import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.persistence.EntityManager;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("documents")
public class DocumentService {

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public Document getDocumentById(
        @PathParam("id") long documentIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Document document = entityManager.find(Document.class, documentIdentity);
        if (document == null) {
            throw new ClientErrorException(NOT_FOUND);
        }
        return document;
    }

    @GET
    @Path("{id}")
    @Produces({ TEXT_PLAIN })
    public Response getDocumentContentAndTypeById(
        @PathParam("id") long documentIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Document document = entityManager.find(Document.class, documentIdentity);
        if (document == null) {
            throw new ClientErrorException(NOT_FOUND);
        }
        return Response.ok(document.getContent(), document.getType()).build();
    }

    @DELETE
    @Path("{id}")
    public void deleteDocumentById(
        @PathParam("id") long documentIdentity
    ) {
        EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");
        Document document = entityManager.find(Document.class, documentIdentity);
        if (document == null) {
            throw new ClientErrorException(NOT_FOUND);
        }
        entityManager.remove(document); // commit transaction try-finally ?
    }

    @POST
    @Path("{id}")
    public Long createOrUpdateDocument(
        @PathParam("id") long documentIdentity
    ) {
        return Long.getLong("7");
    }

}