package ue.edu.co.splitbill.dto;

// Lo que se devuelve de un usuario: nunca la contraseña
public record UserResponse(Long id, String name, String email) {
}
