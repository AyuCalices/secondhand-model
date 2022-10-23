-- MySQL structure script for schema "secondhand"
-- best import using client command "source <path to this file>"

SET CHARACTER SET utf8mb4;
DROP DATABASE IF EXISTS secondhand;
CREATE DATABASE secondhand CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE secondhand;

-- define tables, indices, etc.
CREATE TABLE BaseEntity (
	identity BIGINT NOT NULL AUTO_INCREMENT,
	discriminator ENUM("Document", "Person", "Offer", "Purchase") NOT NULL,
	version INTEGER NOT NULL DEFAULT 1,
	created BIGINT NOT NULL,
	modified BIGINT NOT NULL,
	PRIMARY KEY (identity),
	KEY (discriminator)
);

CREATE TABLE Document (
	documentIdentity BIGINT NOT NULL,
	hash CHAR(64) NOT NULL,
	type VARCHAR(63) NOT NULL,
	content LONGBLOB NOT NULL,
	PRIMARY KEY (documentIdentity),
	FOREIGN KEY (documentIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE,
	UNIQUE KEY (hash)
);

CREATE TABLE Person (
	personIdentity BIGINT NOT NULL,
	avatarReference BIGINT NOT NULL,
	email CHAR(128) NOT NULL,
	passwordHash CHAR(64) NOT NULL,
	groupAlias ENUM("USER", "ADMIN") NOT NULL,
	title VARCHAR(15) NULL,
	surname VARCHAR(31) NOT NULL,
	forename VARCHAR(31) NOT NULL,
	street VARCHAR(63) NOT NULL,
	city VARCHAR(63) NOT NULL,
	country VARCHAR(63) NOT NULL,
	postcode VARCHAR(15) NOT NULL,
	iban CHAR(32) NOT NULL,
	bic CHAR(11) NOT NULL,
	PRIMARY KEY (personIdentity),
	FOREIGN KEY (personIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (avatarReference) REFERENCES Document (documentIdentity) ON DELETE RESTRICT ON UPDATE CASCADE,
	UNIQUE KEY (email)
);

CREATE TABLE Purchase (
	purchaseIdentity BIGINT NOT NULL,
	buyerReference BIGINT NOT NULL,
	payed BIGINT NULL,
	departed BIGINT NULL,
	arrived BIGINT NULL,
	trackingReference VARCHAR(255) NULL,
	PRIMARY KEY (purchaseIdentity),
	FOREIGN KEY (purchaseIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (buyerReference) REFERENCES Person (personIdentity) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Offer (
	offerIdentity BIGINT NOT NULL,
	avatarReference BIGINT NOT NULL,
	sellerReference BIGINT NOT NULL,
	purchaseReference BIGINT NULL,
	category ENUM("ART", "CRAFT", "HANDICRAFT", "GOURMET", "BEAUTY", "HEALTH", "INFANT", "PET", "COMPUTER", "PHONE", "IMAGE", "AUDIO", "VIDEO", "BOOK", "COLLECTIBLE", "VEHICLE", "TOY", "INDUSTRIAL", "STATIONERY", "DOMESTIC", "GARDEN") NOT NULL,
	brand CHAR(32) NOT NULL,
	alias CHAR(64) NOT NULL,
	description VARCHAR(4090) NOT NULL,
	serial CHAR(32),
	price BIGINT NOT NULL,
	postage BIGINT NOT NULL,
	PRIMARY KEY (offerIdentity),
	FOREIGN KEY (offerIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (avatarReference) REFERENCES Document (documentIdentity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (sellerReference) REFERENCES Person (personIdentity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (purchaseReference) REFERENCES Purchase (purchaseIdentity) ON DELETE SET NULL ON UPDATE CASCADE,
	UNIQUE KEY (brand, serial),
	KEY (alias),
	KEY (category)
);

CREATE TABLE PhoneAssociation (
	personReference BIGINT NOT NULL,
	phone CHAR(16) NOT NULL,
	PRIMARY KEY (personReference, phone),
	FOREIGN KEY (personReference) REFERENCES Person (personIdentity) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE DocumentAssociation (
	entityReference BIGINT NOT NULL,
	documentReference BIGINT NOT NULL,
	PRIMARY KEY (entityReference, documentReference),
	FOREIGN KEY (documentReference) REFERENCES Document (documentIdentity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (entityReference) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE
);
