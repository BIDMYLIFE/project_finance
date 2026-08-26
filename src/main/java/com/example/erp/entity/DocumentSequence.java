package com.example.erp.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity @Table(name="document_sequences", schema="SOUTHWND")
public class DocumentSequence {
    @EmbeddedId private Key id; @Column(name="next_value", nullable=false) private int nextValue;
    protected DocumentSequence() {} public DocumentSequence(Key id){this.id=id;this.nextValue=1;} public int reserveValue(){return nextValue++;} public Key getId(){return id;}
    @Embeddable public record Key(UUID organizationId, int sequenceYear, String documentType) implements Serializable {}
}
