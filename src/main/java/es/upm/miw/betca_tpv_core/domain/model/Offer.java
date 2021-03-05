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
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {
    @NotBlank
    private String reference;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
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
}
