package ue.edu.co.splitbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import ue.edu.co.splitbill.entity.Member;

// Consultas a la tabla members
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Integrantes activos del grupo, en el orden en que se agregaron
    List<Member> findByGroupIdAndStatusOrderByIdAsc(Long groupId, Integer status);

    // Trae el integrante solo si no esta borrado
    Optional<Member> findByIdAndStatus(Long id, Integer status);

    // Cuantos integrantes activos tiene el grupo
    long countByGroupIdAndStatus(Long groupId, Integer status);
}
