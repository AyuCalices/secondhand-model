package edu.damago.secondhand.persistence;

public class Person {
    private char[] email;
    private String passwordHash;
    private Group group;
    private Name name;
    private Address address;
    private Account account;
    private char[][] phones;
    private Document avatar;
    private Offer[] offers;
    //private Order[] orders;

    protected Person(){}

    public char[] getEmail() {
        return email;
    }

    public void setEmail(char[] email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public char[][] getPhones() {
        return phones;
    }

    protected void setPhones(char[][] phones) {
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
}