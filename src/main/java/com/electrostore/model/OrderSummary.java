package com.electrostore.model;

import java.time.LocalDateTime;

public class OrderSummary {
    private final int orderId;
    private final String customerName;
    private final double totalAmount;
    private final LocalDateTime createdAt;

    public OrderSummary(int orderId, String customerName, double totalAmount, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}