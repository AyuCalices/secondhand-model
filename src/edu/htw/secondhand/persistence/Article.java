package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public class Article {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = true)
    private Category category;
    @NotNull @Size(max = 32)
    @Column(nullable = false, updatable = true, length = 32)
    private String brand;
    @NotNull @Size(max = 64)
    @Column(nullable = false, updatable = true, length = 64)
    private String alias;
    @NotNull @Size(max = 4090)
    @Column(nullable = false, updatable = true, length = 4090)
    private String description;

    @JsonbProperty
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @JsonbProperty
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonbProperty
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonbProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
