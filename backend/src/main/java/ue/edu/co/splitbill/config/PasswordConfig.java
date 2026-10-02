package ue.edu.co.splitbill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Configuracion para tener BCrypt disponible y poder inyectarlo en AuthService
@Configuration
public class PasswordConfig {

    // BCrypt guarda la contrasena como un hash con sal: ni siquiera quien vea la base puede leerla
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
