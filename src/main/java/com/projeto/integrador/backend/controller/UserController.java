package com.projeto.integrador.backend.controller;

import com.projeto.integrador.backend.dto.address.AddressRequest;
import com.projeto.integrador.backend.dto.address.AddressResponse;
import com.projeto.integrador.backend.dto.user.UpdatePasswordRequest;
import com.projeto.integrador.backend.dto.user.UpdateUserRequest;
import com.projeto.integrador.backend.dto.user.UserResponse;
import com.projeto.integrador.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getCurrentUser(getCurrentEmail()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(getCurrentEmail(), request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(getCurrentEmail(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        return ResponseEntity.ok(userService.getAddresses(getCurrentEmail()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(201).body(userService.addAddress(getCurrentEmail(), request));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable UUID id,
                                                          @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(userService.updateAddress(getCurrentEmail(), id, request));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        userService.deleteAddress(getCurrentEmail(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/addresses/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable UUID id) {
        userService.setDefaultAddress(getCurrentEmail(), id);
        return ResponseEntity.noContent().build();
    }
}
