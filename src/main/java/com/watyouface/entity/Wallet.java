package com.watyouface.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NB: en DB ton champ est nullable, mais en Java on force un default
    @Column(nullable = false)
    private Double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @JsonIgnore
    private User user;

    public void debit(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        if (balance == null) balance = 0.0;
        if (balance < amount) {
            throw new IllegalStateException("Solde insuffisant");
        }
        balance -= amount;
    }

    public void credit(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        if (balance == null) balance = 0.0;
        balance += amount;
    }

    public Long getId() { return id; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
