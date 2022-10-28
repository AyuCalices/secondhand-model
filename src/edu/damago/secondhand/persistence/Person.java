package edu.damago.secondhand.persistence;

import edu.damago.secondhand.util.HashCodes;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(name = "Person", schema = "secondhand") // TODO schema taken from BaseEntity... correct?
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn() // TODO not sure if this needs an argument
@PrimaryKeyJoinColumn(name = "identity") // TODO to inherit and set the id?
public class Person extends BaseEntity {
    @Column(nullable = false, updatable = true)
    @Email
    private String email;
    @Column(nullable = false, updatable = true, length = 64)
    private String passwordHash;
    @Column(nullable = false, updatable = true)
    @Enumerated(EnumType.STRING)
    private Group group;
//    @Column(nullable = false, updatable = true)
    @Embedded // TODO embedding right? not sure if this is a composit
    private Name name;
//    @Column(nullable = false, updatable = true)
    @Embedded
    private Address address;
//    @Column(nullable = false, updatable = true)
    @Embedded
    private Account account;
    @Column(nullable = false, updatable = true)
    private String[] phones;
    @ManyToOne
    @JoinColumn(nullable = false, updatable = true)
    private Document avatar;
    @OneToMany
    @JoinColumn(nullable = true, updatable = true)
    private Offer[] offers;
    @OneToMany
    @JoinColumn(nullable = true, updatable = true)
    private Order[] orders;

    protected Person(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = HashCodes.sha2HashText(256, password);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Name getName() {
        return name;
    }

    protected void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    protected void setAddress(Address address) {
        this.address = address;
    }

    public Account getPrimaryAccount() {
        return account;
    }

    protected void setPrimaryAccount(Account account) {
        this.account = account;
    }

    public String[] getPhones() {
        return phones;
    }

    protected void setPhones(String[] phones) {
        this.phones = phones;
    }

    public Document getAvatar() {
        return avatar;
    }

    public void setAvatar(Document avatar) {
        this.avatar = avatar;
    }

    public Offer[] getOffers() {
        return offers;
    }

    protected void setOffers(Offer[] offers) {
        this.offers = offers;
    }

    public Order[] getOrders() {
        return orders;
    }

    protected void setOrders(Order[] orders) {
        this.orders = orders;
    }
}
