package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferListDto {
    private String reference;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    @PositiveBigDecimal
    private BigDecimal discount;

    public OfferListDto(Offer offer) {
        this.reference = offer.getReference();
        this.description = offer.getDescription();
        this.expiryDate = offer.getExpiryDate();
        this.discount = offer.getDiscount();
    }
}