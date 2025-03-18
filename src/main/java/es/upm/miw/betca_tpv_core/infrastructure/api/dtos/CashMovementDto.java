package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.services.utils.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashMovementDto {
    private MovementType movementType;
    @NotNull(message = "amount field is mandatory")
    @Positive(message = "negative or zero amount are not allowed")
    private BigDecimal amount;
    @NotNull(message = "comment field is mandatory")
    @NotBlank(message = "comment field cannot be empty")
    private String comment;
}