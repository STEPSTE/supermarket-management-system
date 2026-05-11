package com.boutique.service;

import com.boutique.dto.request.CreateUserRequest;
import com.boutique.dto.request.UpdateUserRequest;
import com.boutique.exception.BusinessException;
import com.boutique.exception.ResourceNotFoundException;
import com.boutique.model.User;
import com.boutique.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException("Email déjà utilisé: " + request.email());
        });
        User user = new User(request.name(), request.email(), request.role());
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + id));
    }

    public User update(Long id, UpdateUserRequest request) {
        User user = findById(id);
        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.role() != null) user.setRole(request.role());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé: " + id);
        }
        userRepository.deleteById(id);
    }
}