package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String reference;
    private String description;
    @NotBlank
    private String providerCompany;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime openingDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closingDate;

    public OrderDto(Order order) {
        this.reference = order.getReference();
        this.providerCompany = order.getProviderCompany();
        this.description = order.getDescription();
        this.openingDate = order.getOpeningDate();
        this.closingDate = order.getClosingDate();
    }

}
