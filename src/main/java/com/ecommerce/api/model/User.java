package com.ecommerce.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CUSTOMER;

    private boolean active = true;
    private String  address;
    private String  city;
    private String  country;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public String getFullName() { return firstName + " " + lastName; }
}