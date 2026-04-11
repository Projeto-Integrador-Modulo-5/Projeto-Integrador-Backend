package com.projeto.integrador.backend.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
    @NotBlank String street,
    @NotBlank String number,
    String complement,
    @NotBlank String neighborhood,
    @NotBlank String city,
    @NotBlank @Size(min = 2, max = 2) String state,
    @NotBlank String zipCode
) {}
