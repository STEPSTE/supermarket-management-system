package com.boutique.repository;

import com.boutique.model.Role;
import com.boutique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByActive(Boolean active);
    List<User> findByRoleAndActive(Role role, Boolean active);
}