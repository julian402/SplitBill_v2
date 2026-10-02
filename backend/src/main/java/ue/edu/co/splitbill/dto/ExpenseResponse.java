package ue.edu.co.splitbill.dto;

import java.time.LocalDate;

// perPerson: cuánto le toca a cada integrante de este gasto (lo calcula el servidor)
public record ExpenseResponse(Long id, Long groupId, String description, long amount, Long payerId,
                              String payerName, LocalDate date, long perPerson) {
}
