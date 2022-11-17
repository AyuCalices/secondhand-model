package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Name {
@JsonbVisibility(JsonProtectedPropertyStrategy.class)

    @Size(max = 15)
    @Column(nullable = true, updatable = true, length = 15)
    private String title;
    @NotNull @Size(max = 31)
    @Column(name = "forename", nullable = false, updatable = true, length = 31)
    private String given;
    @NotNull @Size(max = 31)
    @Column(name = "surname", nullable = false, updatable = true, length = 31)
    private String family;

    @JsonbProperty
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
}
