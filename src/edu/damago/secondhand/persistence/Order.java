package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Embeddable
@Table(name = "Order", indexes = @Index(columnList = "discriminator"))
public class Order extends BaseEntity {

    @Column(nullable = true, updatable = true)
    private Long payed;
    @Column(nullable = true, updatable = true)
    private Long departed;
    @Column(nullable = true, updatable = true)
    private Long arrived;
    @Column(nullable = true, updatable = true)
    private String trackingReference;
    @NotNull
    @Embedded
    @OneToMany(mappedBy = "Person")
    private Person buyer;
    @Embedded
    @ManyToOne
    @ElementCollection
    @CollectionTable
    @Column(updatable = false)
    private Offer[] offers;


    protected Order() {}


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

    public Offer[] getOffers() {
        return offers;
    }

    protected void setOffers(Offer[] offers) {
        this.offers = offers;
    }
}
