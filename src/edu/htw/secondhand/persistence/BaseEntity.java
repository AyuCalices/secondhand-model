package edu.htw.secondhand.persistence;

import java.util.HashSet;
import java.util.Set;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import edu.htw.secondhand.util.JsonProtectedPropertyStrategy;


/**
 * This abstract class defines entities as the root of an inheritance tree. Having a common
 * root entity class allows for the unique generation of primary keys across all subclasses,
 * and additionally for both polymorphic relationships and polymorphic queries.
 */
@Entity
@Table(schema = "secondhand", name = "BaseEntity", indexes = @Index(columnList = "discriminator"))
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="discriminator")
@JsonbVisibility(JsonProtectedPropertyStrategy.class)
public abstract class BaseEntity implements Comparable<BaseEntity> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long identity;

	@Positive
//	@Version
	@Column(nullable = false, updatable = true)
	private int version;

	@Column(nullable = false, updatable = false, insertable = true)
	private long created;

	@Column(nullable = false, updatable = true)
	private long modified;

	@NotNull @Size(max = 10)
	@ManyToMany
	@JoinTable(
		schema = "secondhand",
		name = "DocumentAssociation",
		joinColumns = @JoinColumn(nullable = false, updatable = false, insertable = true, name = "entityReference"),
		inverseJoinColumns = @JoinColumn(nullable = false, updatable = false, insertable = true, name = "documentReference"),
		uniqueConstraints = @UniqueConstraint(columnNames = { "entityReference", "documentReference" })
	)
	private Set<Document> illustrations;


	/**
	 * Initializes a new instance.
	 */
	public BaseEntity () {
		this.version = 1;
		this.created = System.currentTimeMillis();
		this.modified = System.currentTimeMillis();
		this.illustrations = new HashSet<>();
	}


	@JsonbProperty
	public long getIdentity () {
		return this.identity;
	}


	protected void setIdentity (final long identity) {
		this.identity = identity;
	}


	@JsonbProperty
	public int getVersion () {
		return this.version;
	}


	public void setVersion (final int version) {
		this.version = version;
	}


	@JsonbProperty
	public long getCreated () {
		return this.created;
	}


	protected void setCreated (final long created) {
		this.created = created;
	}


	@JsonbProperty
	public long getModified () {
		return this.modified;
	}


	public void setModified (final long modified) {
		this.modified = modified;
	}


	@JsonbProperty
	protected long[] getIllustrationReferences () {
		return this.illustrations.stream().mapToLong(Document::getIdentity).sorted().toArray();
	}


	@JsonbTransient
	public Set<Document> getIllustrations () {
		return this.illustrations;
	}


	protected void setIllustrations (final Set<Document> illustrations) {
		this.illustrations = illustrations;
	}


	@Override
	public int compareTo (final BaseEntity other) {
		return Long.compare(this.identity, other.identity);
	}
}