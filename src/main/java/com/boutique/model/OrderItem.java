package com.boutique.model;

import jakarta.persistence.*;

@Embeddable
public class OrderItem {
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    public OrderItem() {}

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}