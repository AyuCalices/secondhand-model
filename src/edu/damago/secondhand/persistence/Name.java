package edu.damago.secondhand.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

    @Column(nullable = true, updatable = true)
    private String title;
    @Column(nullable = false, updatable = true)
    private String given; // TODO does given stand for a mandatory forename?
    @Column(nullable = true, updatable = true) // TODO then -> nullable
    private String family;

    protected Name() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }
}
