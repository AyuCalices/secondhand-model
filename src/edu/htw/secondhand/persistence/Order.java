package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;


@Entity
@Table(schema = "secondhand", name = "Purchase")
@PrimaryKeyJoinColumn(name = "purchaseIdentity")
@DiscriminatorValue(value = "Purchase")
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public class Order extends BaseEntity {

    @Column(nullable = true, updatable = true)
    private Long payed;
    @Column(nullable = true, updatable = true)
    private Long departed;
    @Column(nullable = true, updatable = true)
    private Long arrived;
    @Size(max = 255)
    @Column(nullable = true, updatable = true, length = 255)
    private String trackingReference;
    @ManyToOne(optional = false)
    @JoinColumn(name = "buyerReference", nullable = false, updatable = false, insertable = true)
    private Person buyer;
    @OneToMany(mappedBy = "order", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Offer> offers;

    protected Order() {
        this(null);
    }
    public Order(Person buyer) {
        this.buyer = buyer;
        this.offers = Collections.emptySet();
    }

    @JsonbProperty(nillable = true)
    public Long getPayed() {
        return payed;
    }

    public void setPayed(Long payed) {
        this.payed = payed;
    }

    @JsonbProperty(nillable = true)
    public Long getDeparted() {
        return departed;
    }

    public void setDeparted(Long departed) {
        this.departed = departed;
    }

    @JsonbProperty(nillable = true)
    public Long getArrived() {
        return arrived;
    }

    public void setArrived(Long arrived) {
        this.arrived = arrived;
    }

    @JsonbProperty(nillable = true)
    public String getTrackingReference() {
        return trackingReference;
    }

    public void setTrackingReference(String trackingReference) {
        this.trackingReference = trackingReference;
    }

    @JsonbTransient
    public Person getBuyer() {
        return buyer;
    }

    protected void setBuyer(Person buyer) {
        this.buyer = buyer;
    }

    @JsonbTransient
    public Set<Offer> getOffers() {
        return offers;
    }

    protected void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    @JsonbProperty
    protected long[] getOfferReferences() {
        return offers.stream().mapToLong(Offer::getIdentity).sorted().toArray();
    }

    @JsonbProperty(nillable = true)
    protected Long getBuyerReference() {
        return this.buyer == null ? null : this.buyer.getIdentity();
    }

    @JsonbProperty(nillable = true)
    public Long getSellerReference() {
        return this.offers.stream().map(Offer::getSellerReference).findFirst().orElse(null);
    }
}
