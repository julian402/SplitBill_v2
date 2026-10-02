package ue.edu.co.splitbill.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Cuenta rápida: no se guarda, solo se calcula
public record QuickSplitRequest(
        @NotNull(message = "El valor de la cuenta es obligatorio") @Positive(message = "El valor debe ser mayor que cero") Long subtotal,
        @NotNull(message = "La propina es obligatoria") @Min(value = 0, message = "La propina no puede ser negativa")
        @Max(value = 100, message = "La propina no puede pasar del 100 %") Integer tipPercent,
        @NotNull(message = "El número de personas es obligatorio") @Min(value = 1, message = "Debe haber al menos una persona")
        @Max(value = 50, message = "Máximo 50 personas") Integer people) {
}
