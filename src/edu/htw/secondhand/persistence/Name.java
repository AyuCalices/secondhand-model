package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Comparator;

@Embeddable
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public class Name implements Comparable<Name> {

    @Size(max = 15)
    @Column(nullable = true, updatable = true, length = 15)
    private String title;
    @NotNull @Size(max = 31)
    @Column(name = "forename", nullable = false, updatable = true, length = 31)
    private String given;
    @NotNull @Size(max = 31)
    @Column(name = "surname", nullable = false, updatable = true, length = 31)
    private String family;

    @JsonbProperty(nillable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonbProperty
    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    @JsonbProperty
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    @Override
    public int compareTo(Name o) {
        return Comparator
                .comparing(Name::getTitle, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Name::getFamily)
                .thenComparing(Name::getGiven)
                .compare(this, o);
    }
}
