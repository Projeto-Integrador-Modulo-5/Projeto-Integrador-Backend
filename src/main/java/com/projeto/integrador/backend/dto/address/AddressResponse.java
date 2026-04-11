package com.projeto.integrador.backend.dto.address;

import java.util.UUID;

public record AddressResponse(
    UUID id,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode,
    boolean isDefault
) {}
