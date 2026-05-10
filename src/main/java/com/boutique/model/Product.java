package com.boutique.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ElementCollection
    @CollectionTable(name = "product_photos", joinColumns = @JoinColumn(name = "product_id"))
    private List<Photo> photos = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "product_variants", joinColumns = @JoinColumn(name = "product_id"))
    private List<Variant> variants = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "product_comments", joinColumns = @JoinColumn(name = "product_id"))
    private List<Comment> comments = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<Photo> getPhotos() { return photos; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }
    public List<Variant> getVariants() { return variants; }
    public void setVariants(List<Variant> variants) { this.variants = variants; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}