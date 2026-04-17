package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Address;
import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.dto.address.AddressRequest;
import com.projeto.integrador.backend.dto.address.AddressResponse;
import com.projeto.integrador.backend.dto.user.UpdatePasswordRequest;
import com.projeto.integrador.backend.dto.user.UpdateUserRequest;
import com.projeto.integrador.backend.dto.user.UserResponse;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.AddressRepository;
import com.projeto.integrador.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AddressRepository addressRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public UserResponse getCurrentUser(String email) {
        User user = findByEmail(email);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        User user = findByEmail(email);
        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já em uso");
        }
        user.setName(request.name());
        user.setEmail(request.email());
        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public void updatePassword(String email, UpdatePasswordRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Senha atual incorreta");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public List<AddressResponse> getAddresses(String email) {
        User user = findByEmail(email);
        return addressRepository.findByUserId(user.getId()).stream()
            .map(this::toAddressResponse)
            .toList();
    }

    @Transactional
    public AddressResponse addAddress(String email, AddressRequest request) {
        User user = findByEmail(email);
        Address address = buildAddress(request, user);
        address = addressRepository.save(address);
        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(String email, UUID addressId, AddressRequest request) {
        User user = findByEmail(email);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
        updateAddressFields(address, request);
        address = addressRepository.save(address);
        return toAddressResponse(address);
    }

    @Transactional
    public void deleteAddress(String email, UUID addressId) {
        User user = findByEmail(email);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(String email, UUID addressId) {
        User user = findByEmail(email);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
        addressRepository.clearDefaultByUserId(user.getId());
        address.setDefault(true);
        addressRepository.save(address);
    }

    private Address buildAddress(AddressRequest req, User user) {
        Address a = new Address();
        a.setUser(user);
        updateAddressFields(a, req);
        return a;
    }

    private void updateAddressFields(Address a, AddressRequest req) {
        a.setStreet(req.street());
        a.setNumber(req.number());
        a.setComplement(req.complement());
        a.setNeighborhood(req.neighborhood());
        a.setCity(req.city());
        a.setState(req.state());
        a.setZipCode(req.zipCode());
    }

    private AddressResponse toAddressResponse(Address a) {
        return new AddressResponse(a.getId(), a.getStreet(), a.getNumber(), a.getComplement(),
            a.getNeighborhood(), a.getCity(), a.getState(), a.getZipCode(), a.isDefault());
    }
}
