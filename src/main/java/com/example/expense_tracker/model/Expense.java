package com.example.expense_tracker.model;

import jakarta.persistence.*;

@Entity
@Table
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String description;
    private double amount;

    public Expense() {}

    public Expense(String category, String description, double amount) {
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }


    public String getDescription() {
        return description;
    }


    public double getAmount() {
        return amount;
    }

}