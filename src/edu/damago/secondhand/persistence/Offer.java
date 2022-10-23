package edu.damago.secondhand.persistence;

import javax.persistence.*;

@Entity
@Table(name = "Offer", indexes = {
        @Index(name = "alias", columnList = "alias"),
        @Index(name = "purchaseReference", columnList = "purchaseReference"),
        @Index(name = "sellerReference", columnList = "sellerReference"),
        @Index(name = "category", columnList = "category"),
        @Index(name = "brand", columnList = "brand, serial", unique = true),
        @Index(name = "avatarReference", columnList = "avatarReference")
})
public class Offer {
    @Id
    @Column(name = "offerIdentity", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offerIdentity", nullable = false)
    private BaseEntity baseEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "avatarReference", nullable = false)
    private Document avatarReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sellerReference", nullable = false)
    private Person sellerReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaseReference")
    private Purchase purchaseReference;

    @Lob
    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "brand", nullable = false, length = 32)
    private String brand;

    @Column(name = "alias", nullable = false, length = 64)
    private String alias;

    @Column(name = "description", nullable = false, length = 4090)
    private String description;

    @Column(name = "serial", length = 32)
    private String serial;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "postage", nullable = false)
    private Long postage;

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

    public Document getAvatarReference() {
        return avatarReference;
    }

    public void setAvatarReference(Document avatarReference) {
        this.avatarReference = avatarReference;
    }

    public Person getSellerReference() {
        return sellerReference;
    }

    public void setSellerReference(Person sellerReference) {
        this.sellerReference = sellerReference;
    }

    public Purchase getPurchaseReference() {
        return purchaseReference;
    }

    public void setPurchaseReference(Purchase purchaseReference) {
        this.purchaseReference = purchaseReference;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getPostage() {
        return postage;
    }

    public void setPostage(Long postage) {
        this.postage = postage;
    }

}