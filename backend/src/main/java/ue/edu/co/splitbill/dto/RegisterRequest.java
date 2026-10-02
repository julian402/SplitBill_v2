package ue.edu.co.splitbill.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos que manda la app para crear una cuenta
public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 80) String name,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password) {
}
