package com.boutique.controller;

import com.boutique.dto.ApiResponse;
import com.boutique.dto.request.CreateUserRequest;
import com.boutique.dto.request.UpdateUserRequest;
import com.boutique.dto.request.UpdateUserStatusRequest;
import com.boutique.model.Role;
import com.boutique.model.User;
import com.boutique.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.createUser(request), "Utilisateur créé avec succès"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.findById(id)));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> findByRole(@PathVariable Role role) {
        return ResponseEntity.ok(ApiResponse.success(userService.findByRole(role)));
    }

    @GetMapping("/active/{active}")
    public ResponseEntity<ApiResponse<List<User>>> findByActive(@PathVariable Boolean active) {
        return ResponseEntity.ok(ApiResponse.success(userService.findByActive(active)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.update(id, request), "Utilisateur mis à jour"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<User>> toggleStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.toggleStatus(id, request), "Statut mis à jour"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Utilisateur supprimé"));
    }
}