package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.ListNotEmpty;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    @ListNotEmpty
    private List<String> articleBarcodeList;

    public static Offer ofReferenceDescription(Offer offer) {
        return Offer.builder()
                .reference(offer.getReference())
                .description(offer.getDescription())
                .build();
    }

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUID.randomUUID().toString();
        }
        if (Objects.isNull(creationDate)) {
            this.creationDate = LocalDate.now();
        }
    }
}
