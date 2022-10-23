package edu.damago.secondhand.persistence;

import javax.persistence.*;

@Entity
public class PhoneAssociation {
    @EmbeddedId
    private PhoneAssociationId id;

    @MapsId("personReference")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personReference", nullable = false)
    private Person personReference;

    public PhoneAssociationId getId() {
        return id;
    }

    public void setId(PhoneAssociationId id) {
        this.id = id;
    }

    public Person getPersonReference() {
        return personReference;
    }

    public void setPersonReference(Person personReference) {
        this.personReference = personReference;
    }

}