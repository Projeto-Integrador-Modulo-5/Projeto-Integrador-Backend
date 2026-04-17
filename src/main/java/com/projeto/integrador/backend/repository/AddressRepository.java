package com.projeto.integrador.backend.repository;

import com.projeto.integrador.backend.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserId(UUID userId);
    Optional<Address> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultByUserId(UUID userId);
}
