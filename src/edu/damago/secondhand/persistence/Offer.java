package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="discriminator")
@Table(name = "Offer", indexes = {
        @Index(name = "alias", columnList = "alias"),
        @Index(name = "purchaseReference", columnList = "purchaseReference"),
        @Index(name = "sellerReference", columnList = "sellerReference"),
        @Index(name = "category", columnList = "category"),
        @Index(name = "brand", columnList = "brand, serial", unique = true),
        @Index(name = "avatarReference", columnList = "avatarReference")
})
public class Offer extends BaseEntity{

    @Embedded
    @NotNull
    @ManyToOne
    @JoinColumn
    private Document avatarReference;
    @Embedded
    @NotNull
    @ManyToOne
    @JoinColumn
    private Person sellerReference;
    @Embedded
    @OneToMany
    @JoinColumn
    private Order order;
    @Column
    private String serial;
    @Positive
    @Column
    private Long price;
    @Positive
    @Column
    private Long postage;

    protected Offer() {}

    public Document getAvatarReference() {
        return avatarReference;
    }

    public void setAvatarReference(Document avatarReference) {
        this.avatarReference = avatarReference;
    }

    public Person getSellerReference() {
        return sellerReference;
    }

    protected void setSellerReference(Person sellerReference) {
        this.sellerReference = sellerReference;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    protected void setOrder(Order order) {
        this.order = order;
    }

    public Long getPostage() {
        return postage;
    }

    public void setPostage(Long postage) {
        this.postage = postage;
    }

}