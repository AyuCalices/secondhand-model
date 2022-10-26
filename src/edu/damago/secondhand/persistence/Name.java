package edu.damago.secondhand.persistence;

public class Name {
    private char[] title;
    private char[] given;
    private char[] family;

    protected Name() {}

    public char[] getTitle() {
        return title;
    }

    public void setTitle(char[] title) {
        this.title = title;
    }

    public char[] getGiven() {
        return given;
    }

    public void setGiven(char[] given) {
        this.given = given;
    }

    public char[] getFamily() {
        return family;
    }

    public void setFamily(char[] family) {
        this.family = family;
    }
}
