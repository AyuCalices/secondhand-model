package edu.damago.secondhand.persistence;

import javax.persistence.*;

@Entity
@Table(name = "Person", indexes = {
        @Index(name = "email", columnList = "email", unique = true),
        @Index(name = "avatarReference", columnList = "avatarReference")
})
public class Person {
    @Id
    @Column(name = "personIdentity", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personIdentity", nullable = false)
    private BaseEntity baseEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "avatarReference", nullable = false)
    private Document avatarReference;

    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Column(name = "passwordHash", nullable = false, length = 64)
    private String passwordHash;

    @Lob
    @Column(name = "groupAlias", nullable = false)
    private String groupAlias;

    @Column(name = "title", length = 15)
    private String title;

    @Column(name = "surname", nullable = false, length = 31)
    private String surname;

    @Column(name = "forename", nullable = false, length = 31)
    private String forename;

    @Column(name = "street", nullable = false, length = 63)
    private String street;

    @Column(name = "city", nullable = false, length = 63)
    private String city;

    @Column(name = "country", nullable = false, length = 63)
    private String country;

    @Column(name = "postcode", nullable = false, length = 15)
    private String postcode;

    @Column(name = "iban", nullable = false, length = 32)
    private String iban;

    @Column(name = "bic", nullable = false, length = 11)
    private String bic;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getGroupAlias() {
        return groupAlias;
    }

    public void setGroupAlias(String groupAlias) {
        this.groupAlias = groupAlias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

}