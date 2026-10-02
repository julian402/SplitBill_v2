package ue.edu.co.splitbill.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

import ue.edu.co.splitbill.dto.LoginRequest;
import ue.edu.co.splitbill.dto.RegisterRequest;
import ue.edu.co.splitbill.dto.UserResponse;
import ue.edu.co.splitbill.entity.User;
import ue.edu.co.splitbill.exception.ApiException;
import ue.edu.co.splitbill.repository.UserRepository;

// Registro e inicio de sesion, aqui es donde se usa BCrypt
@Service
public class AuthService {

    private static final int STATUS_ACTIVE = 1;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Crea la cuenta si el correo no esta usado
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (this.userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo");
        }
        // Se guarda el hash, no la contrasena
        String hash = this.passwordEncoder.encode(request.password());
        User user = this.userRepository.save(new User(request.name().trim(), email, hash));
        return toResponse(user);
    }

    // Revisa correo y contrasena; si algo no cuadra responde 401
    public UserResponse login(LoginRequest request) {
        User user = this.userRepository.findByEmailAndStatus(normalizeEmail(request.email()), STATUS_ACTIVE)
                .orElse(null);
        // BCrypt compara la contrasena escrita con el hash guardado
        if (user == null || !this.passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
        return toResponse(user);
    }

    // Lo usan otros services para revisar que el usuario exista
    public User findActive(Long userId) {
        return this.userRepository.findById(userId)
                .filter(user -> user.getStatus() == STATUS_ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El usuario no existe"));
    }

    // Quita espacios y pasa a minusculas, asi Ana@ y ana@ son el mismo correo
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Pasa la entidad al DTO, sin la contrasena
    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
