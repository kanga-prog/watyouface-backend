package com.watyouface.entity;

import jakarta.persistence.*;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public void debit(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        if (balance < amount) {
            throw new IllegalStateException("Solde insuffisant");
        }
        balance -= amount;
    }

    public void credit(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        balance += amount;
    }

    public Long getId() { return id; }
    public Double getBalance() { return balance; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
