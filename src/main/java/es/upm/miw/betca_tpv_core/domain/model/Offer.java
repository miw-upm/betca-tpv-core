package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {
    private String reference;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    @PositiveBigDecimal
    private BigDecimal discount;
    private String[] articleBarcodes;

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUIDBase64.URL.encode();
        }
        if (Objects.isNull(creationDate)) {
            this.creationDate = LocalDate.now();
        }
    }
}
