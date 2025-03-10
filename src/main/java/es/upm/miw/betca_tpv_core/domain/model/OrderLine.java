package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderLine {
    @NotBlank
    private String articleBarcode;
    @Positive
    private Integer requiredAmount;
    @Positive
    private Integer finalAmount;

    public void doDefault() {
        if (Objects.isNull(requiredAmount)) {
            this.requiredAmount = 0;
        }

        if (Objects.isNull(finalAmount)) {
            this.finalAmount = 0;
        }
    }
}
