package com.ecommerce.api.service;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.UserRole;
import com.ecommerce.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired private UserRepository repository;
    @Autowired private EmailService   emailService;   // ← NOUVEAU

    public List<User>     getAllUsers()                 { return repository.findAll(); }
    public Optional<User> getUserById(Long id)          { return repository.findById(id); }
    public Optional<User> getUserByEmail(String email)  { return repository.findByEmailIgnoreCase(email); }
    public List<User>     getUsersByRole(UserRole role) { return repository.findByRole(role); }
    public List<User>     getActiveUsers()              { return repository.findByActive(true); }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (repository.existsByEmailIgnoreCase(user.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");
        if (user.getRole() == null) user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        User saved = repository.save(user);

        // ✅ EMAIL — Bienvenue
        emailService.sendWelcomeEmail(saved);

        return saved;
    }

    public User updateUser(Long id, User updated) {
        User existing = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur #" + id + " introuvable"));
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setCity(updated.getCity());
        existing.setCountry(updated.getCountry());
        return repository.save(existing);
    }

    public User toggleUserStatus(Long id) {
        User user = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable"));
        user.setActive(!user.isActive());
        User saved = repository.save(user);

        // ✅ EMAIL — Statut changé
        emailService.sendAccountStatusEmail(saved);

        return saved;
    }

    public boolean deleteUser(Long id) {
        User user = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable"));
        String email     = user.getEmail();
        String firstName = user.getFirstName();
        repository.deleteById(id);

        // ✅ EMAIL — Compte supprimé
        emailService.sendAccountDeletedEmail(email, firstName);

        return true;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",    repository.count());
        stats.put("activeUsers",   repository.findByActive(true).size());
        stats.put("adminCount",    repository.countByRole(UserRole.ADMIN));
        stats.put("sellerCount",   repository.countByRole(UserRole.SELLER));
        stats.put("customerCount", repository.countByRole(UserRole.CUSTOMER));
        return stats;
    }
}