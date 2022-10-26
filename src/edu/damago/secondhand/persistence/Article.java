package edu.damago.secondhand.persistence;

public class Article {
    private Category category;
    private char[] brand;
    private char[] alias;
    private char[] description;

    protected Article() {}

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public char[] getBrand() {
        return brand;
    }

    public void setBrand(char[] brand) {
        this.brand = brand;
    }

    public char[] getAlias() {
        return alias;
    }

    public void setAlias(char[] alias) {
        this.alias = alias;
    }

    public char[] getDescription() {
        return description;
    }

    public void setDescription(char[] description) {
        this.description = description;
    }
}
