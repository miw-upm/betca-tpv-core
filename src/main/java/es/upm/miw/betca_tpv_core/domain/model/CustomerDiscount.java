package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDiscount {
    @NotBlank
    private String id;
    private String note;
    private LocalDateTime registrationDate;
    @NotBlank
    private Double discount;
    private Double minimumPurchase;
    @NotBlank
    private String user;

    public static CustomerDiscount ofUser(CustomerDiscount customerDiscount) {
        return CustomerDiscount.builder()
                .user(customerDiscount.getUser())
                .build();
    }
}