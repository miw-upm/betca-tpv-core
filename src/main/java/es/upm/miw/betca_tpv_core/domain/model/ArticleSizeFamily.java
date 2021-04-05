package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleSizeFamily {

    @NotBlank
    private String barcode;
    @NotBlank
    private String description;
    private String providerCompany;
    @PositiveBigDecimal
    private BigDecimal retailPrice;
    private Tax tax;
    private Integer type;
    private Map<String, Integer> size;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;

    public static ArticleSizeFamily ofBarcodeDescription(ArticleSizeFamily articleSizeFamily){
        return ArticleSizeFamily.builder()
                .barcode(articleSizeFamily.getBarcode())
                .description(articleSizeFamily.getDescription())
                .build();
    }

    public void doDefault() {
        if (Objects.isNull(tax)) {
            this.tax = Tax.GENERAL;
        }
    }

}
