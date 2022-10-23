package edu.damago.secondhand.persistence;

import javax.persistence.*;

@Entity
@Table(name = "DocumentAssociation", indexes = {
        @Index(name = "documentReference", columnList = "documentReference")
})
public class DocumentAssociation {
    @EmbeddedId
    private DocumentAssociationId id;

    @MapsId("entityReference")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entityReference", nullable = false)
    private BaseEntity entityReference;

    @MapsId("documentReference")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documentReference", nullable = false)
    private Document documentReference;

    public DocumentAssociationId getId() {
        return id;
    }

    public void setId(DocumentAssociationId id) {
        this.id = id;
    }

    public BaseEntity getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(BaseEntity entityReference) {
        this.entityReference = entityReference;
    }

    public Document getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(Document documentReference) {
        this.documentReference = documentReference;
    }

}