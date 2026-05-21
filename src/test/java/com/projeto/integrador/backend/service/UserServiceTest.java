package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Address;
import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.address.AddressRequest;
import com.projeto.integrador.backend.dto.address.AddressResponse;
import com.projeto.integrador.backend.dto.user.UpdatePasswordRequest;
import com.projeto.integrador.backend.dto.user.UpdateUserRequest;
import com.projeto.integrador.backend.dto.user.UserResponse;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.AddressRepository;
import com.projeto.integrador.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Victor Hugo", "victor@test.com", "hashed_pass", Role.CUSTOMER);
    }

    // ── getCurrentUser ────────────────────────────────────────────────────────

    @Test
    void getCurrentUser_shouldReturnUserResponse() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));

        UserResponse result = userService.getCurrentUser("victor@test.com");

        assertThat(result.name()).isEqualTo("Victor Hugo");
        assertThat(result.email()).isEqualTo("victor@test.com");
        assertThat(result.role()).isEqualTo("CUSTOMER");
    }

    @Test
    void getCurrentUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser("naoexiste@test.com"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateUser ────────────────────────────────────────────────────────────

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest("Victor Novo", "victor@test.com");
        UserResponse result = userService.updateUser("victor@test.com", request);

        assertThat(result.name()).isEqualTo("Victor Novo");
    }

    @Test
    void updateUser_shouldThrowWhenNewEmailAlreadyInUse() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@test.com")).thenReturn(true);

        UpdateUserRequest request = new UpdateUserRequest("Victor Hugo", "outro@test.com");
        assertThatThrownBy(() -> userService.updateUser("victor@test.com", request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Email já em uso");
    }

    // ── updatePassword ────────────────────────────────────────────────────────

    @Test
    void updatePassword_shouldEncodeAndSaveNewPassword() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current_pass", "hashed_pass")).thenReturn(true);
        when(passwordEncoder.encode("new_pass")).thenReturn("new_hashed_pass");
        when(userRepository.save(user)).thenReturn(user);

        UpdatePasswordRequest request = new UpdatePasswordRequest("current_pass", "new_pass");
        userService.updatePassword("victor@test.com", request);

        assertThat(user.getPasswordHash()).isEqualTo("new_hashed_pass");
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_shouldThrowWhenCurrentPasswordWrong() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "hashed_pass")).thenReturn(false);

        UpdatePasswordRequest request = new UpdatePasswordRequest("wrong_pass", "new_pass");
        assertThatThrownBy(() -> userService.updatePassword("victor@test.com", request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Senha atual incorreta");
    }

    // ── getAddresses ──────────────────────────────────────────────────────────

    @Test
    void getAddresses_shouldReturnUserAddresses() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        Address addr = buildAddress(user);
        when(addressRepository.findByUserId(user.getId())).thenReturn(List.of(addr));

        List<AddressResponse> result = userService.getAddresses("victor@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).street()).isEqualTo("Rua Teste");
    }

    // ── addAddress ────────────────────────────────────────────────────────────

    @Test
    void addAddress_shouldSaveAndReturnAddressResponse() {
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        Address savedAddr = buildAddress(user);
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddr);

        AddressRequest request = new AddressRequest(
            "Rua Teste", "100", null, "Bairro", "Cidade", "SP", "01310-100");
        AddressResponse result = userService.addAddress("victor@test.com", request);

        assertThat(result.street()).isEqualTo("Rua Teste");
        assertThat(result.city()).isEqualTo("Cidade");
    }

    // ── deleteAddress ─────────────────────────────────────────────────────────

    @Test
    void deleteAddress_shouldRemoveAddressWhenFound() {
        UUID addressId = UUID.randomUUID();
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        Address addr = buildAddress(user);
        when(addressRepository.findByIdAndUserId(addressId, user.getId())).thenReturn(Optional.of(addr));

        userService.deleteAddress("victor@test.com", addressId);

        verify(addressRepository).delete(addr);
    }

    @Test
    void deleteAddress_shouldThrowWhenAddressNotFound() {
        UUID addressId = UUID.randomUUID();
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUserId(addressId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteAddress("victor@test.com", addressId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Endereço não encontrado");
    }

    // ── setDefaultAddress ─────────────────────────────────────────────────────

    @Test
    void setDefaultAddress_shouldMarkAddressAsDefault() {
        UUID addressId = UUID.randomUUID();
        when(userRepository.findByEmail("victor@test.com")).thenReturn(Optional.of(user));
        Address addr = buildAddress(user);
        when(addressRepository.findByIdAndUserId(addressId, user.getId())).thenReturn(Optional.of(addr));
        when(addressRepository.save(addr)).thenReturn(addr);

        userService.setDefaultAddress("victor@test.com", addressId);

        assertThat(addr.isDefault()).isTrue();
        verify(addressRepository).clearDefaultByUserId(user.getId());
        verify(addressRepository).save(addr);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Address buildAddress(User user) {
        Address a = new Address();
        a.setUser(user);
        a.setStreet("Rua Teste");
        a.setNumber("100");
        a.setNeighborhood("Bairro");
        a.setCity("Cidade");
        a.setState("SP");
        a.setZipCode("01310-100");
        return a;
    }
}
