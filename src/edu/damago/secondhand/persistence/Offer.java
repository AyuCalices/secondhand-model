package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
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

    @ManyToOne
    @JoinColumn
    private Purchase purchaseReference;

    @Column
    private String serial;

    @Positive
    @Column
    private Long price;

    @Column
    private Long postage;

    protected Offer(Person sellerReference, Document avatarReference, Order order) {
        setSellerReference(sellerReference);
        setAvatarReference(avatarReference);
        setOrder(order);
    }
    protected Offer(Person sellerReference, Document avatarReference) {
        setSellerReference(sellerReference);
        setAvatarReference(avatarReference);
    }

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

    public Purchase getPurchaseReference() {
        return purchaseReference;
    }

    public void setPurchaseReference(Purchase purchaseReference) {
        this.purchaseReference = purchaseReference;
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