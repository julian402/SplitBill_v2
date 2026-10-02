package ue.edu.co.splitbill.dto;

import jakarta.validation.constraints.NotBlank;

// Correo y contrasena que manda la app para iniciar sesion
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") String email,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
