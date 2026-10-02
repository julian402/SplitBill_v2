package ue.edu.co.splitbill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Lo que manda la app para crear o editar un gasto; las anotaciones validan antes de llegar al service
public record ExpenseRequest(
        @NotBlank(message = "La descripción es obligatoria") @Size(max = 120) String description,
        @NotNull(message = "El monto es obligatorio") @Positive(message = "El monto debe ser mayor que cero") Long amount,
        @NotNull(message = "Indica quién pagó") Long payerId) {
}
