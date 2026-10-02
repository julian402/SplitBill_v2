package ue.edu.co.splitbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import ue.edu.co.splitbill.entity.Expense;

// Consultas a la tabla expenses
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Gastos activos del grupo, los mas recientes primero
    List<Expense> findByGroupIdAndStatusOrderByDateDescIdDesc(Long groupId, Integer status);

    // Trae el gasto solo si no esta borrado
    Optional<Expense> findByIdAndStatus(Long id, Integer status);

    // Para saber si un integrante ya pago algo (y asi no dejar borrarlo)
    boolean existsByPayerIdAndStatus(Long payerId, Integer status);

    // Total gastado en el grupo (0 si no hay gastos)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.groupId = :groupId AND e.status = 1")
    long sumActiveAmountByGroupId(@Param("groupId") Long groupId);
}
