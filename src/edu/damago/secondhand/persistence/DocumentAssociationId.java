package edu.damago.secondhand.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DocumentAssociationId implements Serializable {
    private static final long serialVersionUID = -9211652857735788579L;
    @Column(name = "entityReference", nullable = false)
    private Long entityReference;

    @Column(name = "documentReference", nullable = false)
    private Long documentReference;

    public Long getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(Long entityReference) {
        this.entityReference = entityReference;
    }

    public Long getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(Long documentReference) {
        this.documentReference = documentReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentAssociationId entity = (DocumentAssociationId) o;
        return Objects.equals(this.entityReference, entity.entityReference) &&
                Objects.equals(this.documentReference, entity.documentReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityReference, documentReference);
    }

}