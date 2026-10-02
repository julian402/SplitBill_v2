package ue.edu.co.splitbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import ue.edu.co.splitbill.entity.Group;

// Consultas a la tabla groups, Spring las arma con el nombre del metodo
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Grupos activos de un usuario, el mas nuevo primero
    List<Group> findByOwnerIdAndStatusOrderByIdDesc(Long ownerId, Integer status);

    // Trae el grupo solo si no esta borrado
    Optional<Group> findByIdAndStatus(Long id, Integer status);
}
