package edu.damago.secondhand.persistence;

import javax.persistence.*;

@Entity
@Table(name = "Purchase", indexes = {
        @Index(name = "buyerReference", columnList = "buyerReference")
})
public class Purchase {
    @Id
    @Column(name = "purchaseIdentity", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchaseIdentity", nullable = false)
    private BaseEntity baseEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyerReference", nullable = false)
    private Person buyerReference;

    @Column(name = "payed")
    private Long payed;

    @Column(name = "departed")
    private Long departed;

    @Column(name = "arrived")
    private Long arrived;

    @Column(name = "trackingReference")
    private String trackingReference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    public Person getBuyerReference() {
        return buyerReference;
    }

    public void setBuyerReference(Person buyerReference) {
        this.buyerReference = buyerReference;
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

}