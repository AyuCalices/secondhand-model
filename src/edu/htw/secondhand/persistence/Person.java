package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.HashCodes;
import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;
import org.eclipse.persistence.annotations.CacheIndex;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(schema = "secondhand", name = "Person")
@PrimaryKeyJoinColumn(name = "personIdentity")
@DiscriminatorValue(value = "Person")
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public class Person extends BaseEntity {

    static private final String DEFAULT_PASSWORD_HASH = HashCodes.sha2HashText(256, "changeit");

    @NotNull @Size(max = 128) @Email
    @Column(nullable = false, updatable = true, length = 128, unique = true)
    @CacheIndex(updateable = true)
    private String email;
    @NotNull @Size(min = 64, max = 64)
    @Column(nullable = false, updatable = true, length = 64)
    private String passwordHash;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "groupAlias", nullable = false, updatable = true)
    private Group group;
    @NotNull @Valid
    @Embedded
    private Name name;
    @NotNull @Valid
    @Embedded
    private Address address;
    @NotNull @Valid
    @Embedded
    private Account account;
    @NotNull
    @ElementCollection
    @CollectionTable(
            schema = "secondhand",
            name = "PhoneAssociation",
            joinColumns =  @JoinColumn(name = "personReference", nullable = false, updatable = false, insertable = true),
            uniqueConstraints = @UniqueConstraint(columnNames = {"personReference", "phone"})
    )
    @Column(name = "phone", nullable = false, updatable = false, insertable = true, length = 16)
    private Set<String> phones;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "avatarReference", nullable = false, updatable = true)
    private Document avatar;
    @OneToMany(mappedBy = "seller", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Offer> offers;
    @OneToMany(mappedBy = "buyer", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Order> orders;

    public Person(){
        this.passwordHash = DEFAULT_PASSWORD_HASH;
        this.group = Group.USER;
        this.name = new Name();
        this.address = new Address();
        this.account = new Account();
        this.phones = new HashSet<>();
        this.offers = Collections.emptySet();
        this.orders = Collections.emptySet();
    }

    @JsonbProperty
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonbProperty
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @JsonbProperty
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @JsonbProperty
    public Name getName() {
        return name;
    }

    protected void setName(Name name) {
        this.name = name;
    }

    @JsonbProperty
    public Address getAddress() {
        return address;
    }

    protected void setAddress(Address address) {
        this.address = address;
    }

    @JsonbProperty
    public Account getAccount() {
        return account;
    }

    protected void setAccount(Account account) {
        this.account = account;
    }

    @JsonbProperty
    public Set<String> getPhones() {
        return phones;
    }

    protected void setPhones(Set<String> phones) {
        this.phones = phones;
    }

    @JsonbTransient
    public Document getAvatar() {
        return avatar;
    }

    public void setAvatar(Document avatar) {
        this.avatar = avatar;
    }

    @JsonbTransient
    public Set<Offer> getOffers() {
        return offers;
    }

    protected void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    @JsonbTransient
    public Set<Order> getOrders() {
        return orders;
    }

    protected void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @JsonbProperty
    protected Long getAvatarReference() {
        return avatar.getIdentity();
    }

    @JsonbProperty
    protected long[] getOfferReferences() {
        return offers.stream().mapToLong(x -> x.getIdentity()).sorted().toArray();
    }

    @JsonbProperty
    protected long[] getOrderReferences() {
        return orders.stream().mapToLong(x -> x.getIdentity()).sorted().toArray();
    }

}
