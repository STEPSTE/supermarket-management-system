package com.boutique.service;

import com.boutique.dto.request.CreateUserRequest;
import com.boutique.dto.request.UpdateUserRequest;
import com.boutique.dto.request.UpdateUserStatusRequest;
import com.boutique.exception.BusinessException;
import com.boutique.exception.ResourceNotFoundException;
import com.boutique.model.Role;
import com.boutique.model.User;
import com.boutique.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException("Email déjà utilisé: " + request.email());
        });
        User user = new User(request.name(), request.email(), request.role());
        User saved = userRepository.save(user);
        emailService.sendWelcomeEmail(saved);
        return saved;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + id));
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> findByActive(Boolean active) {
        return userRepository.findByActive(active);
    }

    public User update(Long id, UpdateUserRequest request) {
        User user = findById(id);
        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.role() != null) user.setRole(request.role());
        return userRepository.save(user);
    }

    public User toggleStatus(Long id, UpdateUserStatusRequest request) {
        User user = findById(id);
        user.setActive(request.active());
        User saved = userRepository.save(user);
        emailService.sendAccountStatusChange(saved);
        return saved;
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé: " + id);
        }
        userRepository.deleteById(id);
    }
}