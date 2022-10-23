package edu.damago.secondhand.persistence;

import javax.persistence.*;

@Entity
@Table(name = "Document", indexes = {
        @Index(name = "hash", columnList = "hash", unique = true)
})
public class Document {
    @Id
    @Column(name = "documentIdentity", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documentIdentity", nullable = false)
    private BaseEntity baseEntity;

    @Column(name = "hash", nullable = false, length = 64)
    private String hash;

    @Column(name = "type", nullable = false, length = 63)
    private String type;

    @Column(name = "content", nullable = false)
    private byte[] content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
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

    public void setContent(byte[] content) {
        this.content = content;
    }

}