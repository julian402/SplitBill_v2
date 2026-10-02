package ue.edu.co.splitbill.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ue.edu.co.splitbill.dto.LoginRequest;
import ue.edu.co.splitbill.dto.RegisterRequest;
import ue.edu.co.splitbill.dto.UserResponse;
import ue.edu.co.splitbill.service.AuthService;

// Endpoints de registro e inicio de sesion (/api/auth)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register: responde 201 con el usuario creado
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return this.authService.register(request);
    }

    // POST /api/auth/login: devuelve el usuario si la contrasena coincide
    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return this.authService.login(request);
    }
}
