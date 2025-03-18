package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    private String reference;
    @NotBlank
    private String description;
    @NotBlank
    private String providerCompany;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime openingDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closingDate;

    private List<OrderLine> orderLinesList;

    public void doDefault() {
        this.reference = UUIDBase64.URL.encode();

        if (Objects.isNull(orderLinesList)) {
            this.orderLinesList = new ArrayList<>();
        }
    }

    public static Order ofReferenceDescriptionCompanyOpeningDateClosingDate(Order order) {
        return Order.builder()
                .reference(order.getReference())
                .description(order.getDescription())
                .providerCompany(order.getProviderCompany())
                .openingDate(order.getOpeningDate())
                .closingDate(order.getClosingDate())
                .orderLinesList(order.getOrderLinesList())
                .build();
    }

}
