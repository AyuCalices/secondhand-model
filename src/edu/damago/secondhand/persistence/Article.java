package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Embeddable
public class Article {
    @Enumerated(EnumType.STRING)
    @Column
    private Category category;
    @Column
    @CacheIndex(updateable = false)
    private String brand;
    @Column
    private String alias;
    @Column
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
