package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Entity
@Table(schema = "secondhand", name = "Offer")
@PrimaryKeyJoinColumn(name="offerIdentity")
@DiscriminatorValue(value = "Offer")
public class Offer extends BaseEntity{

    @NotNull @Valid
    @Embedded
    private Article article;
    @NotNull @Size(max = 32)
    @Column(nullable = false, updatable = false, length = 32)
    @CacheIndex(updateable = false)
    private String serial;
    @NotNull @Positive
    @Column(nullable = false, updatable = true)
    private Long price;
    @NotNull @Positive
    @Column(nullable = false, updatable = true)
    private Long postage;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sellerReference", nullable = false, updatable = true)
    private Person seller;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "avatarReference", nullable = false, updatable = true)
    private Document avatar;

    @ManyToOne(optional = true)
    @JoinColumn(name = "purchaseReference", nullable = true, updatable = true)
    private Order order;

    protected Offer() {}
    public Offer(String serial, Long price, Long postage, Person seller, Document avatar) {
        this.serial = serial;
        this.price = price;//generate?
        this.postage = postage;
        this.seller = seller;
        this.avatar = avatar;
    }

    public Article getArticle() { return article; }

    public void setArticle(Article article) { this.article = article; }

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

    public Person getSeller() {
        return seller;
    }

    protected void setSeller(Person seller) {
        this.seller = seller;
    }

    public Document getAvatar() {
        return avatar;
    }

    public void setAvatar(Document avatarReference) {
        this.avatar = avatarReference;
    }

    public Order getOrder() {
        return order;
    }

    protected void setOrder(Order order) {
        this.order = order;
    }
}