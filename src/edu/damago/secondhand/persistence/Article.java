package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Article {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = true)
    private Category category;
    @NotNull @Size(max = 32)
    @CacheIndex(updateable = false)
    @Column(nullable = false, updatable = true, length = 32)
    private String brand;
    @NotNull @Size(max = 64)
    @Column(nullable = false, updatable = true, length = 64)
    private String alias;
    @NotNull @Size(max = 4090)
    @Column(nullable = false, updatable = true, length = 4090)
    private String description;

    public Article() {}

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
