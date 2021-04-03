package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnlineOrder {
    private String reference;
    @NotNull
    private OnlineOrderState state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate deliveryDate;
    private Ticket ticket;

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUIDBase64.URL.encode();
        }
        if (Objects.isNull(deliveryDate)) {
            this.deliveryDate = LocalDate.now();
        }
    }
}