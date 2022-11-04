package edu.damago.secondhand.persistence;

import org.eclipse.persistence.annotations.CacheIndex;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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

    public Article getArticle() { return article; }

    protected void setArticle(Article article) { this.article = article; }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getPostage() {
        return postage;
    }

    public void setPostage(long postage) {
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

    public void setOrder(Order order) {
        this.order = order;
    }
}