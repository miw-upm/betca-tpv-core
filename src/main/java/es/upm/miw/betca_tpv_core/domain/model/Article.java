package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article {
    private String id;
    private String barcode;
    private String reference;
    private String description;
    private BigDecimal retailPrice;
    private Integer stock;
    private Tax tax;
    private List<Tags> tags; // Lista real de objetos
    private List<String> tagIds; // Lista auxiliar de nombres
    private LocalDateTime registrationDate;
    private Boolean discontinued;
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
        if (Objects.isNull(retailPrice)) {
            this.retailPrice = BigDecimal.ZERO;
        }
    }

    public BigDecimal getArticleBaseTax() {
        doDefault();
        return this.retailPrice
                .divide(BigDecimal.ONE.add(tax.getRate()
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getArticleTaxValue() {
        doDefault();
        return this.retailPrice.subtract(getArticleBaseTax());
    }

    public List<String> getTagIds() {
        if (tags == null) return List.of();
        return tags.stream()
                .filter(Objects::nonNull)
                .map(Tags::getName)
                .filter(Objects::nonNull)
                .toList();
    }


    public void setTags(List<String> list) {
    }
}
