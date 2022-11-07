package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;


@Entity
@Table(schema = "secondhand", name = "Purchase")
@PrimaryKeyJoinColumn(name = "purchaseIdentity")
@DiscriminatorValue(value = "Purchase")
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


    public Long getPayed() {
        return payed;
    }

    public void setPayed(Long payed) {
        this.payed = payed;
    }

    public Long getDeparted() {
        return departed;
    }

    public void setDeparted(Long departed) {
        this.departed = departed;
    }

    public Long getArrived() {
        return arrived;
    }

    public void setArrived(Long arrived) {
        this.arrived = arrived;
    }

    public String getTrackingReference() {
        return trackingReference;
    }

    public void setTrackingReference(String trackingReference) {
        this.trackingReference = trackingReference;
    }

    public Person getBuyer() {
        return buyer;
    }

    protected void setBuyer(Person buyer) {
        this.buyer = buyer;
    }

    public Set<Offer> getOffers() {
        return offers;
    }

    protected void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }
}
