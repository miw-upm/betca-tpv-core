package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditSale {
    private String reference;
    @NotBlank
    private String ticketReference;
    private Boolean payed;

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUID.randomUUID().toString();
        }
        if (Objects.isNull(payed)) {
            this.payed = false;
        }
    }
}
