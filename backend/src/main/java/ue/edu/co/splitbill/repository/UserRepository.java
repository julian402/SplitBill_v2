package ue.edu.co.splitbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import ue.edu.co.splitbill.entity.User;

// Spring arma la consulta a partir del nombre del metodo: findByEmailAndStatus -> WHERE use_email = ? AND use_status = ?
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca la cuenta activa con ese correo (para el login)
    Optional<User> findByEmailAndStatus(String email, Integer status);

    // Para no dejar registrar dos cuentas con el mismo correo
    boolean existsByEmail(String email);
}
