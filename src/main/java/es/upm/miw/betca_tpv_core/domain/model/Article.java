package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article {
    @NotBlank
    private String barcode;
    @NotBlank
    private String description;
    @PositiveBigDecimal
    private BigDecimal retailPrice;
    private String reference;
    private Integer stock;
    private Tax tax;
    private Boolean discontinued;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // pattern="dd/MM/yyyy hh:mm" o iso = DateTimeFormat.ISO.TIME
    private LocalDateTime registrationDate;
    private String providerCompany;

    public static Article ofBarcodeDescriptionStock(Article article) {
        return Article.builder()
                .barcode(article.getBarcode())
                .description(article.getDescription())
                .stock(article.getStock())
                .build();
    }

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUID.randomUUID().toString();
        }
        if (Objects.isNull(stock)) {
            this.stock = 10;
        }
        if (Objects.isNull(tax)) {
            this.tax = Tax.GENERAL;
        }
        if (Objects.isNull(discontinued)) {
            this.discontinued = false;
        }
    }

    public String getBarcode(){
        return this.barcode;
    }

}
