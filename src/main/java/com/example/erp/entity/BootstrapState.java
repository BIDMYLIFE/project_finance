package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "bootstrap_state", schema = "SOUTHWND")
public class BootstrapState {
    @Id @Column(name = "state_key", length = 50) private String stateKey;
    @Column(nullable = false) private boolean initialized;
    @Column(name = "initialized_at") private Instant initializedAt;
    protected BootstrapState() {}
    public BootstrapState(String stateKey) { this.stateKey = stateKey; }
    public boolean isInitialized() { return initialized; }
    public void initialize(Instant at) { initialized = true; initializedAt = at; }
}