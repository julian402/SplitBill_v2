package ue.edu.co.splitbill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos para agregar o editar un integrante (el telefono es opcional)
public record MemberRequest(
        @NotBlank(message = "El nombre del integrante es obligatorio") @Size(max = 80) String name,
        @Size(max = 30) String phone) {
}
