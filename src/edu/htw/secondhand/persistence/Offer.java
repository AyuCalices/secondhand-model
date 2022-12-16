package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Entity
@Table(schema = "secondhand", name = "Offer")
@PrimaryKeyJoinColumn(name="offerIdentity")
@DiscriminatorValue(value = "Offer")
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public class Offer extends BaseEntity{

    @NotNull @Valid
    @Embedded
    private Article article;

    @NotNull @Size(max = 32)
    @Column(nullable = false, updatable = true, length = 32)
    private String serial;

    @PositiveOrZero
    @Column(nullable = false, updatable = true)
    private long price;

    @PositiveOrZero
    @Column(nullable = false, updatable = true)
    private long postage;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sellerReference", nullable = false, updatable = false, insertable = true)
    private Person seller;

    @ManyToOne(optional = false)
    @JoinColumn(name = "avatarReference", nullable = false, updatable = true)
    private Document avatar;

    @ManyToOne(optional = true)
    @JoinColumn(name = "purchaseReference", nullable = true, updatable = true)
    private Order order;

    protected Offer() {
        this(null);
    }
    public Offer(Person seller) {
        this.seller = seller;
    }

    @JsonbProperty
    public Article getArticle() { return article; }

    protected void setArticle(Article article) { this.article = article; }

    @JsonbProperty(nillable = true)
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @JsonbProperty
    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @JsonbProperty
    public long getPostage() {
        return postage;
    }

    public void setPostage(long postage) {
        this.postage = postage;
    }

    @JsonbTransient
    public Person getSeller() {
        return seller;
    }

    protected void setSeller(Person seller) {
        this.seller = seller;
    }

    @JsonbTransient
    public Document getAvatar() {
        return avatar;
    }

    public void setAvatar(Document avatarReference) {
        this.avatar = avatarReference;
    }

    @JsonbTransient
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @JsonbProperty
    protected Long getAvatarReference() {
        return this.avatar == null ? null : this.avatar.getIdentity();
    }

    @JsonbProperty
    protected Long getSellerReference() {
        return this.seller == null ? null : this.seller.getIdentity();
    }

    @JsonbProperty(nillable = true)
    protected Long getOrderReference() {
        return this.order == null ? null : this.order.getIdentity();
    }
}