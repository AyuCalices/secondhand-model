package edu.htw.secondhand.persistence;

import edu.htw.secondhand.util.HashCodes;
import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;
import org.eclipse.persistence.annotations.CacheIndex;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(schema = "secondhand", name = "Document")
@PrimaryKeyJoinColumn(name = "documentIdentity")
@DiscriminatorValue(value = "Document")
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public class Document extends BaseEntity {

    @NotNull @Size(min = 64, max = 64)
    @Column(nullable = false, updatable = false, insertable = true, length = 64, unique = true)
    @CacheIndex(updateable = false)
    private String hash;

    @NotNull @Size(max = 63)
    @Column(nullable = false, updatable = true, length = 63)
    private String type;

    @NotNull
    @Column(nullable = false, updatable = false, insertable = true, length = Integer.MAX_VALUE)
    private byte[] content;

    protected Document() {}
    public Document(byte[] content) {
        this.content = content;
        this.hash = HashCodes.sha2HashText(256, content);
        this.type = "application/octet-stream";
    }

    @JsonbProperty
    public String getHash() {
        return hash;
    }

    protected void setHash(String hash) {
        this.hash = hash;
    }

    @JsonbProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonbTransient
    public byte[] getContent() {
        return content;
    }

    protected void setContent(byte[] content) {
        this.content = content;
    }
}
