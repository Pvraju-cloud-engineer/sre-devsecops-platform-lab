package com.example.claims;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    private UUID id;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Claim() {
        // Required by JPA when it loads an entity from the database.
    }

    public Claim(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.status = "SUBMITTED";
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
