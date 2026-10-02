package ue.edu.co.splitbill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ownerId solo se usa al crear: es el usuario que inició sesión en la app
public record GroupRequest(
        @NotBlank(message = "El nombre del grupo es obligatorio") @Size(max = 80) String name,
        @Size(max = 200) String description,
        Long ownerId) {
}
