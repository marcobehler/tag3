package com.marcobehler.resourceserver;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Transaction {

    public Integer id;

    public String description;

    public Integer amount;

    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date occurredAt;
    // timestamp etc

    public Transaction() {
    }

    public Transaction(Integer id, String description, Integer amount, Date occurredAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.occurredAt = occurredAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Date occurredAt) {
        this.occurredAt = occurredAt;
    }
}
