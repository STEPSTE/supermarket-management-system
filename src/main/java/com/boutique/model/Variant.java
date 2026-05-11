package com.boutique.model;

import jakarta.persistence.*;

@Embeddable
public class Variant {
    
    @Column(name = "variant_type")
    private String type;
    
    @Column(name = "variant_value")
    private String value;
    
    @Column(name = "variant_stock")
    private Integer stock;
    
    @Column(name = "variant_price")
    private Double price;
    
    @Column(name = "variant_available")
    private Boolean available = true;

    public Variant() {}
    public Variant(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}