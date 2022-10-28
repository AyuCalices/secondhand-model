package edu.damago.secondhand.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Embeddable
@Table(name = "Document", indexes = @Index(columnList = "discriminator"))
public class Document extends BaseEntity {

    @NotNull
    @Column(nullable = false, updatable = false, length = 64)
    private String hash;
    @Column(nullable = false, updatable = true)
    @ElementCollection
    @CollectionTable
    private String type;
    @Column(nullable = false, updatable = false)
    @ElementCollection
    @CollectionTable
    private byte[] content;

    protected Document() {}

    public String getHash() {
        return hash;
    }

    protected void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    protected void setContent(byte[] content) {
        this.content = content;
    }
}
