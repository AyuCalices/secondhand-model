package edu.damago.secondhand.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PhoneAssociationId implements Serializable {
    private static final long serialVersionUID = -6467735998458732379L;
    @Column(name = "personReference", nullable = false)
    private Long personReference;

    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    public Long getPersonReference() {
        return personReference;
    }

    public void setPersonReference(Long personReference) {
        this.personReference = personReference;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneAssociationId entity = (PhoneAssociationId) o;
        return Objects.equals(this.phone, entity.phone) &&
                Objects.equals(this.personReference, entity.personReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone, personReference);
    }

}