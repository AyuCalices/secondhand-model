package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="discriminator")
@Table(name = "Offer")
public class Offer extends BaseEntity{

    @Embedded
    @NotNull
    @ManyToOne
    @JoinColumn
    private Document avatar;
    @Embedded
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sellerReference", nullable = false, updatable = true)
    private Person seller;
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
        return avatar;
    }

    public void setAvatarReference(Document avatarReference) {
        this.avatar = avatarReference;
    }

    public Person getSeller() {
        return seller;
    }

    protected void setSeller(Person seller) {
        this.seller = seller;
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