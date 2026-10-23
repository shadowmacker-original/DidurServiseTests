package com.example.Didur;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;

public class Product {private long id;
    private String name;
    private double price;
    private LocalDate dueDate;
    // Конструктори, гетери та сетери
    @JsonCreator
    public Product() {}

    public Product(long id, String name, double price, LocalDate dueDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
