package com.billgenpro.model;

public enum InvoiceStatus {
    PENDING("Pending", "#f59e0b"),
    PAID("Paid", "#10b981"),
    OVERDUE("Overdue", "#ef4444");

    private final String displayName;
    private final String color;

    InvoiceStatus(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}

